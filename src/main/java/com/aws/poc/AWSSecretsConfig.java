package com.aws.poc;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;


@Configuration
public class AWSSecretsConfig {

	@Autowired
	private Environment environment;

	@Bean
	public SecretsManagerClient secretManger() {
		Region region = Region.AP_SOUTH_1;
		SecretsManagerClient secretsManager = SecretsManagerClient.builder()
				.credentialsProvider(DefaultCredentialsProvider.create()).region(region).build();
		Properties properties = fetchProperties();

		MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
		propertySources.addFirst(new PropertySource<String>("customPropertySource") {
			@Override
			public Object getProperty(String name) {
				System.out.println("Calling get property: " + name);
				OriginTrackedValue propValue = (OriginTrackedValue) properties.get(name);
				if (propValue!= null && propValue.getValue().toString().startsWith("aws/")) {
					String stringPropValue = propValue.getValue().toString();
					String secretNameToFetch = stringPropValue.split("aws/")[1];
					return getSecretValue(secretNameToFetch, secretsManager).orElse("");
				}
				return null;
			}
		});

		return secretsManager;
	}

	private Optional<String> getSecretValue(String secretNameOrArn, SecretsManagerClient secretsManager) {
		try {
			GetSecretValueResponse response = secretsManager
					.getSecretValue(GetSecretValueRequest.builder().secretId(secretNameOrArn).build());
			String secretValue = response.secretString();
			System.out.println("Secret Value: " + secretValue);
			return Optional.ofNullable(secretValue);
		} catch (SecretsManagerException e) {
			System.err.println("Error fetching secret: " + e.getMessage());
		}
		return Optional.ofNullable(null);
	}

	public Properties fetchProperties() {
		Properties properties = new Properties();
		for (PropertySource<?> propertySource : ((ConfigurableEnvironment) environment).getPropertySources()) {
			Object source = propertySource.getSource();
			if (source instanceof Map) {
				properties.putAll((Map) source);
			}
		}
		return properties;
	}

}
