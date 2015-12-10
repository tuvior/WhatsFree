package ch.epfl.sweng.freeapp.loginAndRegistration;

/**
 * Class representing the info the user will provide whenever he wants to login
 * This class will be created.
 */
public class LogInInfo {

    private String username;
    private String password;

    public LogInInfo(String username, String password) {

        this.username = username;
        this.password = password;


    }

    public String getUsername() {
        return username;

    }

    public String getPassword() {

        return password;

    }
}
