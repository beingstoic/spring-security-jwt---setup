package com.assignup.ie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assignup.ie.entity.AppUser;
import com.assignup.ie.payload.SignupRequest;
import com.assignup.ie.service.AppUserService;
import com.assignup.ie.service.ConfirmationTokenService;

@RestController
@RequestMapping("api/auth")
public class AuthController {

	@Autowired
	AppUserService userDetailsService;
	
	@Autowired
	ConfirmationTokenService confirmationTokenService;

	@PostMapping("/register")
	public ResponseEntity<AppUser> register(@RequestBody SignupRequest request){
		AppUser newUser = userDetailsService.register(request);
		return new ResponseEntity<>(newUser, HttpStatus.OK);
	}
	
	@GetMapping("/verification")
	public ResponseEntity<Boolean> verifyUser(@RequestParam("token") String token){
		return new ResponseEntity<>(confirmationTokenService.verifyToken(token), HttpStatus.OK);
	}
	
}
