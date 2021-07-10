package com.kakao.test1;



import com.kakao.test1.exception.InvalidK8sContextException;
import com.kakao.test1.service.dkos.KubernetesProxyService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import io.kubernetes.client.openapi.models.V1Deployment;

import com.kakao.test1.service.dkos.kubernetes.KubernetesApi;
import java.io.IOException;
import java.util.List;

/**
 * 쿠버네티스 클러스터 외부의 애플리케이션에서 Java API를 사용하는 방법에 대한 간단한 예
 *
 * <p>이것을 실행하는 가장 쉬운 방법: mvn exec:java
 * -Dexec.mainClass="io.kubernetes.client.examples.KubeConfigFileClientExample"
 *
 */
public class KubeTest {
    public static void main(String[] args) throws IOException, ApiException {

        KubernetesProxyService kubernetesProxyService = new KubernetesProxyService();

        try{

            KubernetesApi kubernetesApi = kubernetesProxyService.getContext("media-dev-ay1");
            List<ExtensionsV1beta1Ingress> list= kubernetesApi.getIngressList("kraken");

            for(ExtensionsV1beta1Ingress item : list){
                System.out.println(item.getMetadata().getName());
            }

        }
        catch(InvalidK8sContextException e){
            // error handling..
        }

    }
}