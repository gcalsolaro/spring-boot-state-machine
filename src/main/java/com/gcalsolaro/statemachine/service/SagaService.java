package com.gcalsolaro.statemachine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Service;

import com.gcalsolaro.statemachine.domain.entity.Instance;
import com.gcalsolaro.statemachine.domain.entity.User;
import com.gcalsolaro.statemachine.domain.enums.InstanceState;
import com.gcalsolaro.statemachine.domain.enums.UserState;
import com.gcalsolaro.statemachine.domain.enums.statemachine.ApplicationEvents;
import com.gcalsolaro.statemachine.domain.enums.statemachine.ApplicationStates;
import com.gcalsolaro.statemachine.repository.InstanceRepository;
import com.gcalsolaro.statemachine.repository.UserRepository;

@Service
public class SagaService {

	@Autowired
	private InstanceRepository instanceRepository;

	@Autowired
	private UserRepository userRepository;

	public Action<ApplicationStates, ApplicationEvents> createInstance() {
		return new Action<ApplicationStates, ApplicationEvents>() {
			@Override
			public void execute(StateContext<ApplicationStates, ApplicationEvents> context) {
				Instance instance = findInstance(context.getExtendedState());
				instance.setState(InstanceState.PENDING);
				instance = instanceRepository.save(instance);
				context.getExtendedState().getVariables().put("instance", instance);
			}
		};
	}

	public Action<ApplicationStates, ApplicationEvents> createUser() {
		return new Action<ApplicationStates, ApplicationEvents>() {
			@Override
			public void execute(StateContext<ApplicationStates, ApplicationEvents> context) {
				User user = findUser(context.getExtendedState());
				user.setState(UserState.CREATED);
				user = userRepository.save(user);
				// Save and update context variable
				context.getExtendedState().getVariables().put("user", user);
			}
		};
	}

	public Action<ApplicationStates, ApplicationEvents> evaluateUser() {
		return new Action<ApplicationStates, ApplicationEvents>() {
			@Override
			public void execute(StateContext<ApplicationStates, ApplicationEvents> context) {
				User user = findUser(context.getExtendedState());
				if (user != null) {
					if (user.getState().equals(UserState.CREATED)) {
						// the boolean is used for example purposes to manage the success or failure of the transaction
						boolean success = findSuccessState(context.getExtendedState());
						if (success)
							context.getStateMachine().sendEvent(ApplicationEvents.UPDATE_USER_CREATED);
						else
							context.getStateMachine().sendEvent(ApplicationEvents.UPDATE_USER_REJECTED);
					}
				}
			}
		};
	}

	public Action<ApplicationStates, ApplicationEvents> updateInstance(boolean success) {
		return new Action<ApplicationStates, ApplicationEvents>() {
			@Override
			public void execute(StateContext<ApplicationStates, ApplicationEvents> context) {
				Instance instance = findInstance(context.getExtendedState());

				instance.setState(InstanceState.REJECTED);

				if (success) {
					User user = findUser(context.getExtendedState());
					instance.setUser(user);
					instance.setState(InstanceState.CREATED);
				}

				instanceRepository.save(instance);
			}
		};
	}

	/**
	 * 
	 * @return
	 */
	public Action<ApplicationStates, ApplicationEvents> saveUserState() {
		return new Action<ApplicationStates, ApplicationEvents>() {
			@Override
			public void execute(StateContext<ApplicationStates, ApplicationEvents> context) {
				context.getExtendedState().getVariables().put("SUBCONTEXT_EVENT", context.getTarget().getId());
			}
		};
	}

	public Guard<ApplicationStates, ApplicationEvents> protectInstanceState(InstanceState state) {
		return new Guard<ApplicationStates, ApplicationEvents>() {
			@Override
			public boolean evaluate(StateContext<ApplicationStates, ApplicationEvents> context) {
				Instance instance = findInstance(context.getExtendedState());
				if (instance != null)
					return instance.getState().equals(state);
				return false;
			}
		};
	}

	public Guard<ApplicationStates, ApplicationEvents> protectUserState(UserState state) {
		return new Guard<ApplicationStates, ApplicationEvents>() {
			@Override
			public boolean evaluate(StateContext<ApplicationStates, ApplicationEvents> context) {
				User user = findUser(context.getExtendedState());
				if (user != null)
					return user.getState().equals(state);
				return false;
			}
		};
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Internal Helper ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

	private Instance findInstance(ExtendedState extendedState) {
		for (Object obj : extendedState.getVariables().values()) {
			if (obj instanceof Instance) {
				return (Instance) obj;
			}
		}
		return null;
	}

	private User findUser(ExtendedState extendedState) {
		for (Object obj : extendedState.getVariables().values()) {
			if (obj instanceof User) {
				return (User) obj;
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
