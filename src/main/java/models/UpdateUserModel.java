package models;

/**
 * The UpdateUserModel class extends the CreateUserModel class to include an additional
 * field for the user's ID. This class is used to represent the data required to update
 * an existing user.
 */
public class UpdateUserModel extends CreateUserModel {
    /**
     * The unique identifier for the user.
     */
    private String id;

    /**
     * Constructs a new UpdateUserModel with the specified name, gender, email, and status.
     *
     * @param name   the name of the user
     * @param gender the gender of the user
     * @param email  the email address of the user
     * @param status the status of the user
     */
    public UpdateUserModel(String name, String gender, String email, String status) {
        super(name, gender, email, status);
    }
}