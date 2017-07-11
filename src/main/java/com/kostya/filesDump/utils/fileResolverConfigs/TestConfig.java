package com.kostya.filesDump.utils.fileResolverConfigs;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by Костя on 11.07.2017.
 */
@Configuration
@Profile("test")
@PropertySource("classpath:/properties/test.properties")
public class TestConfig {
}
