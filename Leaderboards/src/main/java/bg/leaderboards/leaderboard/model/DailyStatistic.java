package bg.leaderboards.leaderboard.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "daily_statistics")
public class DailyStatistic {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "country", nullable = false)
    private CountryEnum country;

    @Column(name = "points_earned", nullable = false)
    private long pointsEarned;

    @Column(name = "day", nullable = false)
    private LocalDate day;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public CountryEnum getCountry() {
        return country;
    }

    public void setCountry(CountryEnum country) {
        this.country = country;
    }

    public long getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(long pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }
}
