package bg.leaderboards.leaderboard.model;

import bg.leaderboards.leaderboard.property.CountryEnum;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user_statistics")
public class UserStatistic {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "country", nullable = false)
    private CountryEnum country;

    @Column(name = "total_points", nullable = false)
    private long totalPoints;

    @Column(name = "total_challenges_won", nullable = false)
    private long totalChallengesWon;

    @Column(name = "total_challenges_won_daily", nullable = false)
    private long totalChallengesWonDaily;

    @Column(name = "total_challenges_won_themed", nullable = false)
    private long totalChallengesWonThemed;

    @Column(name = "total_challenges_won_admin", nullable = false)
    private long totalChallengesWonAdmin;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CountryEnum getCountry() {
        return country;
    }

    public void setCountry(CountryEnum country) {
        this.country = country;
    }

    public long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public long getTotalChallengesWon() {
        return totalChallengesWon;
    }

    public void setTotalChallengesWon(long totalChallengesWon) {
        this.totalChallengesWon = totalChallengesWon;
    }

    public long getTotalChallengesWonDaily() {
        return totalChallengesWonDaily;
    }

    public void setTotalChallengesWonDaily(long totalChallengesWonDaily) {
        this.totalChallengesWonDaily = totalChallengesWonDaily;
    }

    public long getTotalChallengesWonThemed() {
        return totalChallengesWonThemed;
    }

    public void setTotalChallengesWonThemed(long totalChallengesWonThemed) {
        this.totalChallengesWonThemed = totalChallengesWonThemed;
    }

    public long getTotalChallengesWonAdmin() {
        return totalChallengesWonAdmin;
    }

    public void setTotalChallengesWonAdmin(long totalChallengesWonAdmin) {
        this.totalChallengesWonAdmin = totalChallengesWonAdmin;
    }
}
