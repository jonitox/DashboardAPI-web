package com.kakao.test1.controller;

import com.kakao.test1.exception.InvalidK8sContextException;
import com.kakao.test1.service.KubeObjService;
import com.kakao.test1.service.dkos.KubernetesProxyService;
import com.kakao.test1.service.dkos.kubernetes.KubernetesApi;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ShowController {

    private final KubeObjService kubeObjService;

    public ShowController(KubeObjService kubeObjService){
        this.kubeObjService= kubeObjService;
    }

    @GetMapping("/show")
    public String showDeployList(Model model){
        List<V1Deployment> list = kubeObjService.getDeploymentList();
        List<String> deploymentNames = new ArrayList<String>();
        for(V1Deployment item: list){
            deploymentNames.add(item.getMetadata().getName());
        }
        model.addAttribute("names", deploymentNames);
        return "showList";
    }
}
