package com.valcare.parkinglot.config;

import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.config.SdkConfig;
import com.infisical.sdk.util.InfisicalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
//import com.infisical.sdk.schema.AuthenticationOptions;

@Service
//@Profile("infisical")
public class InfisicalConfigurationDBService implements ConfigurationDBService {


        @Value("${infisical.clientId}")
        private String clientId;

        @Value("${infisical.clientSecret}")
        private String clientSecret;

        @Value("${infisical.projectId}")
        private String projectId;

        @Value("${infisical.siteUrl:https://app.infisical.com}")
        private String siteUrl;

        private String dbUrl;
        private String dbUser;
        private String dbPassword;

        private InfisicalSdk sdk;

        //@PostConstruct
        public void initializeSecrets() {
            try {
                System.out.println("🔐 Loading secrets from Infisical… for InfisicalConfigurationDBService class");

                var sdk = new InfisicalSdk(
                        new SdkConfig.Builder()
                                .withSiteUrl(siteUrl)
                                .build()
                );

                // Authenticate using Universal Auth
                sdk.Auth().UniversalAuthLogin(clientId, clientSecret);
                System.out.println(clientId+"   "+clientSecret);

                // Load individual secrets
                this.dbUrl = sdk.Secrets().GetSecret("dburl", projectId, "dev", null, null, null, null).getSecretValue();
                this.dbUser = sdk.Secrets().GetSecret("dbuser", projectId, "dev", null, null, null, null).getSecretValue();
                this.dbPassword = sdk.Secrets().GetSecret("dbpassword", projectId, "dev", null, null, null, null).getSecretValue();


                System.out.println(dbUrl+" "+dbPassword+"  "+dbUser);


                System.out.println("✅ Secrets successfully loaded from Infisical.");

            } catch (InfisicalException e) {
                System.err.println("❌ Failed to load secrets from Infisical:");
                e.printStackTrace();
                throw new RuntimeException("Unable to initialize Infisical secrets", e);
            }
        }


    @Override
    public ConfigurationDB accessConfigurationDB() throws InfisicalException {
        ConfigurationDB config = new ConfigurationDB();
        config.setDbUrl(dbUrl);
        config.setDbUserName(dbUser);
        config.setDbPassword(dbPassword);
        return config;
    }
}












