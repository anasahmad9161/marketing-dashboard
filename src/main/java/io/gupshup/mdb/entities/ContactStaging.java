package io.gupshup.mdb.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO for Contact Staging Entity
 *
 * @author deepanshu
 */
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "contact_staging")
public class ContactStaging {

	/**
	 * Contact Staging ID
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	/**
	 * Name of the contact
	 */
	private String name;

	/**
	 * Nickname of the contact
	 */
	private String nickname;

	/**
	 * Salutation of the contact
	 */
	private String salutation;

	/**
	 * Phone Number of the contact (Mandatory)
	 */
	@NonNull
	private String phoneNumber;

	/**
	 * User ID (Mandatory)
	 */
	@NonNull
	private String userId;

	/**
	 * If the contact is duplicate
	 */
	@Column(name = "duplicate")
	private Boolean duplicate = false;
}
