package com.kakao.test1.model;

import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Service;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Getter
public class BoatApp {
    public String name;
    public String namespace;
    public AppContext context;
    public V1Deployment deployment;
    public BoatAppService serviceAndIngress;

    public BoatApp(String namespace, String contextName, String contextUrl, V1Deployment deployments, @Nullable BoatAppService serviceAndIngress) {
        this.name = deployments.getMetadata().getLabels().get("app");
        this.namespace = namespace;
        this.context = new AppContext(contextName, contextUrl);
        this.deployment = deployment;
        this.serviceAndIngress = serviceAndIngress;
    }

    public void rollBackOrRestart() {

    }

    public String getShortCut() {
        /** deafult로 */
        return "https://kemi-kibana5.9rum.cc/app/kibana#/discover?_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now-24h,mode:quick,to:now))&_a=(columns:!(log,kubernetes.namespace,kubernetes.pod_name),filters:!(('$$hashKey':'object:94','$state':(store:appState),meta:(alias:!n,disabled:!f,index:'"
                + "dkos_v3_media-dev-ay1-*"
                + "',key:cluster_name,negate:!f,value:"
                + "media-dev-ay1"
                + "),query:(match:(cluster_name:(query:"
                + "media-dev-ay1"
                + ",type:phrase)))),('$$hashKey':'object:4891','$state':(store:appState),meta:(alias:!n,disabled:!f,index:'"
                + "dkos_v3_media-dev-ay1-*"
                + "',key:kubernetes.namespace,negate:!f,value:kraken),query:(match:(kubernetes.namespace:(query:kraken,type:phrase)))),('$$hashKey':'object:94','$state':(store:appState),meta:(alias:!n,disabled:!f,index:'"
                +"dkos_v3_media-dev-ay1-*"
                +"',key:kubernetes.pod_name,negate:!f,value:"
                +"kraken-soda-pool-development-develop-green-69c7d9467b-jtdm6"
                +"),query:(match:(kubernetes.pod_name:(query:"
                +"kraken-soda-pool-development-develop-green-69c7d9467b-jtdm6"
                +",type:phrase))))),index:'dkos_v3_media-dev-ay1-*',interval:auto,query:(query_string:(analyze_wildcard:!t,query:'*')),sort:!('@timestamp',desc))";
    }
}