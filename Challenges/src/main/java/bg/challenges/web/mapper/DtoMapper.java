package bg.challenges.web.mapper;

import bg.challenges.challenge.model.Challenge;
import bg.challenges.challenge.model.Comment;
import bg.challenges.challenge.model.Picture;
import bg.challenges.challenge.model.ChallengeActivity;
import bg.challenges.challenge.model.ChallengeType;
import bg.challenges.web.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DtoMapper {

    public static ChallengeResponse mapChallengeToChallengeResponse(Challenge challenge) {

        ChallengeResponse challengeResponse = new ChallengeResponse();
        challengeResponse.setId(challenge.getId());
        challengeResponse.setTitle(challenge.getTitle());
        challengeResponse.setDescription(challenge.getDescription());
        challengeResponse.setActivity(challenge.getActivity().name());
        challengeResponse.setDetails(challenge.getDetails());
        challengeResponse.setType(challenge.getType().name());
        challengeResponse.setEndAt(challenge.getEndAt());
        challengeResponse.setStartAt(challenge.getStartAt());

        return challengeResponse;
    }

    public static ChallengeDetailsResponse mapChallengeToChallengeDetailsResponse(Challenge challenge, UUID userId) {

        ChallengeDetailsResponse challengeDetailsResponse = new ChallengeDetailsResponse();
        challengeDetailsResponse.setId(challenge.getId());
        challengeDetailsResponse.setTitle(challenge.getTitle());
        challengeDetailsResponse.setDescription(challenge.getDescription());
        challengeDetailsResponse.setDetails(challenge.getDetails());
        challengeDetailsResponse.setStartAt(challenge.getStartAt());
        challengeDetailsResponse.setEndAt(challenge.getEndAt());
        challengeDetailsResponse.setType(challenge.getType().name());
        challengeDetailsResponse.setActivity(challenge.getActivity().name());

        List<PictureResponse> pictureResponseList = challenge.getPictures().stream().filter(picture -> !picture.isDeleted())
                .map(picture -> mapPictureToPictureResponse(picture, userId))
                .toList();

        challengeDetailsResponse.setPictures(pictureResponseList);

        return challengeDetailsResponse;
    }

    public static PictureResponse mapPictureToPictureResponse(Picture picture, UUID userId) {
        PictureResponse pictureResponse = new PictureResponse();
        pictureResponse.setId(picture.getId());
        pictureResponse.setImageUrl(picture.getImageUrl());
        pictureResponse.setLikes(picture.getLikes());
        pictureResponse.setCaption(picture.getCaption());
        pictureResponse.setStory(picture.getStory());
        pictureResponse.setAuthorId(picture.getAuthorId());

        List<CommentResponse> commentResponseList = picture.getComments().stream().filter(comment -> !comment.isDeleted())
                .map(DtoMapper::mapCommentToCommentResponse).toList();

        pictureResponse.setComments(commentResponseList);

        boolean liked = picture.getLikedByUsers().contains(userId);
        pictureResponse.setLiked(liked);

        return pictureResponse;
    }

    public static CommentResponse mapCommentToCommentResponse(Comment comment) {
        CommentResponse commentDTO = new CommentResponse();
        commentDTO.setId(comment.getId());
        commentDTO.setAuthorId(comment.getAuthorId());
        commentDTO.setText(comment.getText());
        commentDTO.setDateTime(comment.getDateTime());

        return commentDTO;
    }

    public static PictureToggleResponse mapPictureToPictureToggleResponse(Picture picture, UUID userId) {
        PictureToggleResponse pictureToggleDTO = new PictureToggleResponse();
        pictureToggleDTO.setId(picture.getId());
        pictureToggleDTO.setImageUrl(picture.getImageUrl());
        pictureToggleDTO.setCaption(picture.getCaption());
        pictureToggleDTO.setLikes(picture.getLikes());
        pictureToggleDTO.setLiked(picture.getLikedByUsers().contains(userId));

        return pictureToggleDTO;
    }

    public static Comment mapCommentResponseToComment(Picture picture, String text, UUID userId) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setAuthorId(userId);
        comment.setPicture(picture);
        comment.setDateTime(LocalDateTime.now());

        return comment;
    }

    public static Challenge mapCreateChallengeRequestToChallenge(CreateChallengeRequest createChallengeRequest) {
        LocalDateTime startAt = createChallengeRequest.getStartAt().atTime(0, 0, 0);
        LocalDateTime endAt = createChallengeRequest.getEndAt().atTime(23, 59, 59);

        Challenge challenge = new Challenge();
        challenge.setTitle(createChallengeRequest.getTitle());
        challenge.setDescription(createChallengeRequest.getDescription());
        challenge.setDetails(createChallengeRequest.getDetails());
        challenge.setStartAt(startAt);
        challenge.setEndAt(endAt);
        challenge.setType(ChallengeType.valueOf(createChallengeRequest.getType()));
        challenge.setActivity(ChallengeActivity.UPCOMING);

        return challenge;
    }

    public static void mapEditChallengeRequestToChallenge(Challenge challenge, EditChallengeRequest editChallengeRequest) {
        LocalDateTime startAt = editChallengeRequest.getStartAt().atTime(0, 0, 0);
        LocalDateTime endAt = editChallengeRequest.getEndAt().atTime(23, 59, 59);

        challenge.setTitle(editChallengeRequest.getTitle());
        challenge.setDescription(editChallengeRequest.getDescription());
        challenge.setDetails(editChallengeRequest.getDetails());
        challenge.setStartAt(startAt);
        challenge.setEndAt(endAt);
        challenge.setType(ChallengeType.valueOf(editChallengeRequest.getType()));

    }

    public static Picture mapPictureUploadRequestToPicture(Challenge challenge ,String pictureFilePath, String caption, String story, UUID userId) {
        Picture picture = new Picture();
        picture.setImageUrl(pictureFilePath);
        picture.setCaption(caption);
        picture.setStory(story);
        picture.setAuthorId(userId);
        picture.setChallenge(challenge);

        return picture;
    }

    public static CommentReportResponse mapCommentToCommentReportTResponse(Comment comment) {

        CommentReportResponse commentReportResponse = new CommentReportResponse();
        commentReportResponse.setId(comment.getId());
        commentReportResponse.setText(comment.getText());
        commentReportResponse.setChallengeId(comment.getPicture().getChallenge().getId());
        commentReportResponse.setImageUrl(comment.getPicture().getImageUrl());
        commentReportResponse.setAuthorId(comment.getAuthorId());

        return commentReportResponse;
    }

    public static PictureReportResponse mapPictureToPictureReportResponse(Picture picture) {

        PictureReportResponse pictureReportResponse = new PictureReportResponse();
        pictureReportResponse.setId(picture.getId());
        pictureReportResponse.setImageUrl(picture.getImageUrl());
        pictureReportResponse.setChallengeId(picture.getChallenge().getId());
        pictureReportResponse.setAuthorId(picture.getAuthorId());

        return pictureReportResponse;
    }
}
