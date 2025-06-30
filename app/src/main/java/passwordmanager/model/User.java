package passwordmanager.model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String fullname;

    public User(String username, String passwordHash, String fullname) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullname = fullname;
    }

    public User(int id, String username, String passwordHash, String fullname) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullname = fullname;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", fullname='" + fullname + '\'' +
               '}';
    }
}