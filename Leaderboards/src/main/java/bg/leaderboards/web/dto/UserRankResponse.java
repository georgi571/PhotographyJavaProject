package bg.leaderboards.web.dto;

import bg.leaderboards.leaderboard.model.UserRank;

import java.io.Serializable;
import java.util.UUID;

public class UserRankResponse implements Serializable {
    private UUID userId;
    private long totalPoints;
    private UserRank userRank;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public UserRank getUserRank() {
        return userRank;
    }

    public void setUserRank(UserRank userRank) {
        this.userRank = userRank;
    }
}
