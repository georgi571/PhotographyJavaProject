package bg.contacts;

import bg.contacts.contact.model.ContactMessage;
import bg.contacts.contact.repository.ContactMessageRepository;
import bg.contacts.contact.service.impl.ContactMessageServiceImpl;
import bg.contacts.web.dto.ContactMessageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class GetAllMessagesITest {
    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private ContactMessageServiceImpl contactMessageService;

    @Test
    void testGetAllMessages() {
        ContactMessage contactMessage1 = new ContactMessage();
        contactMessage1.setName("User One");
        contactMessage1.setEmail("userone@abv.bg");
        contactMessage1.setMessage("Message 1");

        ContactMessage contactMessage2 = new ContactMessage();
        contactMessage2.setName("User Two");
        contactMessage2.setEmail("usertwo@abv.bg");
        contactMessage2.setMessage("Message 2");

        contactMessageRepository.save(contactMessage1);
        contactMessageRepository.save(contactMessage2);

        List<ContactMessageResponse> response = contactMessageService.getAllMessages();

        assertNotNull(response);
        assertEquals(2, response.size());

        ContactMessageResponse messageResponse1 = response.getFirst();
        assertEquals("User One", messageResponse1.getName());
        assertEquals("userone@abv.bg", messageResponse1.getEmail());
        assertEquals("Message 1", messageResponse1.getMessage());

        ContactMessageResponse messageResponse2 = response.get(1);
        assertEquals("User Two", messageResponse2.getName());
        assertEquals("usertwo@abv.bg", messageResponse2.getEmail());
        assertEquals("Message 2", messageResponse2.getMessage());
    }
}
