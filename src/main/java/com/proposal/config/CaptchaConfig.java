package com.proposal.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

@Configuration
public class CaptchaConfig {

    @Bean
    public DefaultKaptcha producer() {

        Properties properties = new Properties();

        properties.setProperty("kaptcha.textproducer.char.length","5");
        properties.setProperty("kaptcha.image.width","180");
        properties.setProperty("kaptcha.image.height","60");
        properties.setProperty("kaptcha.textproducer.font.size","40");
        properties.setProperty("kaptcha.noise.impl",
                "com.google.code.kaptcha.impl.DefaultNoise");

        Config config = new Config(properties);

        DefaultKaptcha captcha =
                new DefaultKaptcha();

        captcha.setConfig(config);

        return captcha;
    }

}