/*
package com.valcare.parkinglot.config;



import com.infisical.sdk.util.InfisicalException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;


import javax.sql.DataSource;
import java.util.Properties;


@Slf4j
@Configuration
public class ServiceConfiguration {


   // @Autowired
    //private  ConfigurationDBService configurationDBService;

    @Autowired
    private ConfigurableEnvironment environment;


    private final ConfigurationDBService configurationDBService;
    public ServiceConfiguration(ConfigurationDBService configurationDBService) {
        this.configurationDBService = configurationDBService;
    }

    @PostConstruct
    public void setDatasourceProps() {
        try {

            ConfigurationDB secret = configurationDBService.accessConfigurationDB();
            Properties props = new Properties();
            props.put("dburl", secret.getDbUrl());
            props.put("username", secret.getDbUserName());
            props.put("password", secret.getDbPassword());

            environment.getPropertySources().addFirst(new PropertiesPropertySource("dynamicProps", props));

        } catch (Exception e) {
            throw new RuntimeException("Failed to set datasource properties", e);
        }
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        try {
            // Fetch the database secrets from Infisical
            ConfigurationDB secret = configurationDBService.accessConfigurationDB();

            String url = secret.getDbUrl();
            String username = secret.getDbUserName();
            String password = secret.getDbPassword();

            log.info("Connecting to database at {}", url);

           return DataSourceBuilder.create()
                    .url(url)
                    .username(username)
                    .password(password)
                    .build();


        } catch (InfisicalException e) {
            log.error("Failed to load database configuration from Infisical", e);
            throw new RuntimeException("Cannot create DataSource due to Infisical error", e);
        }
    }
}
*/
