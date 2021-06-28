package com.gcalsolaro.statemachine.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gcalsolaro.statemachine.domain.enums.statemachine.ApplicationEvents;
import com.gcalsolaro.statemachine.domain.enums.statemachine.ApplicationStates;
import com.gcalsolaro.statemachine.service.SagaOrchestrator;

@RestController
@RequestMapping("/api/statemachine")
public class ApiController {

	@Autowired
	private SagaOrchestrator sagaOrchestrator;

	@GetMapping("/start/ok")
	public ResponseEntity<String> ok(HttpServletRequest request, HttpServletResponse response) {
		StateMachine<ApplicationStates, ApplicationEvents> sagaResult = sagaOrchestrator.createIstanzaSaga(true);
		return new ResponseEntity<String>(sagaResult.getState().getId().name(), HttpStatus.OK);
	}
	
	@GetMapping("/start/ko")
	public ResponseEntity<String> ko(HttpServletRequest request, HttpServletResponse response) {
		StateMachine<ApplicationStates, ApplicationEvents> sagaResult = sagaOrchestrator.createIstanzaSaga(false);
		return new ResponseEntity<String>(sagaResult.getState().getId().name(), HttpStatus.OK);
	}

}
