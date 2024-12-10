package bg.photographyjava.service.impl;

import bg.photographyjava.model.dto.ContactMessageDTO;
import bg.photographyjava.model.entity.ContactMessage;
import bg.photographyjava.repository.ContactMessageRepository;
import bg.photographyjava.service.ContactMessageService;
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
