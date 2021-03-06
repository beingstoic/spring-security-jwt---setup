package com.assignup.ie.service;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.assignup.ie.constants.UserRole;
import com.assignup.ie.email.EmailSender;
import com.assignup.ie.entity.AppUser;
import com.assignup.ie.entity.ConfirmationToken;
import com.assignup.ie.payload.JwtResponse;
import com.assignup.ie.payload.SignupRequest;
import com.assignup.ie.repository.AppUserRepository;
import com.assignup.ie.security.jwt.JwtUtils;

@Service
public class AppUserService implements UserDetailsService{

	@Autowired
	private AppUserRepository appUserRepository;
	
	@Autowired 
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired 
	private ConfirmationTokenService confirmationTokenService;
	
	@Autowired
	private EmailSender emailSender;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return appUserRepository.findByEmail(username);
	}
	

	public AppUser register(SignupRequest request) {
		if(appUserRepository
                .findByEmail(request.getEmail())
                !=null)
			throw new IllegalStateException("email already taken");
		String encodedPassword = passwordEncoder.encode(request.getPassword());
		UserRole role = null;
		if(request.getRole().contentEquals("CUSTOMER"))
			role = UserRole.CUSTOMER;
		else if(request.getRole().contentEquals("WRITER"))
			role = UserRole.WRITER;
		else throw new IllegalStateException("role is not found");
		
		AppUser newUser = new AppUser(request.getEmail(), encodedPassword, request.getFirstname(), request.getLastname(), role);
		newUser = appUserRepository.save(newUser);
		ConfirmationToken token = confirmationTokenService.generateVerificationToken(newUser);
		URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/auth/verification").queryParam("token", token.getToken())
				.build().toUri();
		String body = "Hi user, please click on the link below and verify your email address with us! \n"+ location;
		emailSender.send(newUser.getEmail(), "Verify your email", body);
		return newUser;
	}
	
	
	public void enableAppUser(String email) {
		if(!appUserRepository.existsByEmail(email)) {
			throw new IllegalStateException("User does not exists");
		}
		AppUser disabledUser= appUserRepository.findByEmail(email);
		disabledUser.setEnabled(true);
		appUserRepository.save(disabledUser);
	}


	public JwtResponse authenticateUser(String username, String password) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		System.out.println(authentication);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		AppUser userDetails = (AppUser) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		return new JwtResponse(jwt, userDetails.getUserId(), userDetails.getUsername(), userDetails.getPassword(), roles);
		
	}
	
	
}
