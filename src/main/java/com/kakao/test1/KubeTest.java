package com.kakao.test1;



import com.kakao.test1.exception.InvalidK8sContextException;
import com.kakao.test1.model.BoatApp;
import com.kakao.test1.service.BoatDashBoardApi;
import com.kakao.test1.service.KubeAPI;
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
        BoatDashBoardApi boatDashBoardApi = new BoatDashBoardApi(kubernetesProxyService);


        List<BoatApp> list = boatDashBoardApi.getBoatApps();
        for (BoatApp item : list) {
            System.out.println(item.getName());
        }
    }

}