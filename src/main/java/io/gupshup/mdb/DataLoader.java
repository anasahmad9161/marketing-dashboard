package io.gupshup.mdb;

import io.gupshup.mdb.utils.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.gupshup.mdb.constants.ServiceConstants.SMS;
import static io.gupshup.mdb.constants.ServiceConstants.WHATSAPP;

@Component
public class DataLoader implements ApplicationRunner {

	@Autowired
	private EntityUtils entityUtils;

	@Override
	public void run(ApplicationArguments args) {
		entityUtils.createChannels(List.of(SMS, WHATSAPP));
	}
}