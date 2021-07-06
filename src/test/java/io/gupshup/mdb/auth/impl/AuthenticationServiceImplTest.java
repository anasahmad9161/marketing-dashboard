package io.gupshup.mdb.auth.impl;

import io.gupshup.mdb.auth.AuthTokenRepository;
import io.gupshup.mdb.auth.AuthenticationEntity;
import io.gupshup.mdb.exceptions.AuthenticationException;
import io.gupshup.mdb.service.CommonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceImplTest {

	@Mock
	private CommonService commonService;
	@Mock
	private AuthTokenRepository repository;

	@InjectMocks
	private AuthenticationServiceImpl authenticationService;

	@Test
	public void shouldReturnEntityIfExists() {
		when(repository.findByAuthToken(anyString())).thenReturn(Optional.of(new AuthenticationEntity()));
		AuthenticationEntity authenticationEntity = authenticationService.getAuthEntity("authToken");
		assert authenticationEntity != null;
	}

	@Test
	public void shouldThrowExceptionForGetEntityByAuthTokenIfDoesNotExist() {
		when(repository.findByAuthToken(anyString())).thenReturn(Optional.empty());
		assertThrows("Invalid Token.", AuthenticationException.class,
		             () -> authenticationService.getAuthEntity("authToken"));
	}

	@Test
	public void shouldThrowExceptionForGetEntityByAuthTokenIsNullOrEmpty() {
		assertThrows("Invalid Token.", AuthenticationException.class, () -> authenticationService.getAuthEntity(""));
	}

	@Test
	public void shouldInvalidateToken() {
		AuthenticationEntity authenticationEntity = new AuthenticationEntity();
		when(repository.findByAuthToken(anyString())).thenReturn(Optional.of(authenticationEntity));
		authenticationService.invalidateToken("authToken");
		verify(repository, times(1)).save(any());
	}

	@Test
	public void shouldReturnTrueIfValidAuthTokenAndPhone() {
		AuthenticationEntity authenticationEntity = new AuthenticationEntity();
		authenticationEntity.setPrimaryPhoneNumber("1234");
		authenticationEntity.setSecondaryPhoneNumber("5678");
		authenticationEntity.setExpired(false);
		when(repository.findByAuthToken(anyString())).thenReturn(Optional.of(authenticationEntity));
		assert authenticationService.validateAuthToken("1234", "authToken");
	}

	@Test
	public void shouldReturnFalseIfExpiredAuthTokenAndValidPhone() {
		AuthenticationEntity authenticationEntity = new AuthenticationEntity();
		authenticationEntity.setPrimaryPhoneNumber("1234");
		authenticationEntity.setSecondaryPhoneNumber("5678");
		authenticationEntity.setExpired(true);
		when(repository.findByAuthToken(anyString())).thenReturn(Optional.of(authenticationEntity));
		assert !authenticationService.validateAuthToken("1234", "authToken");
	}

	@Test
	public void shouldThrowExceptionIfValidAuthTokenAndInvalidPhone() {
		AuthenticationEntity authenticationEntity = new AuthenticationEntity();
		authenticationEntity.setPrimaryPhoneNumber("1234");
		authenticationEntity.setSecondaryPhoneNumber("5678");
		authenticationEntity.setExpired(false);
		when(repository.findByAuthToken(anyString())).thenReturn(Optional.of(authenticationEntity));
		assertThrows("User not found. Please check user ID", AuthenticationException.class,
		             () -> authenticationService.validateAuthToken("9012", "authToken"));
	}

	@Test
	public void shouldThrowExceptionIfAuthenticationEntityExistsForQRTokenAndIsExpired() {
		AuthenticationEntity authenticationEntity = new AuthenticationEntity();
		authenticationEntity.setExpired(true);
		when(repository.findByQrToken(any())).thenReturn(List.of(authenticationEntity));
		assertThrows("Token generated using this qrToken is expired. Please provide new qrToken",
		             AuthenticationException.class, () -> authenticationService.register("qrToken", "apiKey"));
	}

	@Test
	public void shouldThrowExceptionIfAuthenticationEntityExistsForQRTokenAndIsNotExpired() {
		AuthenticationEntity authenticationEntity = new AuthenticationEntity();
		authenticationEntity.setExpired(false);
		when(repository.findByQrToken(any())).thenReturn(List.of(authenticationEntity));
		assertThrows("User is already active. Please logout and try again.", AuthenticationException.class,
		             () -> authenticationService.register("qrToken", "apiKey"));
	}

	@Test
	public void shouldThrowExceptionIfNoPhoneNumbersFound() {
		when(repository.findByQrToken(any())).thenReturn(new ArrayList<>());
		when(commonService.getSenderPhoneNumbers(any())).thenReturn(new ArrayList<>());
		assertThrows("No Phone Numbers found for this API Key.", AuthenticationException.class,
		             () -> authenticationService.register("qrToken", "apiKey"));
	}

	@Test
	public void shouldBeSuccessIfValidRequest() {
		when(repository.findByQrToken(any())).thenReturn(new ArrayList<>());
		when(commonService.getSenderPhoneNumbers(any())).thenReturn(List.of("1234"));
		authenticationService.register("qrToken", "apiKey");
		verify(commonService, atLeastOnce()).createEssentialData("1234");
		verify(repository, atLeastOnce()).save(any());
	}

	@Test
	public void shouldBeSuccessIfValidRequestIfPhoneNumbersAreMoreThanOne() {
		when(repository.findByQrToken(any())).thenReturn(new ArrayList<>());
		when(commonService.getSenderPhoneNumbers(any())).thenReturn(List.of("1234", "5678"));
		authenticationService.register("qrToken", "apiKey");
		verify(commonService, times(2)).createEssentialData(anyString());
		verify(repository, atLeastOnce()).save(any());
	}
}
