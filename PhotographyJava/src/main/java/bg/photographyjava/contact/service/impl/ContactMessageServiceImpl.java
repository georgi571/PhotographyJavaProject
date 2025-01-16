package bg.photographyjava.contact.service.impl;

import bg.photographyjava.contact.service.EmailService;
import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.user.service.UserService;
import bg.photographyjava.web.dto.ContactMessageDTO;
import bg.photographyjava.contact.model.ContactMessage;
import bg.photographyjava.contact.repository.ContactMessageRepository;
import bg.photographyjava.contact.service.ContactMessageService;
import bg.photographyjava.web.dto.ContactReplayDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ContactMessageServiceImpl implements ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final UserService userService;

    public ContactMessageServiceImpl(ContactMessageRepository contactMessageRepository, ModelMapper modelMapper, EmailService emailService, UserService userService) {
        this.contactMessageRepository = contactMessageRepository;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Override
    public void receiveContactMessage(ContactMessageDTO contactMessageDTO) {
        ContactMessage contactMessage = this.modelMapper.map(contactMessageDTO, ContactMessage.class);
        contactMessage.setSentAt(LocalDateTime.now());
        contactMessage.setAnswered(false);
        contactMessage.setDeleted(false);
        this.contactMessageRepository.saveAndFlush(contactMessage);
    }

    @Override
    public List<ContactMessageDTO> getNotAnsweredMessages() {
        List<ContactMessageDTO> notAnsweredMessages = new ArrayList<>();
        List<ContactMessage> contactMessages = this.contactMessageRepository.findAll();
        for (ContactMessage contactMessage : contactMessages) {
            notAnsweredMessages.add(this.modelMapper.map(contactMessage, ContactMessageDTO.class));
        }
        return notAnsweredMessages;
    }

    @Override
    public ContactMessageDTO getContactMessageByID(UUID id) {
        ContactMessage contactMessage = this.contactMessageRepository.findById(id).get();

        return this.modelMapper.map(contactMessage, ContactMessageDTO.class);
    }

    @Override
    public void sendAnswer(ContactReplayDTO contactReplayDTO, String username) {
        ContactMessage contactMessage = this.contactMessageRepository.findById(contactReplayDTO.getId()).get();
        ContactMessageDTO contactMessageDTO = this.getContactMessageByID(contactReplayDTO.getId());
        String subject = "Response to your contact message";
        String message = "Dear " + contactMessageDTO.getName() + ",\n\n" +
                "Thank you for your message. Here's our reply:\n\n" +
                contactReplayDTO.getAnswer() + "\n\n" +
                "Your message:\n\n" +
                contactMessage.getMessage() + "\n\n" +
                "Best regards,\nThe Gamified Photography Team";
        this.emailService.sendEmail(contactMessageDTO.getEmail(), subject, message);
        UserEntity admin = this.userService.getUserByUsername(username).get();
        contactMessage.setAnswer(contactReplayDTO.getAnswer());
        contactMessage.setWhoAnswer(admin);
        contactMessage.setAnswered(true);
        this.contactMessageRepository.saveAndFlush(contactMessage);
    }

    @Override
    public void deleteMessage(UUID id, String username) {
        UserEntity admin = this.userService.getUserByUsername(username).get();
        ContactMessage contactMessage = this.contactMessageRepository.findById(id).get();
        contactMessage.setDeleted(true);

        this.contactMessageRepository.saveAndFlush(contactMessage);
    }


}
