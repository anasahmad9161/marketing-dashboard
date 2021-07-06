package io.gupshup.mdb.dto.campaign;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CampaignStatus {

	private int successCount;
	private int failedCount;
	private int totalListSize;
}
