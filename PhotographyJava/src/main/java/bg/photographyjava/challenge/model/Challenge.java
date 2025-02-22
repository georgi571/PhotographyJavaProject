package bg.photographyjava.challenge.model;

import bg.photographyjava.challenge.property.enums.ChallengeActivity;
import bg.photographyjava.challenge.property.enums.ChallengeType;
import bg.photographyjava.user.model.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "challenges")
public class Challenge {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "details")
    private String details;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ChallengeType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity")
    private ChallengeActivity activity;

    @Column(name = "startAt")
    private LocalDateTime startAt;

    @Column(name = "endAt")
    private LocalDateTime endAt;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Picture> pictures = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Winner> winners = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public ChallengeType getType() {
        return type;
    }

    public void setType(ChallengeType type) {
        this.type = type;
    }

    public ChallengeActivity getActivity() {
        return activity;
    }

    public void setActivity(ChallengeActivity activity) {
        this.activity = activity;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public List<Winner> getWinners() {
        return winners;
    }

    public void setWinners(List<UserEntity> topUsers) {
        for (int i = 0; i < topUsers.size(); i++) {
            Winner winner = new Winner();
            winner.setChallenge(this);
            winner.setUser(topUsers.get(i));
            winner.setPosition(i + 1);
            this.winners.add(winner);
        }
    }
}
