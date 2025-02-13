package bg.photographyjava.contact.service;

import bg.photographyjava.web.dto.ContactMessageRequest;
import bg.photographyjava.web.dto.ContactReplayRequest;
import bg.photographyjava.web.dto.ContactUserResponse;

import java.util.List;
import java.util.UUID;

public interface ContactMessageService {

    void receiveContactMessage(ContactMessageRequest contactMessageRequest);

    List<ContactMessageRequest> getNotAnsweredMessages();

    ContactMessageRequest getContactMessageByID(UUID id);

    void sendAnswer(ContactReplayRequest contactReplayRequest, String username);

    void deleteMessage(UUID id, String username);

    ContactUserResponse getUserDetails(String username);
}
