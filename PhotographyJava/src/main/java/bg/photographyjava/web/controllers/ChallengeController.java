package bg.photographyjava.web.controllers;

import bg.photographyjava.challenge.service.ChallengeService;
import bg.photographyjava.shared.service.CloudinaryService;
import bg.photographyjava.web.dto.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final CloudinaryService cloudinaryService;

    public ChallengeController(ChallengeService challengeService, CloudinaryService cloudinaryService) {
        this.challengeService = challengeService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/list")
    public List<ChallengeDTO> getAllChallenges() {
        return this.challengeService.getAllChallenges();
    }

    @GetMapping("/{id}")
    public ChallengeDetailsDTO getChallengeDetails(@PathVariable UUID id, Authentication authentication) {
        String username = authentication.getName();
        return this.challengeService.getChallengeDetails(id, username);
    }

    @PostMapping("/{challengeId}/pictures")
    public ResponseEntity<Map<String, String>> uploadPicture(
            @PathVariable UUID challengeId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("caption") String caption,
            @RequestParam("story") String story,
            Authentication authentication) {

        String username = authentication.getName();

        try {
            Map<String, Object> uploadResult = this.cloudinaryService.uploadImage(file);

            String pictureFilePath = (String) uploadResult.get("secure_url");

            boolean saved = this.challengeService.savePictureForChallenge(challengeId, pictureFilePath, caption, story, username);

            if (saved) {
                Map<String, String> response = Map.of(
                        "message", "Picture successfully uploaded and associated with the challenge."
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                Map<String, String> response = Map.of(
                        "message", "Failed to associate picture with the challenge."
                );
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (IOException e) {
            Map<String, String> response = Map.of(
                    "message", "Failed to upload picture: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{challengeId}/pictures/{pictureId}/toggle-like")
    public ResponseEntity<?> toggleLikePicture(
            @PathVariable UUID challengeId,
            @PathVariable UUID pictureId,
            Authentication authentication) {
        String username = authentication.getName();
        PictureToggleDTO picture = this.challengeService.toggleLikePicture(challengeId, pictureId, username);
        if (picture != null) {
            return ResponseEntity.ok(picture);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Picture not found");
        }
    }

    @PostMapping("/{challengeId}/pictures/{pictureId}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable UUID challengeId,
            @PathVariable UUID pictureId,
            @RequestBody CommentRequestDTO commentRequestDTO,
            Authentication authentication) {

        String username = authentication.getName();

        CommentResponseDTO newComment = this.challengeService.addComment(challengeId, pictureId, commentRequestDTO.getText(), username);

        if (newComment != null) {
            return ResponseEntity.ok(newComment);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // Report a Picture
    @PostMapping("/{challengeId}/pictures/{pictureId}/report")
    public ResponseEntity<?> reportPicture(@PathVariable UUID challengeId,
                                                @PathVariable UUID pictureId,
                                                @RequestBody CommentRequestDTO reason,
                                                Authentication authentication) {
        String username = authentication.getName();
        try {
            this.challengeService.reportPicture(challengeId, pictureId, username, reason.getText());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Picture reported successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while reporting the picture");
        }
    }

    // Report a Comment
    @PostMapping("/{challengeId}/pictures/{pictureId}/comments/{commentId}/report")
    public ResponseEntity<?> reportComment(@PathVariable UUID challengeId,
                                                @PathVariable UUID pictureId,
                                                @PathVariable UUID commentId,
                                                @RequestBody CommentRequestDTO reason,
                                                Authentication authentication) {
        String username = authentication.getName();
        try {
            this.challengeService.reportComment(challengeId, pictureId, commentId, username, reason.getText());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Comment reported successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while reporting the comment");
        }
    }

    // Delete a Picture
    @DeleteMapping("/{challengeId}/pictures/{pictureId}/delete")
    public ResponseEntity<?> deletePicture(@PathVariable UUID challengeId,
                                                @PathVariable UUID pictureId,
                                                Authentication authentication) {
        String username = authentication.getName();
        try {
            this.challengeService.deletePicture(challengeId, pictureId, username);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Picture deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the picture");
        }
    }

    // Delete a Comment
    @DeleteMapping("/{challengeId}/pictures/{pictureId}/comments/{commentId}/delete")
    public ResponseEntity<?> deleteComment(@PathVariable UUID challengeId,
                                                @PathVariable UUID pictureId,
                                                @PathVariable UUID commentId,
                                                Authentication authentication) {
        String username = authentication.getName();
        try {
            this.challengeService.deleteComment(challengeId, pictureId, commentId, username);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Comment deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the comment");
        }
    }

}
