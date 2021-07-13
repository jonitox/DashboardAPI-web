package com.kakao.test1.model;

import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import io.kubernetes.client.openapi.models.V1Service;
import lombok.Getter;

import java.util.List;

@Getter
public class BoatAppService {
    V1Service service;
    ExtensionsV1beta1Ingress ingress;

    public BoatAppService(V1Service service, ExtensionsV1beta1Ingress ingressList) {
        this.service = service;
        this.ingress = ingressList;
    }
}
