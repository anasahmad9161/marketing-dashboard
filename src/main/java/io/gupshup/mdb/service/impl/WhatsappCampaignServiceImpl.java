package io.gupshup.mdb.service.impl;

import io.gupshup.mdb.exceptions.CustomRuntimeException;
import io.gupshup.mdb.service.WhatsappCampaignService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static io.gupshup.mdb.constants.ServiceConstants.MESSAGE;

@Service
class WhatsappCampaignServiceImpl implements WhatsappCampaignService {

	private static final Logger logger = LoggerFactory.getLogger(WhatsappCampaignServiceImpl.class);

	@Value("${soip.server}")
	private String serverUrl;
	@Value("${soip.env}")
	private String env;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public void sendPNToken(String phoneNumber) {
		logger.info("Preparing Request to call SOIP API to establish whatsapp connection for phone : {}", phoneNumber);
		String url = serverUrl + env + "/phone/" + phoneNumber.substring(0, 3) + "/" + phoneNumber.substring(3)
				+ "/business/msg/pntoken";
		try {
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, null, String.class);
			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				logger.info("Sending PN Token is successful");
			} else {
				logger.info("Sending PN Token via SOIP failed, throwing exception");
				JSONObject obj = new JSONObject(responseEntity.getBody());
				throw new CustomRuntimeException(obj.getString(MESSAGE));
			}
		} catch (HttpClientErrorException e) {
			logger.info("Sending PN Token via SOIP failed with exception : {}", e.getResponseBodyAsString());
			JSONObject obj = new JSONObject(e.getResponseBodyAsString());
			throw new CustomRuntimeException(obj.getString(MESSAGE) + ". Please logout and try again.");
		}
	}

}
