package bg.photographyjava.web.dto;

import java.util.UUID;

public class BanUserResponse {

    private UUID id;

    private String username;

    private String email;

    private boolean isBanned;

    private String reasonForBan;

    public BanUserResponse() {
    }

    public BanUserResponse(UUID id, String username, String email, boolean banned, String reasonForBan) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.isBanned = banned;
        this.reasonForBan = reasonForBan;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public String getReasonForBan() {
        return reasonForBan;
    }

    public void setReasonForBan(String reasonForBan) {
        this.reasonForBan = reasonForBan;
    }
}
