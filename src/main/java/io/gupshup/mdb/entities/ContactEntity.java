package io.gupshup.mdb.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

/**
 * POJO for Contact Entity
 *
 * @author deepanshu
 */
@Getter
@Setter
@Entity
@Table(name = "contact", indexes = @Index(name = "userid_Phone", columnList = "userId, phoneNumber", unique = true))
@RequiredArgsConstructor
@NoArgsConstructor
public class ContactEntity {

	/**
	 * Contact ID
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
	 * Set of Lists that this contact is associated to
	 */
	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinTable(name = "contact_list", joinColumns = @JoinColumn(name = "contact_id"), inverseJoinColumns =
    @JoinColumn(name = "list_id"))
	private Set<ListEntity> lists;
}
