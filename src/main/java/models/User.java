package models;

/**
 * The User class represents a user with an ID, name, gender, email, and status.
 */
public class User {
    /**
     * The unique identifier for the user.
     */
    private String id;

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The gender of the user.
     */
    private String gender;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The status of the user.
     */
    private String status;

    /**
     * Gets the name of the user.
     *
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the gender of the user.
     *
     * @return the gender of the user
     */
    public String getGender() {
        return gender;
    }

    /**
     * Gets the email address of the user.
     *
     * @return the email address of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the status of the user.
     *
     * @return the status of the user
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the name of the user.
     *
     * @param name the new name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the gender of the user.
     *
     * @param gender the new gender of the user
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the new email address of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the status of the user.
     *
     * @param status the new status of the user
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the unique identifier for the user.
     *
     * @return the unique identifier for the user
     */
    public String getId() {
        return id;
    }

    // Getters and Setters
}