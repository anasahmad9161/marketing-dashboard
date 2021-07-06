package io.gupshup.mdb.controller.rest;

import io.gupshup.mdb.auth.AuthenticationService;
import io.gupshup.mdb.dto.contact.Contact;
import io.gupshup.mdb.dto.contact.UploadContactsResponse;
import io.gupshup.mdb.entities.ContactEntity;
import io.gupshup.mdb.service.ContactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.gupshup.mdb.constants.APIConstants.API;
import static io.gupshup.mdb.constants.APIConstants.CONTACT;
import static io.gupshup.mdb.constants.APIConstants.CONTACTID;
import static io.gupshup.mdb.constants.APIConstants.CONTACTS;
import static io.gupshup.mdb.constants.APIConstants.CONTACT_ID;
import static io.gupshup.mdb.constants.APIConstants.TOKEN;
import static io.gupshup.mdb.constants.APIConstants.USER;
import static io.gupshup.mdb.constants.APIConstants.USERID;
import static io.gupshup.mdb.constants.APIConstants.USER_ID;
import static io.gupshup.mdb.constants.APIConstants.VERSION;

@RestController
@RequestMapping(value = API + VERSION + USER + USER_ID, headers = {TOKEN})
public class ContactRestController {

	@Autowired
	private ContactsService contactsService;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	@Qualifier("ContactEntityMapper")
	private Function<ContactEntity, Contact> mapper;

	@RequestMapping(value = CONTACTS, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
	public ResponseEntity<UploadContactsResponse> uploadContacts(@PathVariable(USERID) String userId,
	                                                             @RequestParam(value = "csv") MultipartFile file) {
		return new ResponseEntity<>(contactsService.uploadContacts(userId, file), HttpStatus.CREATED);
	}

	@RequestMapping(value = CONTACTS, method = RequestMethod.GET)
	public ResponseEntity<List<Contact>> getAllContacts(@PathVariable(USERID) String userId) {
		List<ContactEntity> contactEntities = contactsService.getAllContacts(userId);
		List<Contact> contacts = contactEntities.stream().map(entity -> mapper.apply(entity))
		                                        .collect(Collectors.toList());
		return new ResponseEntity<>(contacts, HttpStatus.OK);
	}

	@RequestMapping(value = CONTACT + CONTACT_ID, method = RequestMethod.GET)
	public ResponseEntity<Contact> getContact(@PathVariable(USERID) String userId,
	                                          @PathVariable(CONTACTID) String contactId) {
		return new ResponseEntity<>(mapper.apply(contactsService.getContact(userId, contactId)), HttpStatus.OK);
	}

	@RequestMapping(value = CONTACTS, method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteContacts(@PathVariable(USERID) String userId,
	                                           @RequestBody List<String> contacts) {
		contactsService.deleteContacts(userId, contacts);
		return ResponseEntity.ok().build();
	}
}
