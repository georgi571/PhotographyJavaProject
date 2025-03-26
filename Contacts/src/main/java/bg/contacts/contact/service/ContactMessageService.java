package bg.contacts.contact.service;

import bg.contacts.web.dto.ContactMessageRequest;
import bg.contacts.web.dto.ContactMessageResponse;
import bg.contacts.web.dto.ContactReplayRequest;

import java.util.List;
import java.util.UUID;

public interface ContactMessageService {

    ContactMessageResponse receiveContactMessage(ContactMessageRequest contactMessageRequest);

    List<ContactMessageResponse> getAllMessages();

    ContactMessageResponse sendAnswer(ContactReplayRequest contactReplayRequest, UUID adminId);

    ContactMessageResponse deleteMessage(UUID id, UUID adminId);
}
