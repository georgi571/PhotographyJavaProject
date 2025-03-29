package bg.challenges.challenge.service.impl;

import bg.challenges.challenge.model.Challenge;
import bg.challenges.challenge.model.Comment;
import bg.challenges.challenge.model.Picture;
import bg.challenges.challenge.repository.PictureRepository;
import bg.challenges.challenge.service.CommentService;
import bg.challenges.exception.ResourceNotFoundException;
import bg.challenges.exception.UnauthorizedActionException;
import bg.challenges.web.dto.CommentResponse;
import bg.challenges.web.dto.PictureReportResponse;
import bg.challenges.web.dto.PictureResponse;
import bg.challenges.web.mapper.DtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PictureServiceImplUTest {
    @Mock
    private PictureRepository pictureRepository;

    @Mock
    private CommentService commentService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PictureServiceImpl pictureService;

    private UUID pictureId;
    private UUID userId;
    private Picture picture;

    @BeforeEach
    void setUp() {
        pictureId = UUID.randomUUID();
        userId = UUID.randomUUID();
        picture = new Picture();
        picture.setId(pictureId);
        picture.setAuthorId(userId);
        picture.setLikes(10);
        picture.setLikedByUsers(new HashSet<>());
        picture.setDeleted(false);
        picture.setComments(new ArrayList<>());
    }

    @Test
    void testGetWinnersPicture_ShouldReturnSortedPictures() {
        Picture pic1 = new Picture();
        pic1.setLikes(5);
        Picture pic2 = new Picture();
        pic2.setLikes(20);

        when(pictureRepository.findByChallengeId(any(UUID.class)))
                .thenReturn(List.of(pic1, pic2));

        List<Picture> result = pictureService.getWinnersPicture(UUID.randomUUID());

        assertEquals(2, result.size());
        assertEquals(20, result.get(0).getLikes());
        assertEquals(5, result.get(1).getLikes());
    }

    @Test
    void testGetPictureById_WhenPictureExists_ShouldReturnPicture() {
        when(pictureRepository.findById(pictureId)).thenReturn(Optional.of(picture));

        Picture result = pictureService.getPictureById(pictureId);

        assertNotNull(result);
        assertEquals(pictureId, result.getId());
    }

    @Test
    void testGetPictureById_WhenPictureDoesNotExist_ShouldThrowException() {
        when(pictureRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pictureService.getPictureById(pictureId));
    }

    @Test
    void testSavePicture_ShouldCallRepositorySave() {
        pictureService.savePicture(picture);
        verify(pictureRepository, times(1)).saveAndFlush(picture);
    }

    @Test
    void testToggleLikePicture_ShouldAddLikeIfNotLiked() {
        when(pictureRepository.findById(pictureId)).thenReturn(Optional.of(picture));

        pictureService.toggleLikePicture(pictureId, userId);

        assertTrue(picture.getLikedByUsers().contains(userId));
        assertEquals(11, picture.getLikes());
        verify(pictureRepository, times(1)).saveAndFlush(picture);
    }


    @Test
    void testAddComment_ShouldSaveCommentAndReturnResponse() {
        String text = "Nice picture!";
        UUID commentId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setText(text);

        Picture picture = mock(Picture.class);
        when(picture.getComments()).thenReturn(new ArrayList<>());

        when(pictureRepository.findById(pictureId)).thenReturn(Optional.of(picture));

        MockedStatic<DtoMapper> mockedMapper = Mockito.mockStatic(DtoMapper.class);
        mockedMapper.when(() -> DtoMapper.mapCommentResponseToComment(any(), any(), any()))
                .thenReturn(comment);
        mockedMapper.when(() -> DtoMapper.mapCommentToCommentResponse(any()))
                .thenReturn(new CommentResponse());

        CommentResponse response = pictureService.addComment(pictureId, text, userId);

        assertNotNull(response);
        assertEquals(1, picture.getComments().size());
        verify(commentService, times(1)).saveComment(comment);
        verify(pictureRepository, times(1)).saveAndFlush(picture);

        mockedMapper.close();
    }

    @Test
    void testDeletePicture_WhenUserIsOwner_ShouldMarkAsDeleted() {
        when(pictureRepository.findById(pictureId)).thenReturn(Optional.of(picture));

        pictureService.deletePicture(pictureId, userId, authentication);

        assertTrue(picture.isDeleted());
        verify(pictureRepository, times(1)).saveAndFlush(picture);
    }

    @Test
    void testDeletePicture_WhenUserHasPermission_ShouldMarkAsDeleted() {
        UUID ownerId = UUID.randomUUID();
        Picture picture = new Picture();
        picture.setAuthorId(ownerId);

        when(pictureRepository.findById(pictureId)).thenReturn(Optional.of(picture));

        pictureService.deletePicture(pictureId, ownerId, authentication);

        assertTrue(picture.isDeleted());
        verify(pictureRepository, times(1)).saveAndFlush(picture);
    }

    @Test
    void testDeletePicture_WhenUserIsNotOwnerAndHasNoPermission_ShouldThrowException() {
        UUID anotherUserId = UUID.randomUUID();

        when(pictureRepository.findById(pictureId)).thenReturn(Optional.of(picture));
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

        assertThrows(UnauthorizedActionException.class, () ->
                pictureService.deletePicture(pictureId, anotherUserId, authentication));

        verify(pictureRepository, never()).saveAndFlush(any());
    }

    @Test
    void testGetReportedPicture_ShouldReturnMappedResponse() {
        UUID pictureId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        Picture picture = mock(Picture.class);
        Challenge challenge = mock(Challenge.class);
        PictureReportResponse reportResponse = mock(PictureReportResponse.class);

        when(pictureRepository.findById(pictureId)).thenReturn(Optional.of(picture));

        lenient().when(picture.getId()).thenReturn(pictureId);
        lenient().when(picture.getChallenge()).thenReturn(challenge);
        lenient().when(challenge.getId()).thenReturn(challengeId);

        MockedStatic<DtoMapper> mockedMapper = Mockito.mockStatic(DtoMapper.class);
        mockedMapper.when(() -> DtoMapper.mapPictureToPictureReportResponse(picture))
                .thenReturn(reportResponse);

        PictureReportResponse result = pictureService.getReportedPicture(pictureId);

        assertNotNull(result);
        assertEquals(reportResponse, result);

        mockedMapper.close();
    }

    @Test
    void testGetAllPicturesByUser_ShouldReturnPictureResponses() {
        Picture picture1 = new Picture();
        picture1.setId(UUID.randomUUID());

        Picture picture2 = new Picture();
        picture2.setId(UUID.randomUUID());

        when(pictureRepository.findByAuthorIdAndIsDeletedFalse(userId)).thenReturn(List.of(picture1, picture2));


        MockedStatic<DtoMapper> mockedDtoMapper = mockStatic(DtoMapper.class);

        PictureResponse pictureResponse1 = mock(PictureResponse.class);
        PictureResponse pictureResponse2 = mock(PictureResponse.class);
        mockedDtoMapper.when(() -> DtoMapper.mapPictureToPictureResponse(picture1, userId)).thenReturn(pictureResponse1);
        mockedDtoMapper.when(() -> DtoMapper.mapPictureToPictureResponse(picture2, userId)).thenReturn(pictureResponse2);

        List<PictureResponse> result = pictureService.getAllPicturesByUser(userId, userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(pictureRepository, times(1)).findByAuthorIdAndIsDeletedFalse(userId);
        mockedDtoMapper.verify(() -> DtoMapper.mapPictureToPictureResponse(picture1, userId), times(1));
        mockedDtoMapper.verify(() -> DtoMapper.mapPictureToPictureResponse(picture2, userId), times(1));
    }
}