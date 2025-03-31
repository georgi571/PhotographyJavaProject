package bg.photographyjava.web.dto;

import jakarta.validation.constraints.NotNull;

public class BanUserReasonRequest {

    @NotNull
    private String action;

    @NotNull
    private String reason;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
