/*
package com.valcare.parkinglot.controller;


import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.config.SdkConfig;
import com.infisical.sdk.util.InfisicalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

@Profile("infisical")

public class InfisicalController {

    public static void printData() throws InfisicalException {
        //System.out.println(env.getProperty(siteUserName)+"++inFisical");
        var sdk = new InfisicalSdk(
                new SdkConfig.Builder()
                        // Optional, will default to https://app.infisical.com
                        .withSiteUrl("https://app.infisical.com")
                        .build()
        );

        sdk.Auth().UniversalAuthLogin(
                "CLIENT_ID",
                "CLIENT_SECRET"
        );

        var secret = sdk.Secrets().GetSecret(
                "<secret-name>",
                "<project-id>",
                "<env-slug>",
                "<secret-path>",
                null, // Expand Secret References (boolean, optional)
                null, // Include Imports (boolean, optional)
                null  // Secret Type (shared/personal, defaults to shared, optional)
        );


        System.out.println(secret+" ==sample data");


        System.out.println(" inFisical");
    }

}
*/
