package analysis;

/**
 * This class provides a short information about GitHub user.
 */
public class User {

    private long id;
    private String username;

    public User() {}

    public User(long id,String name) {
        this.id = id;
        this.username = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return username;
    }

    public void setName(String username) {
        this.username = username;
    }

}
