package io.gupshup.mdb.repository;

import io.gupshup.mdb.entities.CampaignReply;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA Repository to perform DB operations for CampaignReply Entity
 *
 * @author deepanshu
 */
public interface CampaignReplyRepository extends JpaRepository<CampaignReply, String> {
}
