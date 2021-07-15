package com.kakao.test1.service;

import com.kakao.test1.exception.BadRequestException;
import com.kakao.test1.exception.InvalidK8sContextException;
import com.kakao.test1.model.BoatApp;
import com.kakao.test1.model.BoatAppService;
import com.kakao.test1.service.kubernetes.KubeAPI;
import com.kakao.test1.service.kubernetes.KubernetesProxyService;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BoatDashBoardApi {

    private static final String clusterName ="media-dev-ay1";
    private static final String DEFAULT_NAMESPACE = "kraken";

    private final KubernetesProxyService kubernetesProxyService;

    public BoatDashBoardApi(KubernetesProxyService kubernetesProxyService) {
        this.kubernetesProxyService = kubernetesProxyService;
    }

    // 삭제: context를 한번 get -> api메소드 사용시마다 proxy를 통해 get하도록 변경. //
//    @PostConstruct
//    public void init() throws InvalidK8sContextException {
//        final String secretPathFormat = "secret/media/kraken-dkosv3/%s";
//        final String contextName = getContextName(clusterName);
//        this.dkosv3ContextSecret = VaultAuthenticatorFactory.appRole("media")
//                .read(String.format(secretPathFormat, getContextName(clusterName)), Dkosv3ContextSecret.class)
//                .orElse(null);
//        if (dkosv3ContextSecret == null) {
//            // 데이터가 없을 경우 에러 발생
//            throw new InvalidK8sContextException(clusterName);
//        }
//    }

    private String extractBackendServiceNameFromIngress(ExtensionsV1beta1Ingress ingress) {
        /**  중간에 null return시, empty list exception 처리 / 해당 경우가 존재하는지? */
        return ingress.getSpec().getRules().get(0).getHttp().getPaths().get(0).getBackend().getServiceName();
    }


    private BoatAppService findMatchedService(List<BoatAppService> boatAppServiceList, Map<String,String> deploymentLabels){
        return boatAppServiceList.stream()
                .filter(service -> {
                    Map<String, String> selectorMap = service.getService().getSpec().getSelector();

                    // selector가 없으면 모든 deployment에 붙는다.
                    if (selectorMap.entrySet().size() <= 0) {
                        return true;
                    } else {
                        return selectorMap.entrySet().stream()
                                .map(entry -> {
                                    String key = entry.getKey();
                                    String value = deploymentLabels.get(key);

                                    if (value == null) {
                                        return false;
                                    } else {
                                        return value.equals(entry.getValue());
                                    }
                                })
                                .reduce((r, n) -> r && n)
                                .orElse(false);
                    }
                }).findAny().orElse(null);
    }

    // get apps
    /** throw error? */
    public List<BoatApp> getBoatAppList(){
        try{
            KubeAPI kubeApi = kubernetesProxyService.getContext(getContextName(clusterName));
            CompletableFuture<List<V1Deployment>> v1DeploymentsTask = CompletableFuture.supplyAsync(() -> kubeApi.getDeploymentList());
            CompletableFuture<List<V1Service>> v1ServicesTask = CompletableFuture.supplyAsync(() -> kubeApi.getServiceList());
            CompletableFuture<List<ExtensionsV1beta1Ingress>> v1IngressTask = CompletableFuture.supplyAsync(() -> kubeApi.getIngressList());

            List<V1Deployment> v1Deployments = v1DeploymentsTask.get();
            List<V1Service> v1Services = v1ServicesTask.get();
            List<ExtensionsV1beta1Ingress> v1Ingress = v1IngressTask.get();

            // 인그레스와 서비스 매칭
            List<BoatAppService> boatAppServiceList = v1Services.parallelStream()
                    .map(v1Service -> {
                        ExtensionsV1beta1Ingress matchedIngress = v1Ingress.stream()
                                .filter(ingress -> extractBackendServiceNameFromIngress(ingress).contains(v1Service.getMetadata().getName()))
                                .findAny().orElse(null);
                        return new BoatAppService(v1Service, matchedIngress);
                    })
                    .collect(Collectors.toList());

            // 디플로이먼트와 서비스 매치
            /** deprecated..
//            return BoatAppServiceList.parallelStream()
//                    .map(boatAppService -> {
//                        Map<String, V1Deployment> matchedDeployments = findMatchedDeployment(boatAppService, v1Deployments);
//                        return new BoatApp(
//                                DEFAULT_NAMESPACE,
//                                getContextName(clusterName), dkosv3ContextSecret.getUrl(),
//                                matchedDeployments, boatAppService);
//                    })
//                    .collect(Collectors.toList());
*/
            return v1Deployments.parallelStream().
                    map(deployment -> {
                        BoatAppService boatAppService =
                                findMatchedService(boatAppServiceList,  deployment.getMetadata().getLabels());
                        return new BoatApp(
                                DEFAULT_NAMESPACE,
                                clusterName,
                                getContextName(clusterName),
                                kubeApi.getDkosv3ContextSecret().getUrl(),
                                deployment, boatAppService
                        );

                    }).collect(Collectors.toList());
        } catch (InvalidK8sContextException e) {
            throw new BadRequestException(e);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    // delete app(including all versions)
    public void deleteBoatApps(String appName){

        BoatApp selectedApp = getBoatAppList().parallelStream()
                .filter(app -> app.getName().equals(appName)).findAny().orElse(null);

        if(selectedApp ==null){
            log.error("Fail to rollBack {} {} - no app selected", appName);
        } else{
        String appLabel = selectedApp.getAppLabel();

        List<BoatApp> appsToDelete = getBoatAppList().parallelStream()
                .filter(app -> app.getAppLabel().equals(appLabel)).collect(Collectors.toList());

        appsToDelete.forEach(boatApp-> deleteBoatApp(boatApp));
        }
    }

    private void deleteBoatApp(BoatApp boatApp){

        V1Deployment deployment = boatApp.getDeployment();
        V1Service service = Optional.ofNullable(boatApp.getServiceAndIngress()).map(BoatAppService::getService).orElse(null);
        ExtensionsV1beta1Ingress ingress = Optional.ofNullable(boatApp.getServiceAndIngress()).map(BoatAppService::getIngress).orElse(null);

        try{
            KubeAPI kubeApi = kubernetesProxyService.getContext(getContextName(clusterName));

            /** todo: status handling */
            kubeApi.deleteDeployment(deployment.getMetadata().getName());
            if(service !=null){
                kubeApi.deleteService(service.getMetadata().getName());
            }
            if(ingress != null){
                kubeApi.deleteIngress(ingress.getMetadata().getName());
            }


        } catch (InvalidK8sContextException e) {
            throw new BadRequestException(e);
        }
    }

    // rollBack or restart
    public void rollBackOrRestart(String appName){
        List<BoatApp> boatAppList = getBoatAppList();
        // 비활성화할 앱
        BoatApp selectedApp = boatAppList.parallelStream()
                .filter(app -> app.getName().equals(appName)).findAny().orElse(null);
        String appLabel = selectedApp.getAppLabel();

        if(selectedApp ==null) {
            log.error("Fail to rollBack {} {} - no app selected", appName);
            return;
        }
        // 활성화할 앱
        BoatApp appOfAnotherVersion = boatAppList.parallelStream()
                .filter(app -> (!app.getName().equals(appName) && app.getAppLabel().equals(appLabel)))
                .findAny().orElse(null);
        if (appOfAnotherVersion ==null) {
            log.error("Fail to rollBack {} {} - no other version existed", appName);
            return;
        }


        try {
            KubeAPI kubeApi = kubernetesProxyService.getContext(getContextName(clusterName));

            Integer replicasDesired = selectedApp.getDeployment().getSpec().getReplicas();

            // 활성화할 버전 디플로이먼트의 인스턴스 활성화
            kubeApi.scaleDeployment(appOfAnotherVersion.getName(),replicasDesired);

            // todo: 모두 up-to-date되면 // util 사용? or wait until up-to-date

            // service selector의 version 변경
            kubeApi.patchServiceSelector(
                    selectedApp.getServiceAndIngress().getService().getMetadata().getName(),
                    appOfAnotherVersion.getVersionLabel());

            // 기존 디플로이먼트 인스턴스의 비활성화
            kubeApi.scaleDeployment(appName, 0);
;
            }catch(InvalidK8sContextException e){
                throw new BadRequestException(e);
            }

        }


    public String getShortCut(String name) {
        String indexName = "dkos_v3_"+clusterName+"-*";
        String containerName = getContainerName(name);

        /** todo: default값으로 변경? */
        return "https://kemi-kibana5.9rum.cc/app/kibana#/discover?_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now-24h,mode:quick,to:now))&_a=(columns:!(log,kubernetes.namespace,kubernetes.container_name),filters:!(('$$hashKey':'object:94','$state':(store:appState),meta:(alias:!n,disabled:!f,index:'"
                +indexName
                +"',key:cluster_name,negate:!f,value:"
                +clusterName
                +"),query:(match:(cluster_name:(query:"
                +clusterName
                +",type:phrase)))),('$$hashKey':'object:4891','$state':(store:appState),meta:(alias:!n,disabled:!f,index:'"
                +indexName
                +"',key:kubernetes.namespace,negate:!f,value:kraken),query:(match:(kubernetes.namespace:(query:kraken,type:phrase)))),('$$hashKey':'object:94','$state':(store:appState),meta:(alias:!n,disabled:!f,index:'"
                +indexName
                +"',key:kubernetes.container_name,negate:!f,value:"
                +containerName
                +"),query:(match:(kubernetes.container_name:(query:"
                +containerName
                +",type:phrase))))),index:'"
                +indexName
                +"',interval:auto,query:(query_string:(analyze_wildcard:!t,query:'*')),sort:!('@timestamp',desc))";
    }

    private String getContainerName(String name){
        return name.substring(0,name.lastIndexOf('-'));
    }

    private String getContextName(String name) {
        if (name.matches("-context$")) {
            return name;
        } else {
            return name + "-context";
        }
    }

}
