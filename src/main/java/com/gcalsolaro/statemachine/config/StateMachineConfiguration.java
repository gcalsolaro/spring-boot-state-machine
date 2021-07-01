package com.gcalsolaro.statemachine.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import com.gcalsolaro.statemachine.domain.enums.InstanceState;
import com.gcalsolaro.statemachine.domain.enums.UserState;
import com.gcalsolaro.statemachine.domain.enums.statemachine.ApplicationEvents;
import com.gcalsolaro.statemachine.domain.enums.statemachine.ApplicationStates;
import com.gcalsolaro.statemachine.service.SagaService;

@Configuration
@EnableStateMachine
public class StateMachineConfiguration extends StateMachineConfigurerAdapter<ApplicationStates, ApplicationEvents> {
	
	@Autowired
	private SagaService sagaService;
	
    @Override
    public void configure(StateMachineConfigurationConfigurer<ApplicationStates, ApplicationEvents> config)
            throws Exception {
        config
        	.withConfiguration()
            .autoStartup(false)
            .listener(new StateMachineListener());
    }

    /*
     * defines only entity's states
     */
    @Override
    public void configure(StateMachineStateConfigurer<ApplicationStates, ApplicationEvents> states) throws Exception {
        states
            .withStates()
            .initial(ApplicationStates.START_INSTANCE)
            .state(ApplicationStates.START_INSTANCE)
            .state(ApplicationStates.INSTANCE_PENDING)
            .end(ApplicationStates.END_INSTANCE_OK)
            .end(ApplicationStates.END_INSTANCE_KO)
            .and()
            .withStates()
                .parent(ApplicationStates.INSTANCE_PENDING)
                .initial(ApplicationStates.START_USER)
                .stateEntry(ApplicationStates.EVALUATE_USER_PENDING, sagaService.evaluateUser()) // This action is performed automatically when the entity goes into the specified state. No explicit invocation is required
                .end(ApplicationStates.END_USER_OK)
                .end(ApplicationStates.END_USER_KO);
    }

    /**
     * defines the transactions and events that lead from one state to another
     */
	@Override
	public void configure(StateMachineTransitionConfigurer<ApplicationStates, ApplicationEvents> transitions) throws Exception {
		transitions
			.withExternal()
				.source(ApplicationStates.START_INSTANCE)
				.target(ApplicationStates.INSTANCE_PENDING)
				.event(ApplicationEvents.CREATE_INSTANCE)
				.action(sagaService.createInstance()) // Action to be performed during the transition from one state to another
				.guard(sagaService.protectInstanceState(InstanceState.NEW)) // Guard interface is used to "protect" the transitions between states
			.and().withExternal()
				.source(ApplicationStates.START_USER)
				.target(ApplicationStates.EVALUATE_USER_PENDING)
				.event(ApplicationEvents.CREATE_USER)
				.action(sagaService.createUser())
				.guard(sagaService.protectInstanceState(InstanceState.PENDING))
				.guard(sagaService.protectUserState(UserState.NEW))
			.and().withExternal()
				.source(ApplicationStates.EVALUATE_USER_PENDING)
				.target(ApplicationStates.END_USER_OK)
				.event(ApplicationEvents.UPDATE_USER_CREATED)
				.action(sagaService.saveUserState())
			.and().withExternal()
				.source(ApplicationStates.EVALUATE_USER_PENDING)
				.target(ApplicationStates.END_USER_KO)
				.event(ApplicationEvents.UPDATE_USER_REJECTED)
				.action(sagaService.saveUserState())
			.and().withExternal()
				.source(ApplicationStates.INSTANCE_PENDING)
				.target(ApplicationStates.END_INSTANCE_OK)
				.event(ApplicationEvents.UPDATE_INSTANCE_CREATED)
				.action(sagaService.updateInstance(true)) // the boolean is used for example purposes to manage the success or failure of the transaction
			.and().withExternal()
				.source(ApplicationStates.INSTANCE_PENDING)
				.target(ApplicationStates.END_INSTANCE_KO)
				.event(ApplicationEvents.UPDATE_INSTANCE_REJECTED)
				.action(sagaService.updateInstance(false));
	}

}