package ai.marketplace.server.structures;

//class that works as interface with the client to exchange data
public class RegistrationUser
{
    private String username;
    private String password;
    private String role;
    private String id;

    public RegistrationUser() {
        this.username = "";
        this.password = "";
        this.role = "";
        this.id="";
    }

    public RegistrationUser(String username, String password, String role, String id) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.id=id;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    public void setId(String id)
    {
        this.id=id;
    }
    public String getId()
    {
        return this.id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
