package bg.contacts.web.controllers;

import bg.contacts.contact.service.ContactMessageService;
import bg.contacts.web.dto.ContactMessageRequest;
import bg.contacts.web.dto.ContactMessageResponse;
import bg.contacts.web.dto.ContactReplayRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    private final ContactMessageService contactMessageService;

    public ContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @GetMapping()
    public ResponseEntity<Void> getLeaderboardsPage() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/receive")
    public ResponseEntity<List<ContactMessageResponse>> getAllMessages() {

        return ResponseEntity.ok(this.contactMessageService.getAllMessages());
    }

    @PostMapping("/receive")
    public ResponseEntity<ContactMessageResponse> receiveMessage(
            @RequestBody ContactMessageRequest contactMessageRequest) {

        ContactMessageResponse response = this.contactMessageService.receiveContactMessage(contactMessageRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/reply")
    public ResponseEntity<ContactMessageResponse> sendReply(@RequestBody @Valid ContactReplayRequest contactReplayRequest,
                                                            Authentication authentication) {
        UUID adminId = (UUID) authentication.getDetails();
        ContactMessageResponse response = this.contactMessageService.sendAnswer(contactReplayRequest, adminId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ContactMessageResponse> deleteMessage(@PathVariable UUID id,
                                                                Authentication authentication) {
        UUID adminId = (UUID) authentication.getDetails();
        ContactMessageResponse response = this.contactMessageService.deleteMessage(id, adminId);

        return ResponseEntity.ok(response);
    }
}
