package io.gupshup.mdb.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WhatsappQR {

	@NotBlank(message = "User Id is mandatory")
	private String userId;
	@NotBlank(message = "Image is mandatory")
	private String image;
}
