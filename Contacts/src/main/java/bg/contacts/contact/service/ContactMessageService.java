package bg.contacts.contact.service;

import bg.contacts.web.dto.ContactMessageRequest;
import bg.contacts.web.dto.ContactMessageResponse;
import bg.contacts.web.dto.ContactReplayRequest;

import java.util.List;
import java.util.UUID;

public interface ContactMessageService {

    void receiveContactMessage(ContactMessageRequest contactMessageRequest);

    List<ContactMessageResponse> getAllMessages();

    void sendAnswer(ContactReplayRequest contactReplayRequest);

    void deleteMessage(UUID id);
}
