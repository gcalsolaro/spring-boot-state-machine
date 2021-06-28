package com.gcalsolaro.statemachine.domain.enums.statemachine;

public enum ApplicationStates {
	
	START_ISTANZA,
	ISTANZA_PENDING,
	START_UTENTE,
	EVALUATE_UTENTE_PENDING,
	END_UTENTE_OK,
	END_UTENTE_KO,
	END_ISTANZA_OK,
	END_ISTANZA_KO
}
