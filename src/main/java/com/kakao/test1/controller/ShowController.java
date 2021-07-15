package com.kakao.test1.controller;

import com.kakao.test1.model.BoatApp;
import com.kakao.test1.service.BoatDashBoardApi;
import com.kakao.test1.service.kubernetes.KubeAPI;
import io.kubernetes.client.openapi.models.V1Deployment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ShowController {

    private final BoatDashBoardApi boatDashBoardApi;

    public ShowController(BoatDashBoardApi boatDashBoardApi){
        this.boatDashBoardApi= boatDashBoardApi;
    }

    @GetMapping("/show")
    public String showDeployList(Model model){
        List<BoatApp> list = boatDashBoardApi.getBoatAppList();
        List<String> nameList = new ArrayList<String>();
        for(BoatApp item: list){
            nameList.add(item.getName());
        }
        model.addAttribute("names", nameList);
        return "showList";
    }
}
