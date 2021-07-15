package com.kakao.test1.controller;

import com.kakao.test1.model.BoatApp;
import com.kakao.test1.service.BoatDashBoardApi;
import com.kakao.test1.service.kubernetes.KubeAPI;
import io.kubernetes.client.openapi.models.V1Deployment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ShowController {

    private final BoatDashBoardApi boatDashBoardApi;

    public ShowController(BoatDashBoardApi boatDashBoardApi){
        this.boatDashBoardApi= boatDashBoardApi;
    }

    @GetMapping("/show")
    public String showDeployList(Model model){
        List<BoatApp> boatAppList = boatDashBoardApi.getBoatAppList();
        List<Map<String, String>> boatAppListWithUrl = new ArrayList<>();

        for(BoatApp item: boatAppList){
            Map<String,String> info = new HashMap<>();
            info.put("name", item.getName());
            info.put("url", boatDashBoardApi.getShortCut(item.getName()));
            boatAppListWithUrl.add(info);
        }
        model.addAttribute("appInfo", boatAppListWithUrl);
        return "showList";
    }

    @GetMapping("/show/delete/{name}")
    public String deleteBoatApp(){
        // ..
        return "";
    }

    @GetMapping("/show/rollback/{name}")
        public String rollBackBoatApp(){
            // ..
            return "";
    }


}
