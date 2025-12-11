package com.valcare.parkinglot.config;

import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.config.SdkConfig;
import com.infisical.sdk.util.InfisicalException;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import java.util.Properties;
import java.util.UUID;


@Data
public class InfisicalEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private String clientId;
    private String clientSecret;
    private String projectId ;
    private String siteUrl;
    private String s= UUID.randomUUID().toString();


    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            System.out.println("🔐 Loading codes from Infisical EnvironmentPostProcessor…");

            // Fetch Infisical config details
             clientId = environment.getProperty("infisical.clientId");
             clientSecret = environment.getProperty("infisical.clientSecret");
             projectId = environment.getProperty("infisical.projectId");
             siteUrl = environment.getProperty("infisical.siteUrl", "https://app.infisical.com");

            System.out.println("Infisical Config: clientId={}, siteUrl={}"+clientId+" "+siteUrl);

            // Initialize Infisical SDK
            InfisicalSdk sdk = new InfisicalSdk(new SdkConfig.Builder().withSiteUrl(siteUrl).build());

            // Attempt authentication to infisical
            sdk.Auth().UniversalAuthLogin(clientId, clientSecret);
            System.out.println("Infisical authentication successful.");

            // Fetch the secrets from infisical
            String dbUrl = sdk.Secrets().GetSecret("dburl", projectId, "dev", null, null, null, null).getSecretValue();
            String dbUser = sdk.Secrets().GetSecret("dbuser", projectId, "dev", null, null, null, null).getSecretValue();
            String dbPassword = sdk.Secrets().GetSecret("dbpassword", projectId, "dev", null, null, null, null).getSecretValue();

            if (dbUrl == null || dbUser == null || dbPassword == null) {
                System.out.println("One or more database secrets are null! dbUrl={}, dbUser={}, dbPassword={}"+dbUrl+","+dbUser+","+dbPassword);
                throw new RuntimeException("Database secrets from Infisical cannot be null");
            }

            // Add the database properties to the environment from infisical
            System.out.println("Setting database properties in environment...");
            Properties props = new Properties();
            props.put("spring.datasource.url", dbUrl);
            props.put("spring.datasource.username", dbUser);
            props.put("spring.datasource.password", dbPassword);
            //System.out.println("spring.datasource.url="+environment.getProperty("spring.datasource.url"));
            //System.out.println("spring.datasource.username="+environment.getProperty("spring.datasource.username"));
            //System.out.println("spring.datasource.password="+environment.getProperty("spring.datasource.password"));
            // Adding dynamic properties to Spring environment
            environment.getPropertySources().addFirst(new PropertiesPropertySource("infisical-db-secrets", props));

        } catch (InfisicalException e) {
            //log.error("Infisical SDK exception occurred", e);
            throw new RuntimeException("Failed to authenticate or fetch secrets from Infisical", e);
        } catch (Exception e) {
           // log.error("Failed to load secrets from Infisical", e);
            throw new RuntimeException("Failed to set datasource properties", e);
        }
        System.out.println("spring.datasource.url="+environment.getProperty("spring.datasource.url"));
        /*System.out.println("spring.datasource.username="+environment.getProperty("spring.datasource.username"));
        System.out.println("spring.datasource.password="+environment.getProperty("spring.datasource.password"));*/
    }
}
