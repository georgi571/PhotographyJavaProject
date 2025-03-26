package bg.contacts.contact.service.impl;

import bg.contacts.contact.model.ContactMessage;
import bg.contacts.contact.repository.ContactMessageRepository;
import bg.contacts.contact.service.EmailService;
import bg.contacts.exception.ResourceNotFoundException;
import bg.contacts.web.dto.ContactMessageRequest;
import bg.contacts.web.dto.ContactMessageResponse;
import bg.contacts.web.dto.ContactReplayRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactMessageServiceImplUTest {
    @Mock
    private ContactMessageRepository contactMessageRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ContactMessageServiceImpl contactMessageService;

    private ContactMessage contactMessage;
    private UUID messageId;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        messageId = UUID.randomUUID();
        adminId = UUID.randomUUID();
        contactMessage = new ContactMessage();
        contactMessage.setId(messageId);
        contactMessage.setName("Test User");
        contactMessage.setEmail("testuser@abv.bg");
        contactMessage.setMessage("Test message");
        contactMessage.setSentAt(LocalDateTime.now());
        contactMessage.setAnswered(false);
        contactMessage.setDeleted(false);
    }

    @Test
    void testReceiveContactMessage() {
        ContactMessageRequest request = new ContactMessageRequest();
        when(contactMessageRepository.saveAndFlush(any())).thenReturn(contactMessage);
        ContactMessageResponse response = contactMessageService.receiveContactMessage(request);
        assertNotNull(response);
        verify(contactMessageRepository, times(1)).saveAndFlush(any(ContactMessage.class));
    }

    @Test
    void testGetAllMessages() {
        when(contactMessageRepository.findAll()).thenReturn(List.of(contactMessage));
        List<ContactMessageResponse> messages = contactMessageService.getAllMessages();
        assertEquals(1, messages.size());
        verify(contactMessageRepository, times(1)).findAll();
    }

    @Test
    void testSendAnswer() {
        ContactReplayRequest replayRequest = new ContactReplayRequest();
        replayRequest.setId(messageId);
        replayRequest.setAnswer("Test Answer");

        when(contactMessageRepository.findById(messageId)).thenReturn(Optional.of(contactMessage));
        when(contactMessageRepository.saveAndFlush(contactMessage)).thenReturn(contactMessage);

        ContactMessageResponse response = contactMessageService.sendAnswer(replayRequest, adminId);
        assertTrue(contactMessage.isAnswered());
        assertEquals(adminId, contactMessage.getWhoAnswer());
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        verify(contactMessageRepository, times(1)).saveAndFlush(contactMessage);
    }

    @Test
    void testSendAnswer_MessageNotFound() {
        ContactReplayRequest replayRequest = new ContactReplayRequest();
        replayRequest.setId(messageId);
        when(contactMessageRepository.findById(messageId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> contactMessageService.sendAnswer(replayRequest, adminId));
    }

    @Test
    void testDeleteMessage() {
        when(contactMessageRepository.findById(messageId)).thenReturn(Optional.of(contactMessage));
        when(contactMessageRepository.saveAndFlush(contactMessage)).thenReturn(contactMessage);

        ContactMessageResponse response = contactMessageService.deleteMessage(messageId, adminId);
        assertTrue(contactMessage.isDeleted());
        assertEquals(adminId, contactMessage.getWhoDelete());
        verify(contactMessageRepository, times(1)).saveAndFlush(contactMessage);
    }

    @Test
    void testDeleteMessage_MessageNotFound() {
        when(contactMessageRepository.findById(messageId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> contactMessageService.deleteMessage(messageId, adminId));
    }

    @Test
    void testMarkOldMessagesAsDeleted() {
        when(contactMessageRepository.findByIsAnsweredTrueAndIsDeletedFalseAndSentAtBefore(any(LocalDateTime.class)))
                .thenReturn(List.of(contactMessage));
        contactMessageService.markOldMessagesAsDeleted();
        assertTrue(contactMessage.isDeleted());
        verify(contactMessageRepository, times(1)).saveAndFlush(contactMessage);
    }

    @Test
    void testPermanentlyDeleteOldMessages() {
        doNothing().when(contactMessageRepository).deleteMessagesOlderThanOneMonth(any(LocalDateTime.class));

        contactMessageService.permanentlyDeleteOldMessages();

        verify(contactMessageRepository, times(1)).deleteMessagesOlderThanOneMonth(any(LocalDateTime.class));
    }
}