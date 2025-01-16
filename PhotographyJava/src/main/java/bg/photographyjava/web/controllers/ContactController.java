package bg.photographyjava.web.controllers;

import bg.photographyjava.web.dto.ContactMessageDTO;
import bg.photographyjava.contact.service.ContactMessageService;
import bg.photographyjava.web.dto.ContactReplayDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactMessageService contactMessageService;

    public ContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    // send to the FE all message which is still not answered

    @GetMapping("receive")
    public List<ContactMessageDTO> contactMessageDTO() {

        return this.contactMessageService.getNotAnsweredMessages();
    }

    // receive contact message from FE

    @PostMapping("receive")
    public ResponseEntity<?> registerUser(
            @RequestBody @Valid ContactMessageDTO contactMessageDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errorResponse = new HashMap<>();

            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errorResponse.put(fieldName, errorMessage);
            });

            return ResponseEntity.badRequest().body(errorResponse);
        }

        contactMessageService.receiveContactMessage(contactMessageDTO);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Contact message receive successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/reply")
    public ResponseEntity<?> sendReply(@RequestBody ContactReplayDTO contactReplayDTO, Authentication authentication) {
        String username = authentication.getName();
        this.contactMessageService.sendAnswer(contactReplayDTO, username);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Reply sent successfully!");

        return ResponseEntity.ok(response);
    }
}
