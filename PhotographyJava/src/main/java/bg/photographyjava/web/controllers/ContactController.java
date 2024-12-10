package bg.photographyjava.web.controllers;

import bg.photographyjava.model.dto.ContactMessageDTO;
import bg.photographyjava.service.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactMessageService contactMessageService;

    public ContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    // receive contact message from FE

    @PostMapping("send")
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
}
