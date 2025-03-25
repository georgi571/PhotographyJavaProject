package bg.leaderboards.leaderboard.model;

import jakarta.persistence.*;

import java.time.Month;
import java.util.UUID;

@Entity
@Table(name = "monthly_statistics")
public class MonthlyStatistic {
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

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "month", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Month month;

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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }
}
