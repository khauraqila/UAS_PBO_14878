package passwordmanager.model;

public class Folder {
    private int id;
    private String name;
    private int userId;

    public Folder(int id, String name, int userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    public Folder(String name, int userId) {
        this.name = name;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return name;
    }
}