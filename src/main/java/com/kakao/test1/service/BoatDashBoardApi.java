package com.kakao.test1.service;

import com.kakao.kraken.vault.VaultAuthenticatorFactory;
import com.kakao.test1.config.Dkosv3ContextSecret;
import com.kakao.test1.exception.BadRequestException;
import com.kakao.test1.exception.InvalidK8sContextException;
import com.kakao.test1.model.BoatApp;
import com.kakao.test1.model.BoatAppService;
import com.kakao.test1.service.dkos.KubernetesProxyService;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
    //    private Dkosv3ContextSecret dkosv3ContextSecret;

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
        /** ! 중간중간 명시된 룰,서비스 등이 없을시 null, empty list exception 처리 / 해당 경우가 존재하는지? */
        return ingress.getSpec().getRules().get(0).getHttp().getPaths().get(0).getBackend().getServiceName();
    }

    // deprecated
//    private Map<String, V1Deployment> findMatchedDeployment(BoatAppService service, List<V1Deployment> v1Deployments){
//        String appValue = service.getService().getSpec().getSelector().get("app");
//        return v1Deployments.stream().filter(deployment -> {
//            String value = deployment.getMetadata().getLabels().get("app");
//            if(value==null){
//                return false;
//            } else{
//                return value.equals(appValue);
//            }
//        }).collect(Collectors.toMap(V1Deployment->V1Deployment.getMetadata().getLabels().get("version"), Function.identity()));
//    }

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
    public List<BoatApp> getBoatApps(){
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
//
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


    // delete app(with some versions)
    public void deleteBoatApps(String appName){
        List<BoatApp> selectedApps = getBoatApps().parallelStream()
                .filter(app -> app.getName().equals(appName)).collect(Collectors.toList());

        if(selectedApps.size()<=0){
            log.error("Fail to remove {} {} - no app selected", appName);
        } else{
            selectedApps.forEach(boatApp-> deleteBoatApp(boatApp));
        }
    }

    public void deleteBoatApp(BoatApp boatApp){

        V1Deployment deployment = boatApp.getDeployment();
        V1Service service = Optional.ofNullable(boatApp.getServiceAndIngress()).map(BoatAppService::getService).orElse(null);
        ExtensionsV1beta1Ingress ingress = Optional.ofNullable(boatApp.getServiceAndIngress()).map(BoatAppService::getIngress).orElse(null);

        try{
            KubeAPI kubeApi = kubernetesProxyService.getContext(getContextName(clusterName));

            /** status handling? */
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



    private String getContextName(String name) {
        if (name.matches("-context$")) {
            return name;
        } else {
            return name + "-context";
        }
    }



}
