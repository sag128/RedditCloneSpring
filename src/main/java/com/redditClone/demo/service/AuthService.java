package com.redditClone.demo.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


import com.redditClone.demo.dto.*;
import com.redditClone.demo.exception.SpringRedditException;
import com.redditClone.demo.security.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.redditClone.demo.model.NotificationEmail;
import com.redditClone.demo.model.User;
import com.redditClone.demo.model.VerificationToken;
import com.redditClone.demo.repository.UserRepository;
import com.redditClone.demo.repository.VerificationTokenRepository;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {

	

	private final PasswordEncoder passwordEncoder ;
	private final VerificationTokenRepository verificationTokenRepository; 
	private final UserRepository userRepository; 
	private final MailService mailService;
	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final RefreshTokenService refreshTokenService;

	@Transactional 
	public void signup(RegisterRequest registerRequest)
	{
		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setCreated(Instant.now());
		user.setEnabled(false);
	
		userRepository.save(user);
		
		
		String token = generateVerificationToken(user);
		 mailService.sendMail(new NotificationEmail("Please Activate your Account",
	                user.getEmail(), "Thank you for signing up to Spring Reddit, " +
	                "please click on the below url to activate your account : " +
	                "http://localhost:8080/api/auth/accountVerification/" + token));
	}

	private String generateVerificationToken(User user) {
			String token = UUID.randomUUID().toString();
			VerificationToken verificationToken = new VerificationToken();
			verificationToken.setToken(token);
			verificationToken.setUser(user);
			verificationTokenRepository.save(verificationToken);
			return token;
	
	}


	@Transactional(readOnly = true)
	public User getCurrentUser() {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)
				SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userRepository.findByUsername(principal.getUsername())
				.orElseThrow(() -> new SpringRedditException("User name not found - " + principal.getUsername()));
	}
	public void verifyAccount(String token)
	{
		Optional<VerificationToken> verToken =  verificationTokenRepository.findByToken(token);
		verToken.orElseThrow(()-> new SpringRedditException("Invalid Token"));
		fetchUserAndEnable(verToken.get());

	}


	@Transactional
	private void fetchUserAndEnable(VerificationToken verificationToken) {

		String username = verificationToken.getUser().getUsername();

		User user = userRepository.findByUsername(username).orElseThrow(()-> new SpringRedditException("No User found with name "+username));

		user.setEnabled(true);
		userRepository.save(user);


	}


	@Transactional
	public AuthenticationResponse login(LoginRequest loginRequest)
	{
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtProvider.generateToken(authentication);
		return  AuthenticationResponse.builder()
									.authenticationToken(token)
									.refreshToken(refreshTokenService.generateRefreshToken().getToken())
									.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationTimeInMs()))
									.username(loginRequest.getUsername())
									.build();

	}


    public boolean isLoggedIn() {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	log.info(String.valueOf(authentication instanceof AnonymousAuthenticationToken));
    	return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();

	}

	public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

	refreshTokenService.validateToken(refreshTokenRequest.getRefreshToken());
	String token =jwtProvider.generateTokenByUsername(refreshTokenRequest.getUsername());
	return AuthenticationResponse.builder()
								.username(refreshTokenRequest.getUsername())
								.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationTimeInMs()))
								.authenticationToken(token)
								.refreshToken(refreshTokenRequest.getRefreshToken())
								.build();

	}

    public String updateUser(UpdateUserDto updateUserDto) {

		User user = getCurrentUser();

		if(!user.getEmail().equalsIgnoreCase(updateUserDto.getEmail()))
		{
			mailService.sendMail(new NotificationEmail("Please Review your Account",
					user.getEmail(), "You requested for an email id change from, " +user.getEmail()+
					" to " +updateUserDto.getEmail()+
					" Please check the new email id for further info"));

			user.setEnabled(false);
			String token = generateVerificationToken(user);
			mailService.sendMail(new NotificationEmail("Email Id change request",
					updateUserDto.getEmail(), "Thank you for signing up to Spring Reddit, " +
					"please click on the below url to activate your account : " +
					"http://localhost:8080/api/auth/accountVerification/" + token));
			user.setEmail(updateUserDto.getEmail());
			userRepository.save(user);
		}
		if(updateUserDto.getPassword()!=null)
		{
			user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
		}

		if(updateUserDto.getUsername()!=null)
		{
			user.setUsername(updateUserDto.getUsername());
		}

		return "User updated successfully";


	}
}