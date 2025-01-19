package bg.photographyjava.challenge.service.impl;

import bg.photographyjava.challenge.model.Challenge;
import bg.photographyjava.challenge.model.Picture;
import bg.photographyjava.challenge.property.enums.ChallengeActivity;
import bg.photographyjava.challenge.property.enums.ChallengeType;
import bg.photographyjava.challenge.repository.ChallengeRepository;
import bg.photographyjava.challenge.service.ChallengeService;
import bg.photographyjava.challenge.service.PictureService;
import bg.photographyjava.user.model.UserEntity;
import bg.photographyjava.web.dto.ChallengeDTO;
import bg.photographyjava.web.dto.ChallengeDetailsDTO;
import bg.photographyjava.web.dto.PictureDTO;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository, PictureService pictureService, ModelMapper modelMapper) {
        this.challengeRepository = challengeRepository;
        this.pictureService = pictureService;
        this.modelMapper = modelMapper;
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
        challenge.setCreatedAt(startAt);
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
    public List<ChallengeDTO> getAllChallenges() {
        LocalDateTime now = LocalDateTime.now();
        List<Challenge> challenges =  challengeRepository.findAll();

        List<ChallengeDTO> challengesDTO = new ArrayList<>();
        for (Challenge challenge : challenges) {
            challengesDTO.add(this.modelMapper.map(challenge, ChallengeDTO.class));
        }

        return challengesDTO;
    }

    @Override
    public ChallengeDetailsDTO getChallengeDetails(UUID id) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        ChallengeDetailsDTO challengeDetailsDTO = this.modelMapper.map(challenge, ChallengeDetailsDTO.class);

        List<PictureDTO> pictureDTOs = challenge.getPictures().stream()
                .map(picture -> this.modelMapper.map(picture, PictureDTO.class))
                .toList();

        challengeDetailsDTO.setPictures(pictureDTOs);

        return challengeDetailsDTO;
    }
}
