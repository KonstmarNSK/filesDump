package com.kostya.filesDump;

import com.kostya.filesDump.configs.MainConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Костя on 13.05.2017.
 */
public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[0];
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{MainConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        ClassPathResource propertiesFile = new ClassPathResource("/properties/global.properties");

        Properties properties = new Properties();
        try {
            properties.load(propertiesFile.getInputStream());
        }catch (IOException e){
            //nop
        }

        registration.setMultipartConfig(new MultipartConfigElement(properties.getProperty("tmpStorageForMultipartFiles")));
        super.customizeRegistration(registration);
    }
}
