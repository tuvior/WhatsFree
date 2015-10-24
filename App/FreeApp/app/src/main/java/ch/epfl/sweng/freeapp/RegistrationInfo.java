package ch.epfl.sweng.freeapp;

/**
 * Created by francisdamachi on 22/10/15.
 */
public class RegistrationInfo {

    private String username;
    private String email;
    private String password;

    public RegistrationInfo(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() { return username; }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }


}
