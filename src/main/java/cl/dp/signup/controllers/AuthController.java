package cl.dp.signup.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cl.dp.signup.payload.request.LoginRequest;
import cl.dp.signup.payload.request.SignupRequest;
import cl.dp.signup.payload.response.ServiceResponse;
import cl.dp.signup.services.AuthService;

import javax.validation.Valid;

@RestController
public class AuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	AuthService authService;

	DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss a");
	
	@PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> signUp(@Valid @RequestBody SignupRequest signUpRequest) {

		try
		{
			ServiceResponse response = authService.signUp(signUpRequest);
			if (!response.isHasError())
				return ResponseEntity.ok().body(response.getResponse());
			return ResponseEntity.badRequest().body(response.getResponse());
		}
		catch(Exception x)
		{
			logger.error(x.getMessage());
			logger.error(x.getStackTrace().toString());
			return ResponseEntity.internalServerError().body("\"error\": [{\"timestamp\": " +customFormatter.format(LocalDateTime.now())  + ", \"codigo\": 500, \"detail\": \"Error Interno\"}]");
		}

	}

	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {
		try
		{
			ServiceResponse response = authService.login(loginRequest);
			if (!response.isHasError())
				return ResponseEntity.ok().body(response.getResponse());
			return ResponseEntity.badRequest().body(response.getResponse());
		}
		catch(Exception x)
		{
			logger.error(x.getMessage());
			logger.error(x.getStackTrace().toString());
			return ResponseEntity.internalServerError().body("\"error\": [{\"timestamp\": " +customFormatter.format(LocalDateTime.now())  + ", \"codigo\": 500, \"detail\": \"Error Interno\"}]");
		}
	}
}
