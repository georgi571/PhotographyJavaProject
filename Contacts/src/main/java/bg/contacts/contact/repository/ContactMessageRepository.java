package bg.contacts.contact.repository;

import bg.contacts.contact.model.ContactMessage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, UUID> {

    List<ContactMessage> findByIsAnsweredTrueAndIsDeletedFalseAndSentAtBefore(LocalDateTime oneWeekAgo);

    @Modifying
    @Transactional
    @Query("DELETE FROM ContactMessage c WHERE c.isDeleted = true AND c.sentAt < :oneMonthAgo")
    void deleteMessagesOlderThanOneMonth(LocalDateTime oneMonthAgo);
}
