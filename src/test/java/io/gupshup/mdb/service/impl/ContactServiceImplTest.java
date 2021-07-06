package io.gupshup.mdb.service.impl;


import io.gupshup.mdb.dto.contact.ContactRecord;
import io.gupshup.mdb.dto.contact.UploadContactsResponse;
import io.gupshup.mdb.entities.ContactEntity;
import io.gupshup.mdb.entities.ContactStaging;
import io.gupshup.mdb.mapper.CsvDataMapper;
import io.gupshup.mdb.repository.ContactRepository;
import io.gupshup.mdb.repository.ContactStagingRepository;
import io.gupshup.mdb.repository.ListsRepository;
import io.gupshup.mdb.utils.EntityUtils;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContactServiceImplTest {

    @Mock
    private CsvDataMapper csvDataMapper;
    @Mock
    private ContactRepository contactRepository;
    @Mock
    private ContactStagingRepository contactStagingRepository;
    @Mock
    private ListsRepository listsRepository;
    @Mock
    private MultipartFile file;
    @Mock
    private EntityUtils entityUtils;

    @InjectMocks
    private ContactsServiceImpl contactsServiceImpl;

    @Test
    public void uploadContactsTest() throws IOException {
        try {
            File file1 = new File("src/test/resources/sampleTest.csv");
            FileInputStream input = new FileInputStream(file1);
            file = new MockMultipartFile("file1", file1.getName(), "text/csv", input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CSVRecord csvRecord = Mockito.mock(CSVRecord.class);
        List<CSVRecord> listCsv = List.of(csvRecord);
        List<ContactEntity> contactEntities = new ArrayList<>();
        ContactEntity contactEntity = new ContactEntity("PhoneNumber", "userid");
        contactEntity.setId("id");
        contactEntities.add(contactEntity);
        ContactStaging contactStaging = new ContactStaging("PhoneNumber", "userid");
        ContactRecord contactRecord = ContactRecord.builder().phoneNumber("PhoneNumber").status("SUCCESS").build();
        Pair<Map<String, ContactStaging>, List<ContactRecord>> parseOutcome =
                Pair.of(Map.of("userid", contactStaging), List.of(contactRecord));
        List<ContactStaging> contactStagings = new ArrayList<>();
        when(csvDataMapper.getCSVRecords(file)).thenReturn(listCsv);
        when(csvDataMapper.parseRecordsAndGetContactStaging(anyString(), any())).thenReturn(parseOutcome);
        when(contactStagingRepository.findAllByDuplicate(true)).thenReturn(contactStagings);
        when(contactRepository.findByUserIdAndPhoneNumberIn(anyString(), any())).thenReturn(contactEntities);
        when(listsRepository.findByNameAndUserIdAndIsActive(anyString(), anyString(), anyBoolean()))
                .thenReturn(Optional.empty());
        UploadContactsResponse response = contactsServiceImpl.uploadContacts("userid", file);

        assert response != null;
        assertEquals(0, response.getFailed());
        assertEquals(0, response.getDuplicates());
        assertEquals("id", response.getContactIds().get(0));
        assertEquals("PhoneNumber", response.getDetails().get(0).getPhoneNumber());
    }



    @Test
    public void testGetAllContactWhenFetchEmptyList() {
        List<ContactEntity> combinedList = new ArrayList<>();
        when(contactRepository.findAllByUserId(anyString())).thenReturn(combinedList);
        List<ContactEntity> td = contactsServiceImpl.getAllContacts("userid");
        assertEquals(0, td.size());
    }

    @Test
    public void testGetAllContactWhenFetchListOfData() {
        ContactEntity contactEntity = new ContactEntity("phoneNumber", "userid");
        List<ContactEntity> combinedList = new ArrayList<>();
        combinedList.add(contactEntity);
        when(contactRepository.findAllByUserId("userid")).thenReturn(combinedList);
        List<ContactEntity> td = contactsServiceImpl.getAllContacts("userid");
        assertEquals(1, td.size());
    }

    @Test
    public void testGetContact() {
        ContactEntity entity = new ContactEntity("PhoneNumber", "userid");
        when(entityUtils.fetchContactEntity(anyString(), anyString())).thenReturn(entity);
        ContactEntity entityOut = contactsServiceImpl.getContact("userid", "contactId");
        assert entityOut != null;
        assert entityOut.getPhoneNumber().equals("PhoneNumber");
        assert entityOut.getUserId().equals("userid");
    }

    @Test
    public void deleteContactsTest() {
        List<String> entities = List.of("ContactId");
        ContactEntity dummyEntity = new ContactEntity("phoneNumber", "userid");
        dummyEntity.setLists(Set.of());
        when(entityUtils.fetchContactEntity(anyString(), anyString())).thenReturn(dummyEntity);
        contactsServiceImpl.deleteContacts("userid", entities);
        verify(entityUtils, times(1)).fetchContactEntity(anyString(), anyString());
        verify(contactRepository, times(1)).deleteAll(any());
    }
}

