package com.gcalsolaro.statemachine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Service;

import com.gcalsolaro.statemachine.domain.entity.Istanza;
import com.gcalsolaro.statemachine.domain.entity.Utente;
import com.gcalsolaro.statemachine.domain.enums.StatoIstanza;
import com.gcalsolaro.statemachine.domain.enums.StatoUtente;
import com.gcalsolaro.statemachine.domain.enums.statemachine.ApplicationEvents;
import com.gcalsolaro.statemachine.domain.enums.statemachine.ApplicationStates;
import com.gcalsolaro.statemachine.repository.IstanzaRepository;
import com.gcalsolaro.statemachine.repository.UtenteRepository;

@Service
public class SagaService {

	@Autowired
	private IstanzaRepository istanzaRepository;

	@Autowired
	private UtenteRepository utenteRepository;

	public Action<ApplicationStates, ApplicationEvents> createIstanza() {
		return new Action<ApplicationStates, ApplicationEvents>() {
			@Override
			public void execute(StateContext<ApplicationStates, ApplicationEvents> context) {
				Istanza istanza = findIstanza(context.getExtendedState());
				istanza.setStato(StatoIstanza.PENDING);
				istanza = istanzaRepository.save(istanza);
				context.getExtendedState().getVariables().put("istanza", istanza);
			}
		};
	}

	public Action<ApplicationStates, ApplicationEvents> createUtente() {
		return new Action<ApplicationStates, ApplicationEvents>() {
			@Override
			public void execute(StateContext<ApplicationStates, ApplicationEvents> context) {
				Utente utente = findUtente(context.getExtendedState());
				utente.setStato(StatoUtente.CREATED);
				utente = utenteRepository.save(utente);
				context.getExtendedState().getVariables().put("utente", utente);
			}
		};
	}

	public Action<ApplicationStates, ApplicationEvents> evaluateUtente() {
		return new Action<ApplicationStates, ApplicationEvents>() {
			@Override
			public void execute(StateContext<ApplicationStates, ApplicationEvents> context) {
				Utente utente = findUtente(context.getExtendedState());
				if (utente != null) {
					if (utente.getStato().equals(StatoUtente.CREATED)) {
						boolean success = findSuccessState(context.getExtendedState());
						if (success)
							context.getStateMachine().sendEvent(ApplicationEvents.UPDATE_UTENTE_CREATED);
						else
							context.getStateMachine().sendEvent(ApplicationEvents.UPDATE_UTENTE_REJECTED);
					}
				}
			}
		};
	}

	public Action<ApplicationStates, ApplicationEvents> updateIstanza(boolean success) {
		return new Action<ApplicationStates, ApplicationEvents>() {
			@Override
			public void execute(StateContext<ApplicationStates, ApplicationEvents> context) {
				Istanza istanza = findIstanza(context.getExtendedState());

				istanza.setStato(StatoIstanza.REJECTED);

				if (success) {
					Utente utente = findUtente(context.getExtendedState());
					istanza.setUtente(utente);
					istanza.setStato(StatoIstanza.CREATED);
				}

				istanza = istanzaRepository.save(istanza);

			}
		};
	}

	public Action<ApplicationStates, ApplicationEvents> saveUtenteState() {
		return new Action<ApplicationStates, ApplicationEvents>() {
			@Override
			public void execute(StateContext<ApplicationStates, ApplicationEvents> context) {
				context.getExtendedState().getVariables().put("SUBCONTEXT_EVENT", context.getTarget().getId());
			}
		};
	}

	public Guard<ApplicationStates, ApplicationEvents> protectIstanzaState(StatoIstanza statoIstanza) {
		return new Guard<ApplicationStates, ApplicationEvents>() {
			@Override
			public boolean evaluate(StateContext<ApplicationStates, ApplicationEvents> context) {
				Istanza istanza = findIstanza(context.getExtendedState());
				if (istanza != null)
					return istanza.getStato().equals(statoIstanza);
				return false;
			}
		};
	}

	public Guard<ApplicationStates, ApplicationEvents> protectUtenteState(StatoUtente statoUtente) {
		return new Guard<ApplicationStates, ApplicationEvents>() {
			@Override
			public boolean evaluate(StateContext<ApplicationStates, ApplicationEvents> context) {
				Utente utente = findUtente(context.getExtendedState());
				if (utente != null)
					return utente.getStato().equals(statoUtente);
				return false;
			}
		};
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Internal Helper ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

	private Istanza findIstanza(ExtendedState extendedState) {
		for (Object obj : extendedState.getVariables().values()) {
			if (obj instanceof Istanza) {
				return (Istanza) obj;
			}
		}
		return null;
	}

	private Utente findUtente(ExtendedState extendedState) {
		for (Object obj : extendedState.getVariables().values()) {
			if (obj instanceof Utente) {
				return (Utente) obj;
			}
		}
		return null;
	}

	private Boolean findSuccessState(ExtendedState extendedState) {
		for (Object obj : extendedState.getVariables().values()) {
			if (obj instanceof Boolean) {
				return (Boolean) obj;
			}
		}
		return null;
	}

}
