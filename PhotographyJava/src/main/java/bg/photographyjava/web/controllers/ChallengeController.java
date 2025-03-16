package bg.photographyjava.web.controllers;

import bg.photographyjava.challenge.service.ChallengeService;
import bg.photographyjava.shared.service.CloudinaryService;
import bg.photographyjava.web.dto.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/challenges")
public class ChallengeController {

//    private final ChallengeService challengeService;
//    private final CloudinaryService cloudinaryService;
//
//    public ChallengeController(ChallengeService challengeService, CloudinaryService cloudinaryService) {
//        this.challengeService = challengeService;
//        this.cloudinaryService = cloudinaryService;
//    }
//
//    // Report a Picture
//    @PostMapping("/{challengeId}/pictures/{pictureId}/report")
//    public ResponseEntity<?> reportPicture(@PathVariable UUID challengeId,
//                                                @PathVariable UUID pictureId,
//                                                @RequestBody CommentRequestDTO reason,
//                                                Authentication authentication) {
//        String username = authentication.getName();
//        try {
//            this.challengeService.reportPicture(challengeId, pictureId, username, reason.getText());
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "Picture reported successfully");
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while reporting the picture");
//        }
//    }
//
//    // Report a Comment
//    @PostMapping("/{challengeId}/pictures/{pictureId}/comments/{commentId}/report")
//    public ResponseEntity<?> reportComment(@PathVariable UUID challengeId,
//                                                @PathVariable UUID pictureId,
//                                                @PathVariable UUID commentId,
//                                                @RequestBody CommentRequestDTO reason,
//                                                Authentication authentication) {
//        String username = authentication.getName();
//        try {
//            this.challengeService.reportComment(challengeId, pictureId, commentId, username, reason.getText());
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "Comment reported successfully");
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while reporting the comment");
//        }
//    }
}
