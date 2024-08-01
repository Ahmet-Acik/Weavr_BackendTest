package repo;

import models.User;

import java.util.HashMap;
import java.util.Map;

/**
 * The UserRepository class provides methods to interact with the user database.
 * It allows finding users by their ID and saving new or updated user information.
 */
public class UserRepository {

    /**
     * A map representing the user database, where the key is the user ID and the value is the User object.
     */
    private Map<String, User> userDatabase = new HashMap<>();

    /**
     * Finds a user by their unique identifier.
     *
     * @param userId the unique identifier of the user
     * @return the User object if found, or null if no user with the given ID exists
     */
    public User findById(String userId) {
        return userDatabase.get(userId);
    }

    /**
     * Saves a new user or updates an existing user in the database.
     *
     * @param user the User object to be saved or updated
     */
    public void save(User user) {
        userDatabase.put(user.getId(), user);
    }
}