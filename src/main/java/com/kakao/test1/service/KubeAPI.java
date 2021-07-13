package com.kakao.test1.service;

import com.kakao.kraken.vault.VaultAuthenticatorFactory;
import com.kakao.test1.config.Dkosv3ContextSecret;
import com.kakao.test1.exception.BadRequestException;
import com.kakao.test1.exception.InvalidK8sContextException;
import com.kakao.test1.model.BoatApp;
import com.kakao.test1.model.BoatAppService;
import com.kakao.test1.service.dkos.kubernetes.KubernetesApi;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
public class KubeAPI {

    private Dkosv3ContextSecret dkosv3ContextSecret;
    private static final String DEFAULT_NAMESPACE = "kraken";

    public KubeAPI(Dkosv3ContextSecret dkosv3ContextSecret){
        this.dkosv3ContextSecret = dkosv3ContextSecret;
    }

    public Dkosv3ContextSecret getDkosv3ContextSecret() {
        return dkosv3ContextSecret;
    }

    public List<V1Deployment> getDeploymentList() {
        ApiClient apiClient = Config.fromToken(dkosv3ContextSecret.getUrl(), dkosv3ContextSecret.getToken(), false);
        AppsV1Api api = new AppsV1Api(apiClient);

        try {
            V1DeploymentList list = api.listNamespacedDeployment(DEFAULT_NAMESPACE, "false", false, null, null, null, 999, null, null, false);

            if(list==null){
                return Collections.emptyList();
            } else {
                List<V1Deployment> ret = new ArrayList<V1Deployment>();
                for(V1Deployment item: list.getItems()){
                        V1ObjectMeta metaData = item.getMetadata();
                        Map<String, String> annotations = metaData.getAnnotations();
                        // boat의 디플로이먼트만 ?
                        if(annotations.containsKey("kubernetes.io/change-cause")){
                            if(annotations.get("kubernetes.io/change-cause").contains("boat")){
                                ret.add(item);
                            }
                        }
                }
                return ret;
            }
        } catch (ApiException e) {
            log.error(e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    public List<V1Service> getServiceList() {

        ApiClient apiClient = Config.fromToken(dkosv3ContextSecret.getUrl(), dkosv3ContextSecret.getToken(), false);
        CoreV1Api api = new CoreV1Api(apiClient);

        try {
            V1ServiceList list = api.listNamespacedService(DEFAULT_NAMESPACE, "false", false, null, null, null, 999, null, null, false);
            if(list==null){
                return Collections.emptyList();
            } else {
                return list.getItems();
            }
        } catch (ApiException e) {
            log.error(e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    public List<ExtensionsV1beta1Ingress> getIngressList() {

        ApiClient apiClient = Config.fromToken(dkosv3ContextSecret.getUrl(), dkosv3ContextSecret.getToken(), false);
        ExtensionsV1beta1Api api = new ExtensionsV1beta1Api(apiClient);

        try {
            ExtensionsV1beta1IngressList list = api.listNamespacedIngress(DEFAULT_NAMESPACE, "false", false, null, null, null, 999, null, null, false);
            if(list==null){
                return Collections.emptyList();
            } else {
                return list.getItems();
            }
        } catch (ApiException e) {
            log.error(e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    public V1Status deleteDeployment(String name) {
        ApiClient apiClient = Config.fromToken(dkosv3ContextSecret.getUrl(), dkosv3ContextSecret.getToken(), false);
        AppsV1Api api = new AppsV1Api(apiClient);

        try {
            return api.deleteNamespacedDeployment(name, DEFAULT_NAMESPACE, "false", null, null, false, null, null);
        } catch (ApiException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public V1Status deleteIngress(String name) {
        ApiClient apiClient = Config.fromToken(dkosv3ContextSecret.getUrl(), dkosv3ContextSecret.getToken(), false);
        ExtensionsV1beta1Api api = new ExtensionsV1beta1Api(apiClient);

        try {
            return api.deleteNamespacedIngress(name, DEFAULT_NAMESPACE, "false", null, null, false, null, null);
        } catch (ApiException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public V1Status deleteService(String name) {
        ApiClient apiClient = Config.fromToken(dkosv3ContextSecret.getUrl(), dkosv3ContextSecret.getToken(), false);
        CoreV1Api api = new CoreV1Api(apiClient);

        try {
            return api.deleteNamespacedService(name, DEFAULT_NAMESPACE, "false", null, null, false, null, null);
        } catch (ApiException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
