package bg.challenges.challenge.service.impl;

import bg.challenges.challenge.model.Comment;
import bg.challenges.challenge.model.Picture;
import bg.challenges.challenge.repository.PictureRepository;
import bg.challenges.challenge.service.CommentService;
import bg.challenges.challenge.service.PictureService;
import bg.challenges.exception.ResourceNotFoundException;
import bg.challenges.exception.UnauthorizedActionException;
import bg.challenges.web.dto.*;
import bg.challenges.web.mapper.DtoMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class PictureServiceImpl implements PictureService {

    private final PictureRepository pictureRepository;
    private final CommentService commentService;

    public PictureServiceImpl(PictureRepository pictureRepository, CommentService commentService) {
        this.pictureRepository = pictureRepository;
        this.commentService = commentService;
    }

    @Override
    public List<Picture> getWinnersPicture(UUID challengeId) {
        List<Picture> pictures = pictureRepository.findByChallengeId(challengeId);
        return pictures.stream()
                .sorted(Comparator.comparingLong(Picture::getLikes).reversed())
                .toList();
    }

    @Override
    public Picture getPictureById(UUID pictureId) {
        return this.pictureRepository.findById(pictureId)
                .orElseThrow(() -> new ResourceNotFoundException("Picture not found with id: " + pictureId));
    }

    @Override
    public void savePicture(Picture picture) {
        this.pictureRepository.saveAndFlush(picture);
    }

    @Override
    public PictureToggleResponse toggleLikePicture(UUID pictureId, UUID userId) {

        Picture picture = this.getPictureById(pictureId);

        if (picture.getLikedByUsers().contains(userId)) {
            picture.setLikes(picture.getLikes() - 1);
            picture.getLikedByUsers().remove(userId);
        } else {
            picture.setLikes(picture.getLikes() + 1);
            picture.getLikedByUsers().add(userId);
        }

        this.savePicture(picture);

        return DtoMapper.mapPictureToPictureToggleResponse(picture, userId);
    }

    @Override
    public CommentResponse addComment(UUID pictureId, String text, UUID userId) {

        Picture picture = this.getPictureById(pictureId);

        Comment comment = DtoMapper.mapCommentResponseToComment(picture, text, userId);

        this.commentService.saveComment(comment);

        picture.getComments().add(comment);
        this.savePicture(picture);

        return DtoMapper.mapCommentToCommentResponse(comment);
    }

    @Override
    public void deletePicture(UUID pictureId, UUID userId, Authentication authentication) {

        Picture picture = this.getPictureById(pictureId);

        boolean isOwner = picture.getAuthorId().equals(userId);
        boolean hasPermission = hasPermission(authentication);

        if (!isOwner && !hasPermission) {
            throw new UnauthorizedActionException("User is not authorized to delete this picture");
        }

        picture.setDeleted(true);
        this.savePicture(picture);
    }

    @Override
    public PictureReportResponse getReportedPicture(UUID id) {
        Picture picture = this.getPictureById(id);
        return DtoMapper.mapPictureToPictureReportResponse(picture);
    }

    @Override
    public List<PictureResponse> getAllPicturesByUser(UUID id, UUID userId) {
        List<Picture> pictures = this.pictureRepository.findByAuthorIdAndIsDeletedFalse(id);

        List<PictureResponse> picturesResponse = new ArrayList<>();
        for (Picture picture : pictures) {
            picturesResponse.add(DtoMapper.mapPictureToPictureResponse(picture, userId));
        }

        return picturesResponse;
    }

    private boolean hasPermission(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("PERMISSION_" + "deletePicture"));
    }

}
