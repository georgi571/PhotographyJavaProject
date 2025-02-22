package bg.photographyjava.challenge.service.impl;

import bg.photographyjava.challenge.model.Challenge;
import bg.photographyjava.challenge.model.Comment;
import bg.photographyjava.challenge.model.Picture;
import bg.photographyjava.challenge.property.enums.ChallengeActivity;
import bg.photographyjava.challenge.property.enums.ChallengeType;
import bg.photographyjava.challenge.repository.ChallengeRepository;
import bg.photographyjava.challenge.service.ChallengeService;
import bg.photographyjava.challenge.service.CommentService;
import bg.photographyjava.challenge.service.PictureService;
import bg.photographyjava.user.model.Report;
import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.user.service.ReportService;
import bg.photographyjava.user.service.UserService;
import bg.photographyjava.web.dto.*;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final PictureService pictureService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final CommentService commentService;
    private final ReportService reportService;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository, PictureService pictureService, ModelMapper modelMapper, UserService userService, CommentService commentService, ReportService reportService) {
        this.challengeRepository = challengeRepository;
        this.pictureService = pictureService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.commentService = commentService;
        this.reportService = reportService;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void startDailyChallenge() {
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = now.format(formatter);

        String title = "Daily Challenge for " + formattedDate;

        LocalDateTime startAt = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endAt = startAt.withHour(23).withMinute(59).withSecond(59).withNano(0);

        Challenge challenge = new Challenge();
        challenge.setTitle(title);
        challenge.setDescription("Participate in today’s exciting daily photography challenge and showcase your creativity!");
        challenge.setDetails("Daily created challenge created by Gamified Photography!");
        challenge.setActivity(ChallengeActivity.ACTIVE);
        challenge.setType(ChallengeType.DAILY);
        challenge.setStartAt(startAt);
        challenge.setEndAt(endAt);

        challengeRepository.saveAndFlush(challenge);
    }

    @Override
    @Scheduled(cron = "59 59 23 * * *")
    public void checkAndSetWinners() {
        LocalDateTime now = LocalDateTime.now();

        List<Challenge> challenges = challengeRepository.findByEndAtBeforeAndWinnersIsNull(now);

        for (Challenge challenge : challenges) {
            setChallengeWinners(challenge.getId());
        }
    }

    @Override
    public void setChallengeWinners(UUID challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow();

        List<Picture> pictures = this.pictureService.getWinnersPicture(challengeId);

        List<UserEntity> topUsers = new ArrayList<>();
        for (int i = 0; i < Math.min(3, pictures.size()); i++) {
            Picture topPicture = pictures.get(i);
            UserEntity user = topPicture.getUser();
            topUsers.add(user);
        }
        challenge.setWinners(topUsers);
        challenge.setActivity(ChallengeActivity.PAST);

        challengeRepository.saveAndFlush(challenge);
    }

    @Override
    public List<ChallengeResponse> getAllChallenges() {
        LocalDateTime now = LocalDateTime.now();
        List<Challenge> challenges = challengeRepository.findAll();

        List<ChallengeResponse> challengesDTO = new ArrayList<>();
        for (Challenge challenge : challenges) {
            challengesDTO.add(this.modelMapper.map(challenge, ChallengeResponse.class));
        }

        return challengesDTO;
    }

    @Override
    public ChallengeDetailsResponse getChallengeDetails(UUID id, String username) {
        UserEntity user = this.userService.getUserByUsername(username).get();
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        ChallengeDetailsResponse challengeDetailsResponse = new ChallengeDetailsResponse();
        challengeDetailsResponse.setId(challenge.getId());
        challengeDetailsResponse.setTitle(challenge.getTitle());
        challengeDetailsResponse.setDescription(challenge.getDescription());
        challengeDetailsResponse.setDetails(challenge.getDetails());
        challengeDetailsResponse.setStartAt(challenge.getStartAt());
        challengeDetailsResponse.setEndAt(challenge.getEndAt());
        challengeDetailsResponse.setType(challenge.getType().name());
        challengeDetailsResponse.setActivity(challenge.getActivity().name());

        List<PictureDTO> pictureDTOs = challenge.getPictures().stream().filter(picture -> !picture.isDeleted())
                .map(picture -> {
                    PictureDTO pictureDTO = new PictureDTO();
                    pictureDTO.setId(picture.getId());
                    pictureDTO.setImageUrl(picture.getImageUrl());
                    pictureDTO.setLikes(picture.getLikes());
                    pictureDTO.setCaption(picture.getCaption());
                    pictureDTO.setStory(picture.getStory());
                    pictureDTO.setUser(this.modelMapper.map(picture.getUser(), UserInformationForPictureDTO.class));

                    List<CommentResponseDTO> commentsDTO = picture.getComments().stream().filter(comment -> !comment.isDeleted())
                            .map(comment -> {
                                CommentResponseDTO commentDTO = new CommentResponseDTO();
                                commentDTO.setId(comment.getId());
                                commentDTO.setAuthor(this.modelMapper.map(picture.getUser(), UserInformationForPictureDTO.class));
                                commentDTO.setText(comment.getText());
                                commentDTO.setDateTime(comment.getDateTime());
                                return commentDTO;
                            }).toList();

                    pictureDTO.setComments(commentsDTO);

                    boolean liked = picture.getLikedByUsers().contains(user);
                    pictureDTO.setLiked(liked);

                    return pictureDTO;
                })
                .collect(Collectors.toList());

        challengeDetailsResponse.setPictures(pictureDTOs);

        return challengeDetailsResponse;
    }

    @Override
    public boolean savePictureForChallenge(UUID challengeId, String pictureFilePath, String caption, String story, String username) {

        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new RuntimeException("Challenge not found"));

        UserEntity user = this.userService.getUserByUsername(username).get();

        Picture picture = new Picture();
        picture.setImageUrl(pictureFilePath);
        picture.setCaption(caption);
        picture.setStory(story);
        picture.setUser(user);
        picture.setChallenge(challenge);

        challenge.getPictures().add(picture);

        this.challengeRepository.saveAndFlush(challenge);

        return true;
    }

    @Override
    public PictureToggleDTO toggleLikePicture(UUID challengeId, UUID pictureId, String username) {
        Challenge challenge = challengeRepository.findById(challengeId).orElse(null);
        UserEntity user = this.userService.getUserByUsername(username).get();

        if (challenge != null) {
            Picture picture = this.pictureService.getPictureById(pictureId);

            if (picture != null) {
                if (picture.getLikedByUsers().contains(user)) {
                    picture.setLikes(picture.getLikes() - 1);
                    picture.getLikedByUsers().remove(user);
                } else {
                    picture.setLikes(picture.getLikes() + 1);
                    picture.getLikedByUsers().add(user);
                }
                this.pictureService.savePicture(picture);
                this.challengeRepository.saveAndFlush(challenge);

                PictureToggleDTO pictureToggleDTO = new PictureToggleDTO();
                pictureToggleDTO.setId(picture.getId());
                pictureToggleDTO.setImageUrl(picture.getImageUrl());
                pictureToggleDTO.setCaption(picture.getCaption());
                pictureToggleDTO.setLikes(picture.getLikes());
                pictureToggleDTO.setLiked(picture.getLikedByUsers().contains(user));

                return pictureToggleDTO;
            }
        }
        return null;
    }

    @Override
    public CommentResponseDTO addComment(UUID challengeId, UUID pictureId, String text, String username) {

        UserEntity user = this.userService.getUserByUsername(username).get();

        Challenge challenge = challengeRepository.findById(challengeId).orElse(null);

        if (challenge != null) {
            Picture picture = this.pictureService.getPictureById(pictureId);

            Comment comment = new Comment();
            comment.setText(text);
            comment.setAuthor(user);
            comment.setPicture(picture);
            comment.setDateTime(LocalDateTime.now());

            this.commentService.saveComment(comment);

            picture.getComments().add(comment);
            this.pictureService.savePicture(picture);

            this.challengeRepository.saveAndFlush(challenge);

            CommentResponseDTO responseDTO = new CommentResponseDTO();
            responseDTO.setText(comment.getText());
            responseDTO.setDateTime(comment.getDateTime());
            responseDTO.setAuthor(this.modelMapper.map(picture.getUser(), UserInformationForPictureDTO.class));

            return responseDTO;
        }
        return null;
    }

    @Override
    public String reportPicture(UUID challengeId, UUID pictureId, String username, String reportReason) {
        Challenge challenge = this.challengeRepository.findById(challengeId).get();
        UserEntity user = this.userService.getUserByUsername(username).get();
        Picture picture = this.pictureService.getPictureById(pictureId);

        Report report = new Report();
        report.setUser(user);
        report.setPicture(picture);
        report.setReportReason(reportReason);
        report.setCreatedAt(LocalDateTime.now());

        this.reportService.saveReport(report);

        return "Picture reported successfully";
    }

    @Override
    public String reportComment(UUID challengeId, UUID pictureId, UUID commentId, String username, String reportReason) {
        Challenge challenge = this.challengeRepository.findById(challengeId).get();
        UserEntity user = this.userService.getUserByUsername(username).get();
        Picture picture = this.pictureService.getPictureById(pictureId);
        Comment comment = this.commentService.getCommentById(commentId);

        Report report = new Report();
        report.setUser(user);
        report.setComment(comment);
        report.setPicture(picture);
        report.setReportReason(reportReason);
        report.setCreatedAt(LocalDateTime.now());

        this.reportService.saveReport(report);

        return "Comment reported successfully";
    }


    @Override
    public String deletePicture(UUID challengeId, UUID pictureId, String username) {
        Challenge challenge = this.challengeRepository.findById(challengeId).get();
        UserEntity user = this.userService.getUserByUsername(username).get();
        Picture picture = this.pictureService.getPictureById(pictureId);

        picture.setDeleted(true);
        this.pictureService.savePicture(picture);

        return "Picture deleted successfully";
    }

    @Override
    public String deleteComment(UUID challengeId, UUID pictureId, UUID commentId, String username) {
        Challenge challenge = this.challengeRepository.findById(challengeId).get();
        UserEntity user = this.userService.getUserByUsername(username).get();
        Picture picture = this.pictureService.getPictureById(pictureId);
        Comment comment = this.commentService.getCommentById(commentId);

        comment.setDeleted(true);
        this.commentService.saveComment(comment);

        return "Comment deleted successfully";
    }

    @Override
    public List<Challenge> findByType(ChallengeType challengeType) {
        return this.challengeRepository.findByType(challengeType);
    }

    @Override
    public ChallengeResponse createChallenge(CreateEventRequest createEventRequest, String username) {
        LocalDateTime startAt = createEventRequest.getStartAt().atTime(0, 0, 0);
        LocalDateTime endAt = createEventRequest.getEndAt().atTime(23, 59, 59);

        Challenge challenge = new Challenge();
        challenge.setTitle(createEventRequest.getTitle());
        challenge.setDescription(createEventRequest.getDescription());
        challenge.setDetails(createEventRequest.getDetails());
        challenge.setStartAt(startAt);
        challenge.setEndAt(endAt);
        challenge.setType(ChallengeType.valueOf(createEventRequest.getType()));
        challenge.setActivity(ChallengeActivity.UPCOMING);

        this.challengeRepository.saveAndFlush(challenge);

        return this.modelMapper.map(challenge, ChallengeResponse.class);
    }

    @Override
    public ChallengeResponse editChallenge(UUID id, EditEventRequest editEventRequest, String name) {
        Challenge challenge = this.challengeRepository.findById(id).get();

        LocalDateTime startAt = editEventRequest.getStartAt().atTime(0, 0, 0);
        LocalDateTime endAt = editEventRequest.getEndAt().atTime(23, 59, 59);

        challenge.setTitle(editEventRequest.getTitle());
        challenge.setDescription(editEventRequest.getDescription());
        challenge.setDetails(editEventRequest.getDetails());
        challenge.setStartAt(startAt);
        challenge.setEndAt(endAt);
        challenge.setType(ChallengeType.valueOf(editEventRequest.getType()));

        this.challengeRepository.saveAndFlush(challenge);

        return this.modelMapper.map(challenge, ChallengeResponse.class);
    }

    @Override
    public void deleteChallenge(UUID id, String name) {
        this.challengeRepository.deleteById(id);
    }
}