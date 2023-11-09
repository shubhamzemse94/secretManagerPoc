package com.aws.poc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements ApplicationRunner {
	
	@Value("${normalConfig}")
	String normalConfig;
	
	@Value("${secretConfig}")
	String secretConfig;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {

		System.out.println("normalConfig:"+ normalConfig+ " secretConfig:"+ secretConfig);
	}
}
