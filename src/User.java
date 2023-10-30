/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping addToCart items.
 */
public class User {

    private final String username;
    private final String id;

    public User(String username, String id) {
        this.username = username;
        this.id = id;
    }
    public String getId() {
        return this.id;
    }

}
