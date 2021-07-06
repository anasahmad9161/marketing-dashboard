package io.gupshup.mdb.mapper.impl;

import io.gupshup.mdb.dto.contact.ContactRecord;
import io.gupshup.mdb.entities.ContactStaging;
import io.gupshup.mdb.exceptions.InvalidRequestException;
import io.gupshup.mdb.validator.MarketingDashboardValidator;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CSVDataMapperImplTest {

	@Mock
	private MarketingDashboardValidator validator;
	@Mock
	private MultipartFile file;

	@InjectMocks
	private CsvDataMapperImpl csvDataMapper;

	@Before
	public void setup() throws IOException {
		File file1 = new File("src/test/resources/sampleTest.csv");
		FileInputStream input = new FileInputStream(file1);
		file = new MockMultipartFile("file1", file1.getName(), "text/csv", input);
	}

	@Test
	public void shouldThrowExceptionIfInvalidFile(){
		when(validator.checkForErrorsInFile(file)).thenReturn(List.of("error"));
		assertThrows(InvalidRequestException.class, () -> csvDataMapper.getCSVRecords(file));
	}

	@Test
	public void shouldThrowExceptionIfHeadersMismatch(){
		when(validator.checkForErrorsInFile(file)).thenReturn(new ArrayList<>());
		when(validator.checkHeaders(anyList())).thenReturn(false);
		assertThrows(InvalidRequestException.class, () -> csvDataMapper.getCSVRecords(file));
	}

	@Test
	public void shouldReturnNonEmptyListOfRecordsIfValidFile(){
		when(validator.checkForErrorsInFile(file)).thenReturn(new ArrayList<>());
		when(validator.checkHeaders(anyList())).thenReturn(true);
		Iterable<CSVRecord> csvRecords = csvDataMapper.getCSVRecords(file);
		assert csvRecords != null;
		assert csvRecords.iterator().hasNext();
	}

	@Test
	public void shouldReturnEmptyContactStagingMapAndErroneousContactRecords(){
		when(validator.checkForErrorsInFile(file)).thenReturn(new ArrayList<>());
		when(validator.checkHeaders(anyList())).thenReturn(true);
		Iterable<CSVRecord> csvRecords = csvDataMapper.getCSVRecords(file);
		when(validator.checkForErrorInRecord(any())).thenReturn(List.of("error"));

		Pair<Map<String, ContactStaging>, List<ContactRecord>> outputPair = csvDataMapper.parseRecordsAndGetContactStaging("userid", csvRecords);
		assert outputPair.getFirst().isEmpty();
		assert !outputPair.getSecond().isEmpty();
		assert outputPair.getSecond().stream().allMatch(contactRecord -> contactRecord.getStatus().equals("FAILED"));
	}

	@Test
	public void shouldReturnSuccessfulOutputIfNoErrors(){
		when(validator.checkForErrorsInFile(file)).thenReturn(new ArrayList<>());
		when(validator.checkHeaders(anyList())).thenReturn(true);
		Iterable<CSVRecord> csvRecords = csvDataMapper.getCSVRecords(file);
		when(validator.checkForErrorInRecord(any())).thenReturn(new ArrayList<>());

		Pair<Map<String, ContactStaging>, List<ContactRecord>> outputPair = csvDataMapper.parseRecordsAndGetContactStaging("userid", csvRecords);

		assert !outputPair.getFirst().isEmpty();
		assert !outputPair.getSecond().isEmpty();
		assert outputPair.getSecond().stream().allMatch(contactRecord -> contactRecord.getStatus().equals("SUCCESS"));
		assert outputPair.getFirst().size() == 3;
	}

}
