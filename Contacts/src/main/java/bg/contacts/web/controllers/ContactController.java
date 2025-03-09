package bg.contacts.web.controllers;

import bg.contacts.contact.service.ContactMessageService;
import bg.contacts.exception.ResourceNotFoundException;
import bg.contacts.web.dto.ContactMessageRequest;
import bg.contacts.web.dto.ContactMessageResponse;
import bg.contacts.web.dto.ContactReplayRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    private final ContactMessageService contactMessageService;

    public ContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @GetMapping("/receive")
    public ResponseEntity<List<ContactMessageResponse>> getAllMessages() {

        return ResponseEntity.ok(this.contactMessageService.getAllMessages());
    }

    @PostMapping("/receive")
    public ResponseEntity<Map<String, String>> receiveMessage(
            @RequestBody @Valid ContactMessageRequest contactMessageRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errorResponse = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            error -> Optional.ofNullable(error.getDefaultMessage()).orElse("Unknown validation error")
                    ));

            return ResponseEntity.badRequest().body(errorResponse);
        }

        this.contactMessageService.receiveContactMessage(contactMessageRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Contact message received successfully"));
    }


    @PostMapping("/reply")
    public ResponseEntity<Map<String, String>> sendReply(
            @RequestBody @Valid ContactReplayRequest contactReplayRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errorResponse = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            error -> Optional.ofNullable(error.getDefaultMessage()).orElse("Unknown validation error")
                    ));

            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            this.contactMessageService.sendAnswer(contactReplayRequest);
            return ResponseEntity.ok(Map.of("message", "Reply sent successfully!"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteMessage(
            @PathVariable UUID id) {

        try {
            this.contactMessageService.deleteMessage(id);
            return ResponseEntity.ok(Map.of("message", "Message successfully marked as deleted"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
