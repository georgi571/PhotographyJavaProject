package bg.photographyjava.web.controllers;

import bg.photographyjava.challenge.service.ChallengeService;
import bg.photographyjava.web.dto.ChallengeDTO;
import bg.photographyjava.web.dto.ChallengeDetailsDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping("/list")
    public List<ChallengeDTO> getAllChallenges() {
        return this.challengeService.getAllChallenges();
    }

    @GetMapping("/{id}")
    public ChallengeDetailsDTO getChallengeDetails(@PathVariable UUID id) {
        return this.challengeService.getChallengeDetails(id);
    }
}
