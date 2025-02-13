package bg.photographyjava.web.controllers;

import bg.photographyjava.web.dto.ContactMessageRequest;
import bg.photographyjava.contact.service.ContactMessageService;
import bg.photographyjava.web.dto.ContactReplayRequest;
import bg.photographyjava.web.dto.ContactUserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    private final ContactMessageService contactMessageService;

    public ContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    // send to the FE all message which is still not answered

    @GetMapping("/receive")
    public ResponseEntity<List<ContactMessageRequest>> getUnansweredMessages() {

        return ResponseEntity.ok(this.contactMessageService.getNotAnsweredMessages());
    }

    // receive contact message from FE

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
            @RequestBody ContactReplayRequest contactReplayRequest,
            Authentication authentication) {

        this.contactMessageService.sendAnswer(contactReplayRequest, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Reply sent successfully!"));
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteMessage(
            @PathVariable UUID id,
            Authentication authentication) {

        this.contactMessageService.deleteMessage(id, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Message successfully marked as deleted"));
    }

    @GetMapping("/user-info")
    public ResponseEntity<ContactUserResponse> getUserInfo(Authentication authentication) {
        if (authentication != null) {
            ContactUserResponse user = this.contactMessageService.getUserDetails(authentication.getName());
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
