package com.kakao.test1.service.dkos.kubernetes;

import com.kakao.test1.config.Dkosv3ContextSecret;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.*;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public class KubernetesApi {
    private final Dkosv3ContextSecret dkosv3ContextSecret;

    public KubernetesApi(Dkosv3ContextSecret dkosv3ContextSecret) {
        this.dkosv3ContextSecret = dkosv3ContextSecret;
    }

    public Dkosv3ContextSecret getDkosv3ContextSecret() {
        return dkosv3ContextSecret;
    }

    public List<V1Deployment> getDeploymentList(String namespace) {
        V1DeploymentList v1DeploymentList = getDeploymentListFromK8s(namespace);

        if (v1DeploymentList == null) {
            return Collections.emptyList();
        } else {
            return v1DeploymentList.getItems();
        }
    }

    private V1DeploymentList getDeploymentListFromK8s(String namespace) {
        ApiClient apiClient = Config.fromToken(dkosv3ContextSecret.getUrl(), dkosv3ContextSecret.getToken(), false);
        AppsV1Api api = new AppsV1Api(apiClient);

        try {
            return api.listNamespacedDeployment(namespace, "false", false, null, null, null, 999, null, null, false);
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

    public List<V1Service> getServiceList(String namespace) {
        V1ServiceList v1ServiceList = getServiceListFromK8s(namespace);

        if (v1ServiceList == null) {
            return Collections.emptyList();
        } else {
            return v1ServiceList.getItems();
        }
    }

    private V1ServiceList getServiceListFromK8s(String namespace) {
        ApiClient apiClient = Config.fromToken(dkosv3ContextSecret.getUrl(), dkosv3ContextSecret.getToken(), false);
        CoreV1Api api = new CoreV1Api(apiClient);

        try {
            return api.listNamespacedService(namespace, "false", false, null, null, null, 999, null, null, false);
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

    public List<ExtensionsV1beta1Ingress> getIngressList(String namespace) {
        ExtensionsV1beta1IngressList v1IngressList = getIngressListFromK8s(namespace);

        if (v1IngressList == null) {
            return Collections.emptyList();
        } else {
            return v1IngressList.getItems();
        }
    }

    private ExtensionsV1beta1IngressList getIngressListFromK8s(String namespace) {
        ApiClient apiClient = Config.fromToken(dkosv3ContextSecret.getUrl(), dkosv3ContextSecret.getToken(), false);
        ExtensionsV1beta1Api api = new ExtensionsV1beta1Api(apiClient);

        try {
            return api.listNamespacedIngress(namespace, "false", false, null, null, null, 999, null, null, false);
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

    public V1Status deleteDeployment(String name, String namespace) {
        ApiClient apiClient = Config.fromToken(dkosv3ContextSecret.getUrl(), dkosv3ContextSecret.getToken(), false);
        AppsV1Api api = new AppsV1Api(apiClient);

        try {
            return api.deleteNamespacedDeployment(name, namespace, "false", null, null, false, null, null);
        } catch (ApiException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public V1Status deleteIngress(String name, String namespace) {
        ApiClient apiClient = Config.fromToken(dkosv3ContextSecret.getUrl(), dkosv3ContextSecret.getToken(), false);
        ExtensionsV1beta1Api api = new ExtensionsV1beta1Api(apiClient);

        try {
            return api.deleteNamespacedIngress(name, namespace, "false", null, null, false, null, null);
        } catch (ApiException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public V1Status deleteService(String name, String namespace) {
        ApiClient apiClient = Config.fromToken(dkosv3ContextSecret.getUrl(), dkosv3ContextSecret.getToken(), false);
        CoreV1Api api = new CoreV1Api(apiClient);

        try {
            return api.deleteNamespacedService(name, namespace, "false", null, null, false, null, null);
        } catch (ApiException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
