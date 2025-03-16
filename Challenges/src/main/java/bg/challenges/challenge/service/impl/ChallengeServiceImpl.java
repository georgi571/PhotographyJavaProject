package bg.challenges.challenge.service.impl;

import bg.challenges.challenge.model.Challenge;
import bg.challenges.challenge.model.Comment;
import bg.challenges.challenge.model.Picture;
import bg.challenges.challenge.property.enums.ChallengeActivity;
import bg.challenges.challenge.property.enums.ChallengeType;
import bg.challenges.challenge.repository.ChallengeRepository;
import bg.challenges.challenge.service.ChallengeService;
import bg.challenges.challenge.service.CommentService;
import bg.challenges.challenge.service.PictureService;
import bg.challenges.exception.*;
import bg.challenges.web.dto.*;
import bg.challenges.web.mapper.DtoMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final PictureService pictureService;
    private final CommentService commentService;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository, PictureService pictureService, CommentService commentService) {
        this.challengeRepository = challengeRepository;
        this.pictureService = pictureService;
        this.commentService = commentService;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void startDailyChallenge() {
        LocalDateTime now = LocalDateTime.now();

        Challenge upcomingChallenge = createNewDailyChallenge(now);

        this.challengeRepository.saveAndFlush(upcomingChallenge);

        List<Challenge> startingChallenges = this.challengeRepository.findByStartAt(now);

        for (Challenge challenge : startingChallenges) {
            challenge.setActivity(ChallengeActivity.ACTIVE);
            this.challengeRepository.saveAndFlush(upcomingChallenge);
        }

        List<Challenge> finishedChallenges = this.challengeRepository.findByEndAtBeforeAndWinnersIsNull(now);

        for (Challenge challenge : finishedChallenges) {
            setChallengeWinners(challenge.getId());
            this.challengeRepository.saveAndFlush(upcomingChallenge);
        }
    }

    private static Challenge createNewDailyChallenge(LocalDateTime time) {
        LocalDateTime tomorrow = time.plusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = tomorrow.format(formatter);

        String title = "Daily Challenge for " + formattedDate;

        LocalDateTime startAt = tomorrow.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endAt = tomorrow.withHour(23).withMinute(59).withSecond(59).withNano(0);

        Challenge challenge = new Challenge();
        challenge.setTitle(title);
        challenge.setDescription("Participate in today’s exciting daily photography challenge and showcase your creativity!");
        challenge.setDetails("Daily created challenge created by Gamified Photography!");
        challenge.setActivity(ChallengeActivity.UPCOMING);
        challenge.setType(ChallengeType.DAILY);
        challenge.setStartAt(startAt);
        challenge.setEndAt(endAt);
        return challenge;
    }

    @Override
    public void setChallengeWinners(UUID challengeId) {
        Challenge challenge = this.challengeRepository.findById(challengeId).orElseThrow();

        List<Picture> pictures = this.pictureService.getWinnersPicture(challengeId);

        List<String> topUsers = new ArrayList<>();
        for (int i = 0; i < Math.min(3, pictures.size()); i++) {
            Picture topPicture = pictures.get(i);
            String username = topPicture.getAuthorId().toString();
            topUsers.add(username);
        }
        challenge.setWinners(topUsers);
        challenge.setActivity(ChallengeActivity.PAST);

        challengeRepository.saveAndFlush(challenge);
    }

    @Override
    public List<ChallengeResponse> getAllChallenges() {
        List<Challenge> challenges = challengeRepository.findAll();

        List<ChallengeResponse> challengesDTO = new ArrayList<>();
        for (Challenge challenge : challenges) {
            challengesDTO.add(DtoMapper.mapChallengeToChallengeResponse(challenge));
        }

        return challengesDTO;
    }

    @Override
    public ChallengeDetailsResponse getChallengeDetails(UUID id, UUID userId) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        return DtoMapper.mapChallengeToChallengeDetailsResponse(challenge, userId);
    }

    @Override
    public void savePictureForChallenge(UUID challengeId, String pictureFilePath, String caption, String story, UUID userId) {

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));

        ChallengeActivity activityStatus = challenge.getActivity();

        if (activityStatus == ChallengeActivity.UPCOMING) {
            throw new ChallengeNotStartException("Cannot upload picture. Challenge has not started yet.");
        }

        if (activityStatus == ChallengeActivity.PAST) {
            throw new ChallengeAlreadyFinishException("Cannot upload picture. Challenge has already finished.");
        }

        Picture picture = DtoMapper.mapPictureUploadRequestToPicture(challenge, pictureFilePath, caption, story, userId);

        this.pictureService.savePicture(picture);

        challenge.getPictures().add(picture);

        this.challengeRepository.saveAndFlush(challenge);
    }

    @Override
    public PictureToggleResponse toggleLikePicture(UUID challengeId, UUID pictureId, UUID userId) {
        Challenge challenge = this.challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));

        Picture picture = this.pictureService.getPictureById(pictureId);

        if (picture.getLikedByUsers().contains(userId)) {
            picture.setLikes(picture.getLikes() - 1);
            picture.getLikedByUsers().remove(userId);
        } else {
            picture.setLikes(picture.getLikes() + 1);
            picture.getLikedByUsers().add(userId);
        }

        pictureService.savePicture(picture);

        return DtoMapper.mapPictureToPictureToggleResponse(picture, userId);
    }

    @Override
    public CommentResponse addComment(UUID challengeId, UUID pictureId, String text, UUID userId) {

        Challenge challenge = this.challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));

        Picture picture = this.pictureService.getPictureById(pictureId);

        Comment comment = DtoMapper.mapCommentResponseToComment(picture, text, userId);

        this.commentService.saveComment(comment);

        picture.getComments().add(comment);
        this.pictureService.savePicture(picture);

        return DtoMapper.mapCommentToCommentResponse(comment);
    }


    @Override
    public void deletePicture(UUID challengeId, UUID pictureId, UUID userId) {

        Challenge challenge = this.challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));

        Picture picture = this.pictureService.getPictureById(pictureId);

        if (!picture.getAuthorId().equals(userId)) {
            throw new UnauthorizedActionException("User is not authorized to delete this picture");
        }

        picture.setDeleted(true);
        this.pictureService.savePicture(picture);
    }

    @Override
    public void deleteComment(UUID challengeId, UUID pictureId, UUID commentId, UUID userId) {

        Challenge challenge = this.challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));

        Picture picture = this.pictureService.getPictureById(pictureId);

        Comment comment = this.commentService.getCommentById(commentId);

        if (!comment.getAuthorId().equals(userId)) {
            throw new UnauthorizedActionException("User is not authorized to delete this comment");
        }

        comment.setDeleted(true);
        this.commentService.saveComment(comment);
    }

    @Override
    public List<Challenge> findByType(ChallengeType challengeType) {
        return this.challengeRepository.findByType(challengeType);
    }

    @Override
    public ChallengeResponse createChallenge(CreateChallengeRequest createChallengeRequest) {
        Challenge challenge = DtoMapper.mapCreateChallengeRequestToChallenge(createChallengeRequest);

        this.challengeRepository.saveAndFlush(challenge);

        return DtoMapper.mapChallengeToChallengeResponse(challenge);
    }

    @Override
    public ChallengeResponse editChallenge(UUID id, EditChallengeRequest editChallengeRequest) {
        Challenge challenge = this.challengeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + id));

        ChallengeActivity activityStatus = challenge.getActivity();

        if (activityStatus == ChallengeActivity.ACTIVE) {
            throw new ChallengeAlreadyStartException("Cannot edit challenge which is already start");
        }

        if (activityStatus == ChallengeActivity.PAST) {
            throw new ChallengeAlreadyFinishException("Cannot edit challenge which is already finished");
        }

        DtoMapper.mapEditChallengeRequestToChallenge(challenge, editChallengeRequest);

        this.challengeRepository.saveAndFlush(challenge);

        return DtoMapper.mapChallengeToChallengeResponse(challenge);
    }

    @Override
    public void deleteChallenge(UUID id) {
        Challenge challenge = this.challengeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + id));

        ChallengeActivity activityStatus = challenge.getActivity();

        if (activityStatus == ChallengeActivity.ACTIVE) {
            throw new ChallengeAlreadyStartException("Cannot delete challenge which is already start");
        }

        if (activityStatus == ChallengeActivity.PAST) {
            throw new ChallengeAlreadyFinishException("Cannot delete challenge which is already finished");
        }

        this.challengeRepository.deleteById(id);
    }
}