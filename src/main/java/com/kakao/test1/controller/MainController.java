package com.kakao.test1.controller;

import com.kakao.test1.exception.InvalidK8sContextException;
import com.kakao.test1.service.dkos.KubernetesProxyService;
import com.kakao.test1.service.dkos.kubernetes.KubernetesApi;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @GetMapping("/home")
    public String home(){
        return "test3";
    }


}
