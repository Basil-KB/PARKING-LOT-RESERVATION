package com.valcare.parkinglot.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationDB {
    private String dbUrl;
    private String dbUserName;
    private String dbPassword;
}
