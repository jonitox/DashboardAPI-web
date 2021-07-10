package com.kakao.test1.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class MysqlMediaSecret {
    private String hostname;
    private Integer port;
    private String user;
    private String password;
}