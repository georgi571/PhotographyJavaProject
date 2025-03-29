package bg.challenges.challenge.service.impl;

import bg.challenges.challenge.model.Challenge;
import bg.challenges.challenge.model.Picture;
import bg.challenges.challenge.model.Winner;
import bg.challenges.challenge.model.ChallengeActivity;
import bg.challenges.challenge.model.ChallengeType;
import bg.challenges.challenge.repository.ChallengeRepository;
import bg.challenges.challenge.service.ChallengeService;
import bg.challenges.challenge.service.PictureService;
import bg.challenges.exception.*;
import bg.challenges.shared.service.CloudinaryService;
import bg.challenges.shared.service.impl.KafkaProducer;
import bg.challenges.web.dto.*;
import bg.challenges.web.mapper.DtoMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final PictureService pictureService;
    private final KafkaProducer kafkaProducer;
    private final CloudinaryService cloudinaryService;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository, PictureService pictureService, KafkaProducer kafkaProducer, CloudinaryService cloudinaryService) {
        this.challengeRepository = challengeRepository;
        this.pictureService = pictureService;
        this.kafkaProducer = kafkaProducer;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    @Scheduled(cron = "00 02 01 * * *")
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
        }

        for (Challenge challenge : finishedChallenges) {
            Challenge uploadeChallenge = this.challengeRepository.findById(challenge.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));

            List<WinnerRegisterV1> winnersWithPoints = getWinnersWithPoints(uploadeChallenge);
            for (WinnerRegisterV1 winnersWithPoint : winnersWithPoints) {
                this.kafkaProducer.sendMessage(winnersWithPoint);
            }
        }
    }

    private List<WinnerRegisterV1> getWinnersWithPoints(Challenge challenge) {
        List<WinnerRegisterV1> winnersWithPoints = new ArrayList<>();

        List<Winner> winners = challenge.getWinners();

        for (int i = 0; i < Math.min(3, winners.size()); i++) {
            Winner winner = winners.get(i);

            WinnerRegisterV1 winnerRegisterV1 = new WinnerRegisterV1();
            winnerRegisterV1.setUserId(winner.getUserId());
            if (winner.getPosition() == 1) {
                winnerRegisterV1.setPoints(10);
            } else if (winner.getPosition() == 2) {
                winnerRegisterV1.setPoints(7);
            } else {
                winnerRegisterV1.setPoints(4);
            }
            winnerRegisterV1.setType(challenge.getType());

            winnersWithPoints.add(winnerRegisterV1);
        }

        return winnersWithPoints;
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
        Challenge challenge = this.challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));

        List<Picture> pictures = this.pictureService.getWinnersPicture(challengeId);

        List<Winner> winners = new ArrayList<>();
        for (int i = 0; i < Math.min(3, pictures.size()); i++) {
            Picture topPicture = pictures.get(i);
            UUID userId = topPicture.getAuthorId();

            Winner winner = new Winner();
            winner.setChallenge(challenge);
            winner.setUserId(userId);
            winner.setPosition(i + 1);

            winners.add(winner);
        }
        challenge.setWinners(winners);
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
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));

        return DtoMapper.mapChallengeToChallengeDetailsResponse(challenge, userId);
    }

    @Override
    public PictureResponse savePictureForChallenge(UUID challengeId, MultipartFile file, String caption, String story, UUID userId) throws IOException {

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));

        ChallengeActivity activityStatus = challenge.getActivity();

        if (activityStatus == ChallengeActivity.UPCOMING) {
            throw new ChallengeNotStartException("Cannot upload picture. Challenge has not started yet.");
        }

        if (activityStatus == ChallengeActivity.PAST) {
            throw new ChallengeAlreadyFinishException("Cannot upload picture. Challenge has already finished.");
        }

        Map<String, Object> uploadResult = this.cloudinaryService.uploadImage(file);
        String pictureFilePath = (String) uploadResult.get("secure_url");

        Picture picture = DtoMapper.mapPictureUploadRequestToPicture(challenge, pictureFilePath, caption, story, userId);

        this.pictureService.savePicture(picture);

        challenge.getPictures().add(picture);

        this.challengeRepository.saveAndFlush(challenge);

        return DtoMapper.mapPictureToPictureResponse(picture, userId);
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