package io.gupshup.mdb.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * POJO for List Entity
 *
 * @author deepanshu
 */
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "list")
@EntityListeners(AuditingEntityListener.class)
public class ListEntity {

	/**
	 * List ID
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String listId;

	/**
	 * List Name (Mandatory)
	 */
	@NonNull
	private String name;

	/**
	 * User ID (Mandatory)
	 */
	@NonNull
	private String userId;

	/**
	 * Date of Creation
	 */
	@CreatedDate
	private LocalDateTime creationDate;

	/**
	 * Last Modified Date
	 */
	@LastModifiedDate
	private LocalDateTime lastUpdatedDate;

	/**
	 * If list is active
	 */
	private boolean isActive = true;

	/**
	 * Set of contacts in this list
	 */
	@ManyToMany(mappedBy = "lists", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH,
			CascadeType.MERGE, CascadeType.REFRESH})
	private Set<ContactEntity> contactEntities;
}
