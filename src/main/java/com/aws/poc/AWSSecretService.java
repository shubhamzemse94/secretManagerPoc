package com.aws.poc;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

@Component
public class AWSSecretService {

	@Autowired
	private SecretsManagerClient secretsManager;

	public Optional<String> getSecretValue(String secretNameOrArn) {
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

}
