package io.gupshup.mdb.service.impl;

import io.gupshup.mdb.entities.ListEntity;
import io.gupshup.mdb.exceptions.CustomRuntimeException;
import io.gupshup.mdb.exceptions.InvalidRequestException;
import io.gupshup.mdb.repository.ListsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommonServiceImplTest {

	@Mock
	private ListsRepository listsRepository;
	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private CommonServiceImpl commonServiceImpl;

	@Test
	public void createEssentialDataTest() {
		when(listsRepository.findByNameAndUserIdAndIsActive(anyString(), anyString(), anyBoolean()))
				.thenReturn(Optional.empty());
		when(listsRepository.save(any())).thenReturn(new ListEntity("name", "userid"));
		commonServiceImpl.createEssentialData("userid");
		verify(listsRepository, times(1)).findByNameAndUserIdAndIsActive(anyString(), anyString(), anyBoolean());
		verify(listsRepository, times(1)).save(any());
	}

	@Test
	public void createEssentialDataTestIfAlreadyExists() {
		when(listsRepository.findByNameAndUserIdAndIsActive(anyString(), anyString(), anyBoolean()))
				.thenReturn(Optional.of(new ListEntity("name", "userid")));
		commonServiceImpl.createEssentialData("userid");
		verify(listsRepository, times(1)).findByNameAndUserIdAndIsActive(anyString(), anyString(), anyBoolean());
		verify(listsRepository, times(0)).save(any());
	}

	@Test
	public void getSenderPhoneNumbersTest() {
		when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity(
				"[{\"phoneno\":\"123456789\"}]", HttpStatus.OK));
		List<String> list1 = commonServiceImpl.getSenderPhoneNumbers("apikey");
		assert list1 != null;
		assertEquals(1, list1.size());
		assert list1.get(0).equals("123456789");
	}

	@Test
	public void getSenderPhoneNumbersTestIfBadRequest() {
		when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity(
				"[{\"phoneno\":\"123456789\"}]", HttpStatus.BAD_REQUEST));
		assertThrows(CustomRuntimeException.class, () -> commonServiceImpl.getSenderPhoneNumbers("apikey"));
	}

	@Test
	public void getSenderPhoneNumbersTestIfWrongRequest() {
		when(restTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity("phone",
		                                                                                  HttpStatus.HTTP_VERSION_NOT_SUPPORTED));
		assertThrows(CustomRuntimeException.class, () -> commonServiceImpl.getSenderPhoneNumbers("apikey"));
	}

	@Test
	public void getSenderPhoneNumbersTestIfInvalidRequest() {
		assertThrows(InvalidRequestException.class, () -> commonServiceImpl.getSenderPhoneNumbers(""));
	}
}
