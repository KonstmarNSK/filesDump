package com.kostya.filesDump.configs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Костя on 13.05.2017.
 */
@Configuration
@ComponentScan(basePackageClasses = MainConfig.class)
@ComponentScan(basePackages = "com.kostya.filesDump.utils")
public class MainConfig {
}
