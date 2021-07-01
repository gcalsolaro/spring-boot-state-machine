package com.gcalsolaro.statemachine.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import com.gcalsolaro.statemachine.domain.entity.Instance;
import com.gcalsolaro.statemachine.domain.entity.User;
import com.gcalsolaro.statemachine.domain.enums.InstanceState;
import com.gcalsolaro.statemachine.domain.enums.UserState;
import com.gcalsolaro.statemachine.domain.enums.statemachine.ApplicationEvents;
import com.gcalsolaro.statemachine.domain.enums.statemachine.ApplicationStates;

@Service
public class SagaOrchestrator {

	@Autowired
	private StateMachine<ApplicationStates, ApplicationEvents> stateMachine;

	/**
	 * Simple SAGA Orchestrator
	 * 
	 * @param success
	 * @return
	 */
	public StateMachine<ApplicationStates, ApplicationEvents> createInstanceSaga(boolean success) {
		this.setUpVariable(success);
		try {
			StateMachine<ApplicationStates, ApplicationEvents> sagaStateMachine = this.startSaga();

			this.fireEvent(sagaStateMachine, ApplicationEvents.CREATE_INSTANCE);
			//
			// More check...
			// ...
			// More Stuff
			//
			this.fireEvent(sagaStateMachine, ApplicationEvents.CREATE_USER);

			ApplicationStates subcontextState = (ApplicationStates) sagaStateMachine.getExtendedState().getVariables().get("SUBCONTEXT_EVENT");

			if (subcontextState.equals(ApplicationStates.END_USER_OK)) {
				this.fireEvent(sagaStateMachine, ApplicationEvents.UPDATE_INSTANCE_CREATED);
			} else if (subcontextState.equals(ApplicationStates.END_USER_KO)) {
				this.fireEvent(sagaStateMachine, ApplicationEvents.UPDATE_INSTANCE_REJECTED);
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

	/**
	 * Stop statemachine
	 */
	private void stopSaga() {
		stateMachine.stop();
	}

	/**
	 * Start statemachine
	 * 
	 * @return
	 */
	private StateMachine<ApplicationStates, ApplicationEvents> startSaga() {
		stateMachine.start();
		return stateMachine;
	}

	/**
	 * Dispatch Event
	 * 
	 * @param stateMachine
	 * @param event
	 * @return
	 */
	private boolean fireEvent(StateMachine<ApplicationStates, ApplicationEvents> stateMachine, ApplicationEvents event) {
		return stateMachine.sendEvent(event);
	}

	/**
	 * Set variable for excecution
	 * 
	 * @param success
	 */
	private void setUpVariable(boolean success) {
		stateMachine.getExtendedState().getVariables().put("success", success); // only for test
		stateMachine.getExtendedState().getVariables().put("instance", this.setUpInstance());
		stateMachine.getExtendedState().getVariables().put("user", this.setUpUser());
	}

	private Instance setUpInstance() {
		Instance i = new Instance();
		i.setcInstance("SAGA_INSTANCE_TEST");
		i.setInfo("test saga");
		i.setState(InstanceState.NEW);
		return i;
	}

	private User setUpUser() {
		User u = new User();
		u.setName("Giuseppe");
		u.setSurname("Calsolaro");
		u.setEmail("giuseppe.calsolaro@gmail.com");
		u.setFiscalCode("CLSGPP87M28E409V");
		u.setDtBorn(new Date());
		u.setState(UserState.NEW);
		return u;
	}

}
