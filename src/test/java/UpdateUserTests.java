import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import models.CreateUserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import services.GoRestService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * The UpdateUserTests class contains test cases for updating user information
 * using the GoRest API. It includes tests for successful updates, updates with
 * invalid data, and various failure scenarios.
 */
public class UpdateUserTests {

    /**
     * The unique identifier of the user to be updated.
     */
    private String userId;

    /**
     * Logs the metadata from the response.
     *
     * @param response the response to extract metadata from
     */
    private void logMetaData(ValidatableResponse response) {
        System.out.println("Meta: " + response.extract().path("meta"));
        System.out.println("Data: " + response.extract().path("data"));
    }

    /**
     * Sets up the test environment by creating a user to be updated in the tests.
     */
    @BeforeEach
    public void setUp() {
        // Create a user to be updated in the tests
        String randomEmail = "qatest+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", randomEmail, "active");
        userId = GoRestService.createUser(createUserModel)
                .then()
                .statusCode(SC_CREATED)
                .extract()
                .path("data.id").toString(); // Ensure the ID is treated as a String
    }

    /**
     * Tests the successful update of a user.
     * Expects the user to be updated successfully.
     */
    @Test
    @DisplayName("Test Successful User Update")
    public void Users_UpdateUsers_Success() {
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma Updated", "male", updatedEmail, "active");
        GoRestService.updateUser(userId, updateUserModel)
                .then()
                .statusCode(SC_OK)
                .body("data.id", equalTo(Integer.parseInt(userId))) // Convert userId to Integer for comparison
                .body("data.name", equalTo(updateUserModel.getName()));
    }

    /**
     * Tests the update of a user with invalid data.
     * Expects the update to fail with a 422 Unprocessable Entity status.
     *
     * @param updateUserModel the model containing invalid user data
     */
    @ParameterizedTest
    @MethodSource("provideInvalidUserModels")
    @DisplayName("Test User Update with Invalid Data")
    public void Users_UpdateUsers_Failure_InvalidData(CreateUserModel updateUserModel) {
        GoRestService.updateUser(userId, updateUserModel)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data", notNullValue());
    }

    /**
     * Provides a stream of invalid user models for parameterized tests.
     *
     * @return a stream of invalid CreateUserModel instances
     */
    private static Stream<CreateUserModel> provideInvalidUserModels() {
        return Stream.of(
                new CreateUserModel("", "male", "qatest@test.com", "active"), // Missing name
                new CreateUserModel("Gino Paloma", "", "qatest@test.com", "active"), // Missing gender
                new CreateUserModel("Gino Paloma", "male", "", "active"), // Missing email
                new CreateUserModel("Gino Paloma", "male", "qatest@test.com", "") // Missing status
        );
    }

    /**
     * Tests the update of a user with invalid data and checks for specific error messages.
     * Expects the update to fail with a 422 Unprocessable Entity status and specific error messages.
     *
     * @param userId the unique identifier of the user to be updated
     * @param updateUserModel the model containing invalid user data
     * @param fields the fields expected to have errors
     * @param messages the error messages expected for the fields
     */
    @ParameterizedTest
    @MethodSource("provideInvalidUserModelsWithMessagesForUpdate")
    @DisplayName("Test User Update with Invalid Data and Specific Error Messages")
    public void Users_UpdateUsers_Failure_InvalidDataWithMessages(String userId, CreateUserModel updateUserModel, String[] fields, String[] messages) {
        Response response = GoRestService.updateUser(userId, updateUserModel);
        ValidatableResponse validatableResponse = response.then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data", notNullValue());

        for (int i = 0; i < fields.length; i++) {
            validatableResponse.body("data.find { it.field == '" + fields[i] + "' }.message", equalTo(messages[i]));
        }

        logMetaData(validatableResponse);
    }

    /**
     * Provides a stream of invalid user models with expected error messages for parameterized tests.
     *
     * @return a stream of Arguments containing invalid CreateUserModel instances and expected error messages
     */
    private static Stream<Arguments> provideInvalidUserModelsWithMessagesForUpdate() {
        return Stream.of(
                Arguments.of("7270217", new CreateUserModel("", "", "", "active"),
                        new String[]{"email", "name", "gender"},
                        new String[]{"can't be blank", "can't be blank", "can't be blank, can be male of female"}),
                Arguments.of("7270217", new CreateUserModel("Max", "", "", "active"),
                        new String[]{"email", "gender"},
                        new String[]{"can't be blank", "can't be blank, can be male of female"}),
                Arguments.of("7270217", new CreateUserModel("Max", "male", "", ""),
                        new String[]{"email", "status"},
                        new String[]{"can't be blank", "can't be blank"})
        );
    }

    /**
     * Tests the update of a user with an invalid email.
     * Expects the update to fail with a 422 Unprocessable Entity status.
     */
    @Test
    @DisplayName("Test User Update with Invalid Email")
    public void Users_UpdateUsers_Failure_InvalidEmail() {
        String invalidEmail = "invalid-email";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma Updated", "male", invalidEmail, "active");
        GoRestService.updateUser(userId, updateUserModel)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data", notNullValue());
    }

    /**
     * Tests the update of a user with empty fields.
     * Expects the update to fail with a 422 Unprocessable Entity status.
     */
    @Test
    @DisplayName("Test User Update with Empty Fields")
    public void Users_UpdateUsers_Failure_EmptyFields() {
        final CreateUserModel updateUserModel = new CreateUserModel("", "", "", "");
        GoRestService.updateUser(userId, updateUserModel)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data", notNullValue());
    }

    /**
     * Tests the update of a non-existent user.
     * Expects the update to fail with a 404 Not Found status.
     */
    @Test
    @DisplayName("Test User Update for Non-Existent User")
    public void Users_UpdateUsers_Failure_NonExistentUser() {
        String nonExistentUserId = "999999";
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma Updated", "male", updatedEmail, "active");
        GoRestService.updateUser(nonExistentUserId, updateUserModel)
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    /**
     * Tests the update of a user without an authorization token.
     * Expects the update to fail with a 401 Unauthorized status.
     */
    @Test
    @DisplayName("Test User Update without Authorization Token")
    public void Users_UpdateUsers_Failure_NoToken() {
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma", "male", updatedEmail, "active");

        Response findResponse = GoRestService.findById(userId);
        if (findResponse.statusCode() == SC_OK) {
            Response response = GoRestService.updateUserWithoutToken(userId, updateUserModel);
            response.then()
                    .statusCode(SC_UNAUTHORIZED)
                    .body("meta", equalTo(null))
                    .body("data.message", equalTo("Authentication failed"));
        } else {
            Response response = GoRestService.updateUserWithoutToken("72702", updateUserModel); // Assuming "72702" is an invalid ID
            response.then()
                    .statusCode(SC_NOT_FOUND)
                    .body("meta", equalTo(null))
                    .body("data.message", equalTo("Resource not found"));
        }
    }

    /**
     * Tests the update of a user without an authorization token and with an invalid user ID.
     * Expects the update to fail with a 404 Not Found status.
     */
    @Test
    @DisplayName("Test User Update without Authorization Token and Invalid User ID")
    public void Users_UpdateUsers_Failure_NoToken_InvalidId() {
        String invalidUserId = "72702";
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma", "male", updatedEmail, "active");
        Response response = GoRestService.updateUserWithoutToken(invalidUserId, updateUserModel);
        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("meta", equalTo(null))
                .body("data.message", equalTo("Resource not found"));
    }

    /**
     * Tests the update of a user with an invalid authorization token.
     * Expects the update to fail with a 401 Unauthorized status.
     */
    @Test
    @DisplayName("Test User Update with Invalid Authorization Token")
    public void Users_UpdateUsers_Failure_InvalidToken() {
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma", "male", updatedEmail, "active");
        Response response = GoRestService.updateUserWithInvalidToken(userId, updateUserModel);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("meta", equalTo(null))
                .body("data.message", equalTo("Invalid token"));
    }

    /**
     * Tests the update of a user using data fetched from the GoRest API.
     * Expects the user to be updated successfully.
     */
    @Test
    @DisplayName("Test User Update Using Fetched Data")
    public void updateUserUsingFetchedData() {
        // Fetch all users
        Response allUsersResponse = GoRestService.getAllUsers();
        List<Map<String, Object>> users = allUsersResponse.jsonPath().getList("data");

        // Assume we take the first user for update
        Map<String, Object> user = users.get(0);
        String userId = String.valueOf(user.get("id"));

        // Create updated user data
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        CreateUserModel updateUserModel = new CreateUserModel("Updated Name", "male", updatedEmail, "active");

        // Update the user
        Response updateResponse = GoRestService.updateUser(userId, updateUserModel);
        updateResponse.then()
                .statusCode(200)
                .body("data.email", equalTo(updatedEmail))
                .body("data.name", equalTo("Updated Name"));
    }

}