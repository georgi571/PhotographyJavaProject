package bg.contacts;

import bg.contacts.contact.model.ContactMessage;
import bg.contacts.contact.repository.ContactMessageRepository;
import bg.contacts.contact.service.impl.ContactMessageServiceImpl;
import bg.contacts.web.dto.ContactMessageRequest;
import bg.contacts.web.dto.ContactMessageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class ReceiveContactMessageITest {
    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private ContactMessageServiceImpl contactMessageService;

    @Test
    void testReceiveContactMessage() {
        ContactMessageRequest request = new ContactMessageRequest();
        request.setName("Test User");
        request.setEmail("testuser@abv.bg");
        request.setMessage("New message");

        ContactMessageResponse response = contactMessageService.receiveContactMessage(request);
        assertNotNull(response);
        assertEquals("Test User", response.getName());
        assertEquals("testuser@abv.bg", response.getEmail());
        assertEquals("New message", response.getMessage());

        ContactMessage savedMessage = contactMessageRepository.findAll().getFirst();
        assertNotNull(savedMessage);
        assertEquals("Test User", savedMessage.getName());
        assertEquals("testuser@abv.bg", savedMessage.getEmail());
        assertEquals("New message", savedMessage.getMessage());
    }
}
