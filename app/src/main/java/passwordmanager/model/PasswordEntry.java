package passwordmanager.model;

public class PasswordEntry {
    private int id;
    private String name;           
    private String username;
    private String encryptedPassword;
    private String hashKey;
    private int folderId;
    private int userId;
    private String category;       
    
    public PasswordEntry(String name, String username, String encryptedPassword, String hashKey, int folderId, int userId, String category) {
        this.name = name;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.hashKey = hashKey;
        this.folderId = folderId;
        this.userId = userId;
        this.category = category;
    }

    public PasswordEntry(int id, String name, String username, String encryptedPassword, String hashKey, int folderId, int userId, String category) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.hashKey = hashKey;
        this.folderId = folderId;
        this.userId = userId;
        this.category = category;
    }


    public int getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEncryptedPassword() { return encryptedPassword; }
    public String getHashKey() { return hashKey; }
    public int getFolderId() { return folderId; }
    public int getUserId() { return userId; }
    public String getCategory() { return category; }

    @Override
    public String toString() {
        return name + " (" + username + ")";
    }
}
