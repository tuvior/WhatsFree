package ch.epfl.sweng.freeapp.loginAndRegistration;

/**
 *
 * Class instantiated  whenever the user wants to Register
 */
public class RegistrationInfo {
    private String username;
    private String email;
    private String password;

    public RegistrationInfo(String username, String password, String email) {
        this.username = username;
        this.email = email;
        this.password = password;
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
