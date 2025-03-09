package bg.contacts.web.mapper;

import bg.contacts.contact.model.ContactMessage;
import bg.contacts.web.dto.ContactMessageRequest;
import bg.contacts.web.dto.ContactMessageResponse;

import java.time.LocalDateTime;

public class DtoMapper {

    public static ContactMessage mapContactMessageRequestToContactMessage(ContactMessageRequest contactMessageRequest) {

        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setName(contactMessageRequest.getName());
        contactMessage.setEmail(contactMessageRequest.getEmail());
        contactMessage.setMessage(contactMessageRequest.getMessage());
        contactMessage.setAnswered(false);
        contactMessage.setDeleted(false);
        contactMessage.setSentAt(LocalDateTime.now());

        return contactMessage;
    }

    public static ContactMessageResponse mapContactMessageToContactMessageResponse(ContactMessage contactMessage) {

        ContactMessageResponse contactMessageResponse = new ContactMessageResponse();
        contactMessageResponse.setId(contactMessage.getId());
        contactMessageResponse.setName(contactMessage.getName());
        contactMessageResponse.setEmail(contactMessage.getEmail());
        contactMessageResponse.setMessage(contactMessage.getMessage());
        contactMessageResponse.setAnswered(contactMessage.isAnswered());
        contactMessageResponse.setDeleted(contactMessage.isDeleted());
        contactMessageResponse.setSentAt(contactMessage.getSentAt());

        return contactMessageResponse;
    }
}
