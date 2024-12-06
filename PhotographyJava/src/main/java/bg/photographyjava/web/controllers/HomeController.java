package bg.photographyjava.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<String> getInfo() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/home")
    public ResponseEntity<String> goToHome() {
        return ResponseEntity.ok().build();
    }
}
