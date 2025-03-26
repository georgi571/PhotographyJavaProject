package bg.leaderboards.web.dto;

import java.io.Serializable;

public class UserChallengesResponse implements Serializable {

    long numberOfDailyWinChallenges;

    long numberOfThemedWinChallenges;

    long numberOFAdminWinChallenges;

    public long getNumberOfDailyWinChallenges() {
        return numberOfDailyWinChallenges;
    }

    public void setNumberOfDailyWinChallenges(long numberOfDailyWinChallenges) {
        this.numberOfDailyWinChallenges = numberOfDailyWinChallenges;
    }

    public long getNumberOfThemedWinChallenges() {
        return numberOfThemedWinChallenges;
    }

    public void setNumberOfThemedWinChallenges(long numberOfThemedWinChallenges) {
        this.numberOfThemedWinChallenges = numberOfThemedWinChallenges;
    }

    public long getNumberOFAdminWinChallenges() {
        return numberOFAdminWinChallenges;
    }

    public void setNumberOFAdminWinChallenges(long numberOFAdminWinChallenges) {
        this.numberOFAdminWinChallenges = numberOFAdminWinChallenges;
    }
}
