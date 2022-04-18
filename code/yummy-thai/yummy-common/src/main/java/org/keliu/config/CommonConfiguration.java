package org.keliu.config;

import org.keliu.common.CommonJsonMapperInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {

    @Bean
    public CommonJsonMapperInitializer commonJsonMapperInitializer() {
        return new CommonJsonMapperInitializer();

    }
}
