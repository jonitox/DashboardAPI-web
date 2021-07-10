package com.kakao.test1.config;

import com.kakao.kraken.vault.VaultAuthenticatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;



@Configuration
public class DBConfig {

    @Autowired
    public DBConfig(@Value("${vault.secret.mysql}") String secretPath){
    VaultAuthenticatorFactory.appRole("media").read(secretPath, MysqlMediaSecret.class).ifPresent(data -> { System.out.println(data); });
}
}
