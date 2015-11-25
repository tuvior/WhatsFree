package ch.epfl.sweng.freeapp.loginAndRegistration;
/**
 * Created by francisdamachi on 22/10/15.
 */
public class RegistrationInfo {
    private String username;
    private String password;
    private String email;

    public RegistrationInfo(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
