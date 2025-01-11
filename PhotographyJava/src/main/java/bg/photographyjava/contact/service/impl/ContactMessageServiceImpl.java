package bg.photographyjava.contact.service.impl;

import bg.photographyjava.web.dto.ContactMessageDTO;
import bg.photographyjava.contact.model.ContactMessage;
import bg.photographyjava.contact.repository.ContactMessageRepository;
import bg.photographyjava.contact.service.ContactMessageService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ContactMessageServiceImpl implements ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final ModelMapper modelMapper;

    public ContactMessageServiceImpl(ContactMessageRepository contactMessageRepository, ModelMapper modelMapper) {
        this.contactMessageRepository = contactMessageRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void receiveContactMessage(ContactMessageDTO contactMessageDTO) {
        ContactMessage contactMessage = this.modelMapper.map(contactMessageDTO, ContactMessage.class);
        contactMessage.setSentAt(LocalDateTime.now());
        contactMessage.setAnswered(false);
        this.contactMessageRepository.saveAndFlush(contactMessage);
    }
}
