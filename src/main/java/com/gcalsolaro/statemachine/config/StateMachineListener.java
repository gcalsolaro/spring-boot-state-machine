package com.gcalsolaro.statemachine.config;

import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import com.gcalsolaro.statemachine.domain.enums.statemachine.ApplicationEvents;
import com.gcalsolaro.statemachine.domain.enums.statemachine.ApplicationStates;

import java.util.logging.Logger;

public class StateMachineListener extends StateMachineListenerAdapter<ApplicationStates, ApplicationEvents> {

	private static final Logger logger = Logger.getLogger(StateMachineListener.class.getName());

	@Override
	public void stateChanged(State<ApplicationStates, ApplicationEvents> from, State<ApplicationStates, ApplicationEvents> to) {
		logger.info(() -> String.format("Transitioned from %s to %s%n", from == null ? "none" : from.getId(), to.getId()));
	}
}
