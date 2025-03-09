package bg.contacts.contact.service.impl;

import bg.contacts.contact.service.EmailService;
import bg.contacts.exception.ResourceNotFoundException;
import bg.contacts.web.dto.ContactMessageRequest;
import bg.contacts.contact.model.ContactMessage;
import bg.contacts.contact.repository.ContactMessageRepository;
import bg.contacts.contact.service.ContactMessageService;
import bg.contacts.web.dto.ContactMessageResponse;
import bg.contacts.web.dto.ContactReplayRequest;
import bg.contacts.web.mapper.DtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ContactMessageServiceImpl implements ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final EmailService emailService;

    public ContactMessageServiceImpl(ContactMessageRepository contactMessageRepository, EmailService emailService) {
        this.contactMessageRepository = contactMessageRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void markOldMessagesAsDeleted() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<ContactMessage> messagesToDelete = contactMessageRepository.findByIsAnsweredTrueAndIsDeletedFalseAndSentAtBefore(oneWeekAgo);

        for (ContactMessage message : messagesToDelete) {
            message.setDeleted(true);
            contactMessageRepository.saveAndFlush(message);
        }
    }

    @Scheduled(cron = "0 0 1 * * SUN")
    @Transactional
    public void permanentlyDeleteOldMessages() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        contactMessageRepository.deleteMessagesOlderThanOneMonth(oneMonthAgo);
    }

    @Override
    public void receiveContactMessage(ContactMessageRequest contactMessageRequest) {

        ContactMessage contactMessage = DtoMapper.mapContactMessageRequestToContactMessage(contactMessageRequest);

        this.contactMessageRepository.saveAndFlush(contactMessage);
    }

    @Override
    public List<ContactMessageResponse> getAllMessages() {

        List<ContactMessageResponse> allMessages = new ArrayList<>();
        List<ContactMessage> contactMessages = this.contactMessageRepository.findAll();

        for (ContactMessage contactMessage : contactMessages) {
            allMessages.add(DtoMapper.mapContactMessageToContactMessageResponse(contactMessage));
        }

        return allMessages;
    }

    @Override
    public void sendAnswer(ContactReplayRequest contactReplayRequest) {

        ContactMessage contactMessage = this.contactMessageRepository.findById(contactReplayRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact message not found with ID: " + contactReplayRequest.getId()));


        String subject = "Response to your contact message";
        String message = "Dear " + contactMessage.getName() + ",\n\n" +
                "Thank you for your message. Here's our reply:\n\n" +
                contactReplayRequest.getAnswer() + "\n\n" +
                "Your message:\n\n" +
                contactMessage.getMessage() + "\n\n" +
                "Best regards,\nThe Gamified Photography Team";

        this.emailService.sendEmail(contactMessage.getEmail(), subject, message);

        contactMessage.setAnswer(contactReplayRequest.getAnswer());
        contactMessage.setWhoAnswer(contactReplayRequest.getUserId());
        contactMessage.setAnswered(true);

        this.contactMessageRepository.saveAndFlush(contactMessage);
    }

    @Override
    public void deleteMessage(UUID id) {

        ContactMessage contactMessage = this.contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact message not found with ID: " + id));

        contactMessage.setDeleted(true);

        this.contactMessageRepository.saveAndFlush(contactMessage);
    }
}
