package com.gcalsolaro.statemachine.domain.enums.statemachine;

public enum ApplicationStates {
	
	START_INSTANCE,
	INSTANCE_PENDING,
	START_USER,
	EVALUATE_USER_PENDING,
	END_USER_OK,
	END_USER_KO,
	END_INSTANCE_OK,
	END_INSTANCE_KO
}
