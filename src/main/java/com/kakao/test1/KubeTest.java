package com.kakao.test1;



import com.kakao.test1.exception.InvalidK8sContextException;
import com.kakao.test1.service.dkos.KubernetesProxyService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import io.kubernetes.client.openapi.models.V1Deployment;

import com.kakao.test1.service.dkos.kubernetes.KubernetesApi;
import io.kubernetes.client.openapi.models.V1ObjectMeta;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 KuberneteAPI 활용 test
 */
public class KubeTest {
    public static void main(String[] args) throws IOException, ApiException {

        KubernetesProxyService kubernetesProxyService = new KubernetesProxyService();

        try{

            KubernetesApi kubernetesApi = kubernetesProxyService.getContext("media-dev-ay1");
            List<V1Deployment> list= kubernetesApi.getDeploymentList("kraken");

            for(V1Deployment item : list){
                V1ObjectMeta metaData = item.getMetadata();
                Map<String, String> annotations = metaData.getAnnotations();
                if(annotations.containsKey("kubernetes.io/change-cause")){
                    if(annotations.get("kubernetes.io/change-cause").contains("boat")){
                        System.out.println(metaData.getName());
                    }
                }
//                System.out.println(metaData.getAnnotations());
            }

        }
        catch(InvalidK8sContextException e){
            // error handling..
        }

    }
}