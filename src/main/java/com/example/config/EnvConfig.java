package com.example.config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:config.properties"})
public interface EnvConfig extends Config {
    @Key("baseUrl")
    String baseUrl();

    @Key("username")
    String userName();

    @Key("password")
    String password();
}
