package bg.challenges.challenge.service.impl;

import bg.challenges.challenge.model.Challenge;
import bg.challenges.challenge.model.ChallengeActivity;
import bg.challenges.challenge.model.ChallengeType;
import bg.challenges.challenge.model.Picture;
import bg.challenges.challenge.repository.ChallengeRepository;
import bg.challenges.challenge.service.PictureService;
import bg.challenges.exception.ChallengeAlreadyFinishException;
import bg.challenges.exception.ChallengeNotStartException;
import bg.challenges.shared.service.CloudinaryService;
import bg.challenges.shared.service.impl.KafkaProducer;
import bg.challenges.web.dto.ChallengeDetailsResponse;
import bg.challenges.web.dto.ChallengeResponse;
import bg.challenges.web.dto.CreateChallengeRequest;
import bg.challenges.web.dto.EditChallengeRequest;
import bg.challenges.web.mapper.DtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceImplUTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private PictureService pictureService;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ChallengeServiceImpl challengeService;

    @Test
    void testStartDailyChallenge() {
        Challenge challenge = mock(Challenge.class);
        List<Challenge> startingChallenges = List.of(challenge);
        List<Challenge> finishedChallenges = List.of(challenge);

        when(challengeRepository.findByStartAt(any(LocalDateTime.class))).thenReturn(startingChallenges);
        when(challengeRepository.findByEndAtBeforeAndWinnersIsNull(any(LocalDateTime.class))).thenReturn(finishedChallenges);

        when(challengeRepository.findById(any())).thenReturn(Optional.of(challenge));

        when(challengeRepository.saveAndFlush(any(Challenge.class))).thenReturn(challenge);

        challengeService.startDailyChallenge();

        verify(challengeRepository, times(3)).saveAndFlush(any(Challenge.class));
    }

    @Test
    void testSavePictureForChallenge_whenChallengeIsUpcoming_shouldThrowException() {
        Challenge challenge = mock(Challenge.class);
        when(challenge.getActivity()).thenReturn(ChallengeActivity.UPCOMING);
        when(challengeRepository.findById(any())).thenReturn(java.util.Optional.of(challenge));

        assertThrows(ChallengeNotStartException.class, () -> {
            challengeService.savePictureForChallenge(
                    java.util.UUID.randomUUID(), file, "caption", "story", java.util.UUID.randomUUID());
        });
    }

    @Test
    void testSavePictureForChallenge_whenChallengeIsActive_shouldSavePicture() throws IOException {
        Challenge challenge = mock(Challenge.class);
        when(challenge.getActivity()).thenReturn(ChallengeActivity.ACTIVE);
        when(challengeRepository.findById(any())).thenReturn(java.util.Optional.of(challenge));
        when(cloudinaryService.uploadImage(file)).thenReturn(Map.of("secure_url", "http://image.url"));

        challengeService.savePictureForChallenge(
                java.util.UUID.randomUUID(), file, "caption", "story", java.util.UUID.randomUUID());

        verify(pictureService, times(1)).savePicture(any());
        verify(challengeRepository, times(1)).saveAndFlush(challenge);
    }

    @Test
    void testSavePictureForChallenge_whenChallengeIsPast_shouldThrowException() {
        Challenge challenge = mock(Challenge.class);
        when(challenge.getActivity()).thenReturn(ChallengeActivity.PAST);
        when(challengeRepository.findById(any())).thenReturn(java.util.Optional.of(challenge));

        assertThrows(ChallengeAlreadyFinishException.class, () -> {
            challengeService.savePictureForChallenge(
                    java.util.UUID.randomUUID(), file, "caption", "story", java.util.UUID.randomUUID());
        });
    }

    @Test
    void testSetChallengeWinners() {
        UUID challengeId = UUID.randomUUID();
        Challenge challenge = mock(Challenge.class);
        when(challengeRepository.findById(challengeId)).thenReturn(java.util.Optional.of(challenge));

        Picture picture1 = mock(Picture.class);
        Picture picture2 = mock(Picture.class);
        List<Picture> pictures = Arrays.asList(picture1, picture2);
        when(pictureService.getWinnersPicture(challengeId)).thenReturn(pictures);

        challengeService.setChallengeWinners(challengeId);

        verify(challengeRepository, times(1)).saveAndFlush(challenge);
        verify(pictureService, times(1)).getWinnersPicture(challengeId);
    }

    @Test
    void testCreateChallenge() {
        CreateChallengeRequest createChallengeRequest = mock(CreateChallengeRequest.class);

        LocalDate startAt = LocalDate.of(2025, 3, 28);
        LocalDate endAt = LocalDate.of(2025, 3, 28);

        when(createChallengeRequest.getStartAt()).thenReturn(startAt);
        when(createChallengeRequest.getEndAt()).thenReturn(endAt);
        when(createChallengeRequest.getType()).thenReturn(ChallengeType.DAILY.name());

        Challenge challenge = mock(Challenge.class);
        when(challengeRepository.saveAndFlush(any())).thenReturn(challenge);

        ChallengeResponse response = challengeService.createChallenge(createChallengeRequest);

        assertNotNull(response);

        verify(challengeRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void testEditChallenge() {
        UUID challengeId = UUID.randomUUID();

        EditChallengeRequest editChallengeRequest = mock(EditChallengeRequest.class);

        LocalDate startAt = LocalDate.of(2025, 3, 28);
        LocalDate endAt = LocalDate.of(2025, 3, 28);
        when(editChallengeRequest.getStartAt()).thenReturn(startAt);
        when(editChallengeRequest.getEndAt()).thenReturn(endAt);

        when(editChallengeRequest.getType()).thenReturn(ChallengeType.DAILY.name());

        Challenge challenge = mock(Challenge.class);

        ChallengeActivity challengeActivity = mock(ChallengeActivity.class);
        when(challenge.getActivity()).thenReturn(challengeActivity);

        when(challengeActivity.name()).thenReturn(ChallengeActivity.UPCOMING.name());
        when(challenge.getType()).thenReturn(ChallengeType.DAILY);
        when(challengeRepository.findById(challengeId)).thenReturn(java.util.Optional.of(challenge));

        ChallengeResponse response = challengeService.editChallenge(challengeId, editChallengeRequest);

        assertNotNull(response);

        verify(challengeRepository, times(1)).saveAndFlush(challenge);
    }

    @Test
    void testGetAllChallenges() {
        List<Challenge> challenges = List.of(mock(Challenge.class), mock(Challenge.class));
        when(challengeRepository.findAll()).thenReturn(challenges);

        try (MockedStatic<DtoMapper> mockedDtoMapper = mockStatic(DtoMapper.class)) {
            mockedDtoMapper.when(() -> DtoMapper.mapChallengeToChallengeResponse(any(Challenge.class)))
                    .thenReturn(mock(ChallengeResponse.class));

            List<ChallengeResponse> result = challengeService.getAllChallenges();

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(challengeRepository, times(1)).findAll();
            mockedDtoMapper.verify(() -> DtoMapper.mapChallengeToChallengeResponse(any(Challenge.class)), times(2));
        }
    }

    @Test
    void testGetChallengeDetails() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Challenge challenge = mock(Challenge.class);
        ChallengeDetailsResponse challengeDetailsResponse = mock(ChallengeDetailsResponse.class);

        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));

        try (MockedStatic<DtoMapper> mockedDtoMapper = mockStatic(DtoMapper.class)) {
            mockedDtoMapper.when(() -> DtoMapper.mapChallengeToChallengeDetailsResponse(challenge, userId))
                    .thenReturn(challengeDetailsResponse);

            ChallengeDetailsResponse result = challengeService.getChallengeDetails(challengeId, userId);

            assertNotNull(result);
            assertEquals(challengeDetailsResponse, result);

            verify(challengeRepository, times(1)).findById(challengeId);
            mockedDtoMapper.verify(() -> DtoMapper.mapChallengeToChallengeDetailsResponse(challenge, userId), times(1));
        }
    }

    @Test
    void testFindByType() {
        ChallengeType type = ChallengeType.DAILY;
        List<Challenge> mockChallenges = List.of(new Challenge(), new Challenge());

        when(challengeRepository.findByType(type)).thenReturn(mockChallenges);

        List<Challenge> result = challengeService.findByType(type);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(challengeRepository, times(1)).findByType(type);
    }
}