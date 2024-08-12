package repo;

import models.User;

import java.util.HashMap;
import java.util.Map;

/**
 * The UserRepository class provides methods to interact with the user database.
 * It allows finding users by their ID and saving new or updated user information.
 */
public class UserRepository {


    private Map<String, User> userDatabase = new HashMap<>();


    public User findById(String userId) {
        return userDatabase.get(userId);
    }


    public void save(User user) {
        userDatabase.put(user.getId(), user);
    }
}