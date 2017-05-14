package com.xebia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by artur.skrzydlo on 2017-05-13.
 */
@Configuration
@EnableJpaRepositories("com.xebia.repositories")
public class CommonBeanConfiguration {
}
