// deprecated ..//

//package com.kakao.test1.model;
//
//import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
//import io.kubernetes.client.openapi.models.V1Deployment;
//import io.kubernetes.client.openapi.models.V1Service;
//import lombok.Getter;
//
//import java.util.List;
//import java.util.Map;
//
//@Getter
//public class BoatAppOrigin {
//    public String name;
//    public String namespace;
//    public AppContext context;
//    public Map<String, V1Deployment> deployments;
//    /** public String version; */
//    public BoatAppService service;
//
//    public BoatAppOrigin(String namespace, String contextName, String contextUrl, Map<String,V1Deployment> deployments, BoatAppService service) {
//        this.name = deployments.get();
//        this.namespace = namespace;
//        this.context = new AppContext(contextName, contextUrl);
//        this.deployments = deployments;
//        this.service = service;
//    }
//
//    public void rollBackOrRestart(){
//
//    }
//}
