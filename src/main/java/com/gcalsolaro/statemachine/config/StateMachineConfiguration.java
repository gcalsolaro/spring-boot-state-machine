package com.gcalsolaro.statemachine.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import com.gcalsolaro.statemachine.domain.enums.StatoIstanza;
import com.gcalsolaro.statemachine.domain.enums.StatoUtente;
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

    @Override
    public void configure(StateMachineStateConfigurer<ApplicationStates, ApplicationEvents> states) throws Exception {
        states
            .withStates()
            .initial(ApplicationStates.START_ISTANZA)
            .state(ApplicationStates.START_ISTANZA)
            .state(ApplicationStates.ISTANZA_PENDING)
            .end(ApplicationStates.END_ISTANZA_OK)
            .end(ApplicationStates.END_ISTANZA_KO)
            .and()
            .withStates()
                .parent(ApplicationStates.ISTANZA_PENDING)
                .initial(ApplicationStates.START_UTENTE)
                .stateEntry(ApplicationStates.EVALUATE_UTENTE_PENDING, sagaService.evaluateUtente())
                .end(ApplicationStates.END_UTENTE_OK)
                .end(ApplicationStates.END_UTENTE_KO);
    }

	@Override
	public void configure(StateMachineTransitionConfigurer<ApplicationStates, ApplicationEvents> transitions) throws Exception {
		transitions
			.withExternal()
				.source(ApplicationStates.START_ISTANZA)
				.target(ApplicationStates.ISTANZA_PENDING)
				.event(ApplicationEvents.CREATE_ISTANZA)
				.action(sagaService.createIstanza())
				.guard(sagaService.protectIstanzaState(StatoIstanza.NEW))
			.and().withExternal()
				.source(ApplicationStates.START_UTENTE)
				.target(ApplicationStates.EVALUATE_UTENTE_PENDING)
				.event(ApplicationEvents.CREATE_UTENTE)
				.action(sagaService.createUtente())
				.guard(sagaService.protectIstanzaState(StatoIstanza.PENDING))
				.guard(sagaService.protectUtenteState(StatoUtente.NEW))
			.and().withExternal()
				.source(ApplicationStates.EVALUATE_UTENTE_PENDING)
				.target(ApplicationStates.END_UTENTE_OK)
				.event(ApplicationEvents.UPDATE_UTENTE_CREATED)
				.action(sagaService.saveUtenteState())
			.and().withExternal()
				.source(ApplicationStates.EVALUATE_UTENTE_PENDING)
				.target(ApplicationStates.END_UTENTE_KO)
				.event(ApplicationEvents.UPDATE_UTENTE_REJECTED)
				.action(sagaService.saveUtenteState())
			.and().withExternal()
				.source(ApplicationStates.ISTANZA_PENDING)
				.target(ApplicationStates.END_ISTANZA_OK)
				.event(ApplicationEvents.UPDATE_ISTANZA_CREATED)
				.action(sagaService.updateIstanza(true))
			.and().withExternal()
				.source(ApplicationStates.ISTANZA_PENDING)
				.target(ApplicationStates.END_ISTANZA_KO)
				.event(ApplicationEvents.UPDATE_ISTANZA_REJECTED)
				.action(sagaService.updateIstanza(false));
	}

}