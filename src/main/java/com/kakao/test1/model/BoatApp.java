package com.kakao.test1.model;

import io.kubernetes.client.openapi.models.*;
import lombok.Getter;

import javax.annotation.Nullable;

@Getter
public class BoatApp {
    public String name;
    public String namespace;
    public String clusterName;
    public AppContext context;
    public V1Deployment deployment;
    public BoatAppService serviceAndIngress;

    public BoatApp(String namespace, String clusterName, String contextName, String contextUrl, V1Deployment deployment, @Nullable BoatAppService serviceAndIngress) {
        this.name = deployment.getMetadata().getName();
        this.namespace = namespace;
        this.clusterName = clusterName;
        this.context = new AppContext(contextName, contextUrl);
        this.deployment = deployment;
        this.serviceAndIngress = serviceAndIngress;
    }

    /** exception? */
    public String getAppLabel(){
        return deployment.getMetadata().getLabels().get("app");
    }

    public String getVersionLabel() {
        return deployment.getMetadata().getLabels().get("version");
    }

//    public boolean hasReplicas(){
//        return deployment.getSpec().getReplicas()>0;
//    }

}