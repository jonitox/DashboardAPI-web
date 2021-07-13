package com.kakao.test1.model;

import lombok.Getter;

@Getter
public class AppContext {
    public String name;
    public String url;

    public AppContext(String name, String url) {
        this.name = name;
        this.url = url;
    }
}