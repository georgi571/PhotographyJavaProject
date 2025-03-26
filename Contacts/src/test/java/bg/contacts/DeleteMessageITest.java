package bg.contacts;

import bg.contacts.contact.model.ContactMessage;
import bg.contacts.contact.repository.ContactMessageRepository;
import bg.contacts.contact.service.impl.ContactMessageServiceImpl;
import bg.contacts.exception.ResourceNotFoundException;
import bg.contacts.web.dto.ContactMessageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class DeleteMessageITest {
    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private ContactMessageServiceImpl contactMessageService;

    @Test
    void testDeleteMessage() {
        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setName("User One");
        contactMessage.setEmail("userone@example.com");
        contactMessage.setMessage("Message to be deleted");

        ContactMessage savedMessage = contactMessageRepository.save(contactMessage);

        UUID adminId = UUID.randomUUID();

        ContactMessageResponse response = contactMessageService.deleteMessage(savedMessage.getId(), adminId);

        assertEquals(savedMessage.getId(), response.getId());
        assertTrue(response.isDeleted());

        ContactMessage deletedMessage = contactMessageRepository.findById(savedMessage.getId()).orElseThrow();
        assertTrue(deletedMessage.isDeleted());
        assertEquals(adminId, deletedMessage.getWhoDelete());
    }

    @Test
    void testDeleteMessage_NotFound() {
        UUID invalidId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();

        assertThrows(ResourceNotFoundException.class, () -> contactMessageService.deleteMessage(invalidId, adminId));
    }
}
