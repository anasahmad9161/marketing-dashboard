package io.gupshup.mdb.controller.rest;

import io.gupshup.mdb.auth.AuthenticationService;
import io.gupshup.mdb.dto.list.ContactCampaignAllList;
import io.gupshup.mdb.dto.list.ContactCampaignList;
import io.gupshup.mdb.entities.ListEntity;
import io.gupshup.mdb.service.ListsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.gupshup.mdb.constants.APIConstants.API;
import static io.gupshup.mdb.constants.APIConstants.LIST;
import static io.gupshup.mdb.constants.APIConstants.LISTID;
import static io.gupshup.mdb.constants.APIConstants.LISTS;
import static io.gupshup.mdb.constants.APIConstants.LIST_ID;
import static io.gupshup.mdb.constants.APIConstants.TOKEN;
import static io.gupshup.mdb.constants.APIConstants.USER;
import static io.gupshup.mdb.constants.APIConstants.USERID;
import static io.gupshup.mdb.constants.APIConstants.USER_ID;
import static io.gupshup.mdb.constants.APIConstants.VERSION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = API + VERSION + USER + USER_ID, headers = {TOKEN})
public class ListRestController {

	@Autowired
	private ListsService listsService;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	@Qualifier("ListEntityMapper")
	private Function<ListEntity, ContactCampaignList> listMapper;

	@Autowired
	@Qualifier("AllListEntityMapper")
	private Function<ListEntity, ContactCampaignAllList> allListMapper;

	@RequestMapping(value = LIST, method = RequestMethod.POST)
	public ResponseEntity<ContactCampaignList> saveList(@PathVariable(USERID) String userId,
	                                                    @RequestParam(name = "name") String name) {
		return new ResponseEntity<>(listMapper.apply(listsService.saveList(name, userId)), CREATED);
	}

	@RequestMapping(value = LIST + LIST_ID, method = RequestMethod.GET)
	public ResponseEntity<ContactCampaignList> getList(@PathVariable(USERID) String userId,
	                                                   @PathVariable(LISTID) String listId) {
		return new ResponseEntity<>(listMapper.apply(listsService.getList(userId, listId)), OK);
	}

	@RequestMapping(value = LISTS, method = RequestMethod.GET)
	public ResponseEntity<List<ContactCampaignAllList>> getAllLists(@PathVariable(USERID) String userId) {
		List<ListEntity> entityList = listsService.getAllLists(userId);
		List<ContactCampaignAllList> allLists = entityList.stream().map(entity -> allListMapper.apply(entity))
		                                                  .collect(Collectors.toList());
		return new ResponseEntity<>(allLists, OK);
	}

	@RequestMapping(value = LIST + LIST_ID, method = RequestMethod.PATCH)
	public ResponseEntity<ContactCampaignList> updateList(@PathVariable(USERID) String userId,
	                                                      @PathVariable(LISTID) String listId,
	                                                      @RequestParam(name = "name") String name) {
		return new ResponseEntity<>(listMapper.apply(listsService.updateName(listId, name, userId)), OK);
	}

	@RequestMapping(value = LIST + LIST_ID + "/add", method = RequestMethod.PATCH)
	public ResponseEntity<ContactCampaignList> addContacts(@PathVariable(USERID) String userId,
	                                                       @PathVariable(LISTID) String listId,
	                                                       @RequestBody List<String> contacts) {
		return new ResponseEntity<>(listMapper.apply(listsService.addContactsToList(userId, listId, contacts)), OK);
	}

	@RequestMapping(value = LIST + LIST_ID + "/remove", method = RequestMethod.PATCH)
	public ResponseEntity<ContactCampaignList> removeContacts(@PathVariable(USERID) String userId,
	                                                          @PathVariable(LISTID) String listId,
	                                                          @RequestBody List<String> contacts) {
		return new ResponseEntity<>(listMapper.apply(listsService.removeContactsFromList(userId, listId, contacts)), OK);
	}

	@RequestMapping(value = LIST + LIST_ID, method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteList(@PathVariable(USERID) String userId,
	                                       @PathVariable(LISTID) String listId) {
		listsService.deleteList(userId, listId);
		return ResponseEntity.ok().build();
	}
}
