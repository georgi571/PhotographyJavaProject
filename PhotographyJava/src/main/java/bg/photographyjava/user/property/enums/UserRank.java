package bg.photographyjava.user.property.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum UserRank {
    BEGINNER(0),
    AMATEUR(100),
    INTERMEDIATE(500),
    EXPERT(1000),
    MASTER(2000);

    private final int pointsRequired;

    UserRank(int pointsRequired) {
        this.pointsRequired = pointsRequired;
    }

    public int getPointsRequired() {
        return pointsRequired;
    }

    public static UserRank getRankForPoints(int points) {
        UserRank currentRank = UserRank.BEGINNER;
        for (UserRank rank : UserRank.values()) {
            if (points >= rank.getPointsRequired()) {
                currentRank = rank;
            }
        }
        return currentRank;
    }

    public static List<UserRank> getRanks() {
        return new ArrayList<>(Arrays.asList(UserRank.values()));
    }
}
