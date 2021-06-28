package com.gcalsolaro.statemachine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.gcalsolaro.statemachine.config.StateMachineConfiguration;

@SpringBootApplication
@Import(StateMachineConfiguration.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}