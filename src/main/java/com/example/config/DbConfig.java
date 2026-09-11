package com.example.config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:config-db.properties"})
public interface DbConfig extends Config {

    @Config.Key("db.url")
    String dbUrl();

    @Config.Key("db.user")
    String dbUser();

    @Config.Key("db.password")
    String dbPassword();
}
