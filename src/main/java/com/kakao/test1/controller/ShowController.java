package com.kakao.test1.controller;

import com.kakao.test1.service.KubeAPI;
import io.kubernetes.client.openapi.models.V1Deployment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ShowController {

    private final KubeAPI kubeObjService;

    public ShowController(KubeAPI kubeObjService){
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
