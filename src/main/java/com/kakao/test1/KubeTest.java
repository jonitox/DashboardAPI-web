package com.kakao.test1;



import com.kakao.test1.model.BoatApp;
import com.kakao.test1.service.BoatDashBoardApi;
import com.kakao.test1.service.kubernetes.KubernetesProxyService;
import io.kubernetes.client.openapi.ApiException;



import java.io.IOException;
import java.util.List;

/**
 KuberneteAPI 활용 test
 */
public class KubeTest {
    public static void main(String[] args) throws IOException, ApiException {


        KubernetesProxyService kubernetesProxyService = new KubernetesProxyService();
        BoatDashBoardApi boatDashBoardApi = new BoatDashBoardApi(kubernetesProxyService);

        boatDashBoardApi.rollBackOrRestart("kraken-test1-development-master-green");

    }

}