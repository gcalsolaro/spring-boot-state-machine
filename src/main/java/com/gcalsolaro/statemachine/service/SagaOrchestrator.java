package com.gcalsolaro.statemachine.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import com.gcalsolaro.statemachine.domain.entity.Istanza;
import com.gcalsolaro.statemachine.domain.entity.Utente;
import com.gcalsolaro.statemachine.domain.enums.StatoIstanza;
import com.gcalsolaro.statemachine.domain.enums.StatoUtente;
import com.gcalsolaro.statemachine.domain.enums.statemachine.ApplicationEvents;
import com.gcalsolaro.statemachine.domain.enums.statemachine.ApplicationStates;

@Service
public class SagaOrchestrator {

	@Autowired
	private StateMachine<ApplicationStates, ApplicationEvents> stateMachine;

	public StateMachine<ApplicationStates, ApplicationEvents> createIstanzaSaga(boolean success) {
		this.setUpVariable(success);
		try {
			StateMachine<ApplicationStates, ApplicationEvents> sagaStateMachine = this.startSaga();

			this.fireEvent(sagaStateMachine, ApplicationEvents.CREATE_ISTANZA);
			this.fireEvent(sagaStateMachine, ApplicationEvents.CREATE_UTENTE);

			ApplicationStates subcontextState = 
					(ApplicationStates) sagaStateMachine.getExtendedState().getVariables().get("SUBCONTEXT_EVENT");

			if (subcontextState.equals(ApplicationStates.END_UTENTE_OK)) {
				this.fireEvent(sagaStateMachine, ApplicationEvents.UPDATE_ISTANZA_CREATED);
			} else if (subcontextState.equals(ApplicationStates.END_UTENTE_KO)) {
				this.fireEvent(sagaStateMachine, ApplicationEvents.UPDATE_ISTANZA_REJECTED);
			}

			return sagaStateMachine;
		} finally {
			this.stopSaga();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Transition<ApplicationStates, ApplicationEvents>> getTransitions(StateMachine<ApplicationStates, ApplicationEvents> stateMachine) {
		List<Transition<ApplicationStates, ApplicationEvents>> transitions = new ArrayList<>();

		for (Object objTrans : stateMachine.getTransitions()) {
			Transition<ApplicationStates, ApplicationEvents> transition = (Transition<ApplicationStates, ApplicationEvents>) objTrans;

			if (transition.getSource().getId().equals(stateMachine.getState().getId())) {
				transitions.add(transition);
			}
		}

		return transitions;
	}

	private void stopSaga() {
		stateMachine.stop();
	}

	private boolean fireEvent(StateMachine<ApplicationStates, ApplicationEvents> stateMachine, ApplicationEvents event) {
		return stateMachine.sendEvent(event);
	}

	private StateMachine<ApplicationStates, ApplicationEvents> startSaga() {
		stateMachine.start();
		return stateMachine;
	}

	private void setUpVariable(boolean success) {
		stateMachine.getExtendedState().getVariables().put("success", success);
		stateMachine.getExtendedState().getVariables().put("istanza", this.setUpIstanza());
		stateMachine.getExtendedState().getVariables().put("utente", this.setUpUtente());
	}

	private Istanza setUpIstanza() {
		Istanza istanza = new Istanza();
		istanza.setcIstanza("SAGA_ISTANZA_TEST");
		istanza.setInfo("test saga");
		istanza.setStato(StatoIstanza.NEW);
		return istanza;
	}

	private Utente setUpUtente() {
		Utente utente = new Utente();
		utente.setNome("Giuseppe");
		utente.setCognome("Calsolaro");
		utente.setEmail("giuseppe.calsolaro@gmail.com");
		utente.setcFiscale("CLSGPP87M28E409V");
		utente.setDtNascita(new Date());
		utente.setStato(StatoUtente.NEW);
		return utente;
	}

}
