package com.valcare.parkinglot.config;

import com.infisical.sdk.util.InfisicalException;

public interface ConfigurationDBService {

    ConfigurationDB accessConfigurationDB() throws InfisicalException;
}
