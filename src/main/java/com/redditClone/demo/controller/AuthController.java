package com.redditClone.demo.controller;

import com.redditClone.demo.dto.*;
import com.redditClone.demo.model.User;
import com.redditClone.demo.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.redditClone.demo.service.AuthService;

import lombok.AllArgsConstructor;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;
	
	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest)
	{
		authService.signup(registerRequest);
		return new ResponseEntity<>("User registration Successful",HttpStatus.OK);
	}

	@GetMapping("/accountVerification/{token}")

	public ResponseEntity<String> verifyAccount(@PathVariable String token)
	{
		authService.verifyAccount(token);

		return new ResponseEntity<>("Account activated successfully",HttpStatus.OK);
	}

	@PostMapping("/login")

		public AuthenticationResponse login(@RequestBody LoginRequest loginRequest)
		{
			return	authService.login(loginRequest);

		}

	@PostMapping("/refresh/token")
	public  AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest)
	{
		return authService.refreshToken(refreshTokenRequest);
	}

	@PostMapping("/logout")
	public String logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest)
	{
		refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
		return "Refresh token deleted successfully";
	}

	@PutMapping("/changeCurrentUserDetails")
	public String updateUser (@RequestBody UpdateUserDto updateUserDto) {
		return authService.updateUser(updateUserDto);
	}

	@GetMapping
	public ResponseEntity<List<User>> getAllUsers()
	{
		return ResponseEntity.status(HttpStatus.OK).body(authService.getAllUsers());
	}




	
}
