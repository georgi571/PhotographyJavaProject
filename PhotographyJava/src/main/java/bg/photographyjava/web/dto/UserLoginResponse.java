package bg.photographyjava.web.dto;

import java.io.Serializable;

public class UserLoginResponse implements Serializable {
    private String jwtToken;

    public UserLoginResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
