package bg.challenges.web.controllers;

import bg.challenges.challenge.service.ChallengeService;
import bg.challenges.shared.service.CloudinaryService;
import bg.challenges.web.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final CloudinaryService cloudinaryService;

    public ChallengeController(ChallengeService challengeService, CloudinaryService cloudinaryService) {
        this.challengeService = challengeService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping()
    public ResponseEntity<Void> getChallengesPage() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ChallengeResponse>> getAllChallenges() {
        return ResponseEntity.ok(this.challengeService.getAllChallenges());
    }

    @GetMapping("/{id}")
    public ChallengeDetailsResponse getChallengeDetails(@PathVariable UUID id,
                                                        Authentication authentication) {

        UUID userId = (UUID) authentication.getDetails();
        return this.challengeService.getChallengeDetails(id, userId);
    }

    @PostMapping("/{challengeId}/pictures")
    public ResponseEntity<Map<String, String>> uploadPicture(
            @PathVariable UUID challengeId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("caption") String caption,
            @RequestParam("story") String story,
            Authentication authentication) throws IOException {

        UUID userId = (UUID) authentication.getDetails();

        Map<String, Object> uploadResult = this.cloudinaryService.uploadImage(file);
        String pictureFilePath = (String) uploadResult.get("secure_url");

        this.challengeService.savePictureForChallenge(challengeId, pictureFilePath, caption, story, userId);

        Map<String, String> response = Map.of("message", "Picture successfully uploaded and associated with the challenge.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{challengeId}/pictures/{pictureId}/toggle-like")
    public ResponseEntity<PictureToggleResponse> toggleLikePicture(
            @PathVariable UUID challengeId,
            @PathVariable UUID pictureId,
            Authentication authentication) {

        UUID userId = (UUID) authentication.getDetails();

        PictureToggleResponse picture = this.challengeService.toggleLikePicture(challengeId, pictureId, userId);

        return ResponseEntity.ok(picture);
    }

    @PostMapping("/{challengeId}/pictures/{pictureId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable UUID challengeId,
            @PathVariable UUID pictureId,
            @RequestBody CommentRequest commentRequestDTO,
            Authentication authentication) {

        UUID userId = (UUID) authentication.getDetails();

        CommentResponse newComment = this.challengeService.addComment(challengeId, pictureId, commentRequestDTO.getText(), userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
    }

    @DeleteMapping("/{challengeId}/pictures/{pictureId}/delete")
    public ResponseEntity<Void> deletePicture(@PathVariable UUID challengeId,
                                           @PathVariable UUID pictureId,
                                           Authentication authentication) {

        UUID userId = (UUID) authentication.getDetails();
        this.challengeService.deletePicture(challengeId, pictureId, userId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{challengeId}/pictures/{pictureId}/comments/{commentId}/delete")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID challengeId,
                                           @PathVariable UUID pictureId,
                                           @PathVariable UUID commentId,
                                           Authentication authentication) {
        UUID userId = (UUID) authentication.getDetails();
        this.challengeService.deleteComment(challengeId, pictureId, commentId, userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create-challenge")
    public ResponseEntity<ChallengeResponse> createChallenge(@RequestBody CreateChallengeRequest createChallengeRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.challengeService.createChallenge(createChallengeRequest));
    }

    @PutMapping("/edit-challenge/{id}")
    public ResponseEntity<ChallengeResponse> editChallenge(@PathVariable UUID id,
                                                           @RequestBody EditChallengeRequest editChallengeRequest) {

        return ResponseEntity.ok(this.challengeService.editChallenge(id, editChallengeRequest));
    }

    @DeleteMapping("/delete-challenge/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable UUID id) {
        this.challengeService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }
}
