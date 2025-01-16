package bg.photographyjava.contact.service;

import bg.photographyjava.web.dto.ContactMessageDTO;
import bg.photographyjava.web.dto.ContactReplayDTO;

import java.util.List;
import java.util.UUID;

public interface ContactMessageService {

    void receiveContactMessage(ContactMessageDTO contactMessageDTO);

    List<ContactMessageDTO> getNotAnsweredMessages();

    ContactMessageDTO getContactMessageByID(UUID id);

    void sendAnswer(ContactReplayDTO contactReplayDTO, String username);
}
