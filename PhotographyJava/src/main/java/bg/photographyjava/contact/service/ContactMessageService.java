package bg.photographyjava.contact.service;

import bg.photographyjava.web.dto.ContactMessageDTO;

public interface ContactMessageService {

    void receiveContactMessage(ContactMessageDTO contactMessageDTO);
}
