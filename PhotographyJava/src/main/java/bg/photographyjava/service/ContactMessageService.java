package bg.photographyjava.service;

import bg.photographyjava.model.dto.ContactMessageDTO;

public interface ContactMessageService {

    void receiveContactMessage(ContactMessageDTO contactMessageDTO);
}
