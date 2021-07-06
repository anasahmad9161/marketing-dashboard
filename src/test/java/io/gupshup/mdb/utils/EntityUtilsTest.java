package io.gupshup.mdb.utils;

import io.gupshup.mdb.entities.CampaignEntity;
import io.gupshup.mdb.entities.ChannelEntity;
import io.gupshup.mdb.entities.ContactEntity;
import io.gupshup.mdb.entities.ListEntity;
import io.gupshup.mdb.entities.MessageEntity;
import io.gupshup.mdb.exceptions.ResourceNotFountException;
import io.gupshup.mdb.repository.CampaignRepository;
import io.gupshup.mdb.repository.ChannelRepository;
import io.gupshup.mdb.repository.ContactRepository;
import io.gupshup.mdb.repository.ListsRepository;
import io.gupshup.mdb.repository.MessageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
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
public class EntityUtilsTest {

	@Mock
	private CampaignRepository campaignRepository;
	@Mock
	private ListsRepository listsRepository;
	@Mock
	private ContactRepository contactRepository;
	@Mock
	private MessageRepository messageRepository;
	@Mock
	private ChannelRepository channelRepository;

	@InjectMocks
	private EntityUtils entityUtils;

	@Test
	public void shouldReturnCampaignEntityIfExists() {
		when(campaignRepository.findByCampaignIdAndUserId(anyString(), anyString()))
				.thenReturn(Optional.of(new CampaignEntity()));
		assert entityUtils.fetchCampaignEntity("user", "id") != null;
	}

	@Test
	public void shouldThrowExceptionIfCampaignEntityDoesNotExist() {
		when(campaignRepository.findByCampaignIdAndUserId(anyString(), anyString())).thenReturn(Optional.empty());
		assertThrows(ResourceNotFountException.class, () -> entityUtils.fetchCampaignEntity("user","id"));
	}

	@Test
	public void shouldReturnListEntityIfExists() {
		when(listsRepository.findByListIdAndUserId(anyString(), anyString())).thenReturn(Optional.of(new ListEntity("name", "user")));
		assert entityUtils.fetchListEntity("user", "id") != null;
	}

	@Test
	public void shouldReturnAllContactsListEntityIfExists() {
		ListEntity listEntity = new ListEntity("All Contacts", "user");
		listEntity.setContactEntities(new HashSet<>());
		when(listsRepository.findByListIdAndUserId(anyString(), anyString())).thenReturn(Optional.of(listEntity));
		when(contactRepository.findAllByUserId(anyString())).thenReturn(new ArrayList<>());
		ListEntity entity = entityUtils.fetchListEntity("user", "id");
		assert entity != null;
		assert entity.getContactEntities().isEmpty();
	}

	@Test
	public void shouldThrowExceptionIfListEntityDoesNotExist() {
		when(listsRepository.findByListIdAndUserId(anyString(), anyString())).thenReturn(Optional.empty());
		assertThrows(ResourceNotFountException.class, () -> entityUtils.fetchListEntity("user","id"));
	}

	@Test
	public void shouldReturnContactEntityIfExists() {
		when(contactRepository.findByIdAndUserId(anyString(), anyString())).thenReturn(Optional.of(new ContactEntity()));
		assert entityUtils.fetchContactEntity("user", "id") != null;
	}

	@Test
	public void shouldThrowExceptionIfContactEntityDoesNotExist() {
		when(contactRepository.findByIdAndUserId(anyString(), anyString())).thenReturn(Optional.empty());
		assertThrows(ResourceNotFountException.class, () -> entityUtils.fetchContactEntity("user","id"));
	}

	@Test
	public void shouldReturnChannelEntityIfExists() {
		when(channelRepository.findById(anyString())).thenReturn(Optional.of(new ChannelEntity()));
		assert entityUtils.fetchChannelEntity("id") != null;
	}

	@Test
	public void shouldThrowExceptionIfChannelEntityDoesNotExist() {
		when(channelRepository.findById(anyString())).thenReturn(Optional.empty());
		assertThrows(ResourceNotFountException.class, () -> entityUtils.fetchChannelEntity("id"));
	}

	@Test
	public void shouldReturnMessageEntityIfExists() {
		when(messageRepository.findById(anyString())).thenReturn(Optional.of(new MessageEntity()));
		assert entityUtils.fetchMessageEntity("id") != null;
	}

	@Test
	public void shouldThrowExceptionIfMessageEntityDoesNotExist() {
		when(messageRepository.findById(anyString())).thenReturn(Optional.empty());
		assertThrows(ResourceNotFountException.class, () -> entityUtils.fetchMessageEntity("id"));
	}

	@Test
	public void shouldCreateChannelsIfDoesNotExist(){
		when(channelRepository.findByChannelName(anyString())).thenReturn(Optional.empty());
		entityUtils.createChannels(List.of("SMS"));
		verify(channelRepository, atLeastOnce()).save(any());
	}

	@Test
	public void shouldNotCreateChannelsIfAlreadyExist(){
		when(channelRepository.findByChannelName(anyString())).thenReturn(Optional.of(new ChannelEntity()));
		entityUtils.createChannels(List.of("SMS"));
		verify(channelRepository, times(0)).save(any());
	}
}
