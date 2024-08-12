package models;

/**
 * The UpdateUserModel class extends the CreateUserModel class to include an additional
 * field for the user's ID. This class is used to represent the data required to update
 * an existing user.
 */
public class UpdateUserModel extends CreateUserModel {
    private String id;


    public UpdateUserModel(String name, String gender, String email, String status) {
        super(name, gender, email, status);
    }
}