package bg.challenges.web.controllers;

import bg.challenges.challenge.service.ChallengeService;
import bg.challenges.challenge.service.CommentService;
import bg.challenges.challenge.service.PictureService;
import bg.challenges.web.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final PictureService pictureService;
    private final CommentService commentService;

    public ChallengeController(ChallengeService challengeService, PictureService pictureService, CommentService commentService) {
        this.challengeService = challengeService;
        this.pictureService = pictureService;
        this.commentService = commentService;
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
    public ResponseEntity<ChallengeDetailsResponse> getChallengeDetails(@PathVariable UUID id,
                                                        Authentication authentication) {

        UUID userId = (UUID) authentication.getDetails();
        return ResponseEntity.ok(this.challengeService.getChallengeDetails(id, userId));
    }

    @PostMapping("/{challengeId}/pictures")
    public ResponseEntity<PictureResponse> uploadPicture(@PathVariable UUID challengeId,
                                                             @RequestParam("file") MultipartFile file,
                                                             @RequestParam("caption") String caption,
                                                             @RequestParam("story") String story,
                                                             Authentication authentication) throws IOException {

        UUID userId = (UUID) authentication.getDetails();

        PictureResponse pictureResponse = this.challengeService.savePictureForChallenge(challengeId, file, caption, story, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(pictureResponse);
    }

    @PatchMapping("/pictures/{pictureId}/toggle-like")
    public ResponseEntity<PictureToggleResponse> toggleLikePicture(@PathVariable UUID pictureId,
                                                                   Authentication authentication) {

        UUID userId = (UUID) authentication.getDetails();

        PictureToggleResponse picture = this.pictureService.toggleLikePicture(pictureId, userId);

        return ResponseEntity.ok(picture);
    }

    @PostMapping("/pictures/{pictureId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable UUID pictureId,
                                                      @RequestBody CommentRequest commentRequestDTO,
                                                      Authentication authentication) {

        UUID userId = (UUID) authentication.getDetails();

        CommentResponse newComment = this.pictureService.addComment(pictureId, commentRequestDTO.getText(), userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
    }

    @DeleteMapping("/pictures/{pictureId}")
    public ResponseEntity<Void> deletePicture(@PathVariable UUID pictureId,
                                              Authentication authentication) {

        UUID userId = (UUID) authentication.getDetails();
        this.pictureService.deletePicture(pictureId, userId, authentication);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId,
                                              Authentication authentication) {

        UUID userId = (UUID) authentication.getDetails();
        this.commentService.deleteComment(commentId, userId, authentication);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create-challenge")
    public ResponseEntity<ChallengeResponse> createChallenge(@RequestBody CreateChallengeRequest createChallengeRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.challengeService.createChallenge(createChallengeRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChallengeResponse> editChallenge(@PathVariable UUID id,
                                                           @RequestBody EditChallengeRequest editChallengeRequest) {

        return ResponseEntity.ok(this.challengeService.editChallenge(id, editChallengeRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable UUID id) {

        this.challengeService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reported/picture/{id}")
    public ResponseEntity<PictureReportResponse> getReportedPicture(@PathVariable UUID id) {

        return ResponseEntity.ok(this.pictureService.getReportedPicture(id));
    }

    @GetMapping("/reported/comment/{id}")
    public ResponseEntity<CommentReportResponse> getReportedComment(@PathVariable UUID id) {

        return ResponseEntity.ok(this.commentService.getReportedComment(id));
    }

    @GetMapping("/pictures/all/{id}")
    public ResponseEntity<List<PictureResponse>> getAllPictureForUser(@PathVariable UUID id,
                                                                      Authentication authentication) {

        UUID userId = (UUID) authentication.getDetails();
        return ResponseEntity.ok(this.pictureService.getAllPicturesByUser(id, userId));
    }
}
