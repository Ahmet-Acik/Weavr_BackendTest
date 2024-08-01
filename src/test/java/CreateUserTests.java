import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import models.CreateUserModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import services.GoRestService;

import java.util.UUID;
import java.util.stream.Stream;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateUserTests {

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
     * Tests the creation of a user with valid data.
     * Expects the user to be created successfully.
     */
    @Test
    @DisplayName("Test Successful User Creation")
    public void Users_CreateUsers_Success() {
        String randomEmail = "qatest+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", randomEmail, "active");
        Response response = GoRestService.createUser(createUserModel);
        response.then()
                .statusCode(SC_CREATED)
                .body("data.id", notNullValue())
                .body("data.name", equalTo(createUserModel.getName()));
        logMetaData(response.then());
    }

    /**
     * Tests the creation of a user with invalid data.
     * Expects the user creation to fail with a 422 Unprocessable Entity status.
     *
     * @param createUserModel the model containing invalid user data
     */
    @ParameterizedTest
    @MethodSource("provideInvalidUserModels")
    @DisplayName("Test User Creation with Invalid Data")
    public void Users_CreateUsers_Failure_InvalidData(CreateUserModel createUserModel) {
        Response response = GoRestService.createUser(createUserModel);
        response.then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data", notNullValue());
        logMetaData(response.then());
    }

    /**
     * Provides a stream of invalid user models for parameterized tests.
     *
     * @return a stream of invalid CreateUserModel instances
     */
    private static Stream<CreateUserModel> provideInvalidUserModels() {
        return Stream.of(
                new CreateUserModel("", "male", "qatest@test.com", "active"), // Missing name
                new CreateUserModel("", "", "qatest@test.com", "active"), // Missing gender
                new CreateUserModel("Gino Paloma", "", "", "active"), // Missing email
                new CreateUserModel("Gino Paloma", "", "qatest@test.com", ""), // Missing status
                new CreateUserModel("Gino Paloma", "male", "qatest@test.com", ""),// Missing status
                new CreateUserModel("Gino Paloma", "male", "", "active") // Missing status
        );
    }

    /**
     * Tests the creation of a user with invalid data and checks for specific error messages.
     * Expects the user creation to fail with a 422 Unprocessable Entity status and specific error messages.
     *
     * @param createUserModel the model containing invalid user data
     * @param fields the fields expected to have errors
     * @param messages the error messages expected for the fields
     */
    @ParameterizedTest
    @MethodSource("provideInvalidUserModelsWithMessages")
    @DisplayName("Test User Creation with Invalid Data and Specific Error Messages")
    public void Users_CreateUsers_Failure_InvalidDataWithMessages(CreateUserModel createUserModel, String[] fields, String[] messages) {
        Response response = GoRestService.createUser(createUserModel);
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
    private static Stream<Arguments> provideInvalidUserModelsWithMessages() {
        return Stream.of(
                Arguments.of(new CreateUserModel("", "male", "qatest@test.com", "active"),
                        new String[]{"name"},
                        new String[]{"can't be blank"}),
                Arguments.of(new CreateUserModel("", "", "qatest@test.com", "active"),
                        new String[]{"name", "gender"},
                        new String[]{"can't be blank", "can't be blank, can be male of female"}),
                Arguments.of(new CreateUserModel("Gino Paloma", "", "", "active"),
                        new String[]{"email", "gender"},
                        new String[]{"can't be blank", "can't be blank, can be male of female"}),
                Arguments.of(new CreateUserModel("Gino Paloma", "", "qatest@test.com", ""),
                        new String[]{"status", "gender"},
                        new String[]{"can't be blank", "can't be blank, can be male of female"}),
                Arguments.of(new CreateUserModel("Gino Paloma", "male", "qatest@test.com", ""),
                        new String[]{"status"},
                        new String[]{"can't be blank"}),
                Arguments.of(new CreateUserModel("Gino Paloma", "male", "", "active"),
                        new String[]{"email"},
                        new String[]{"can't be blank"})
        );
    }

    /**
     * Tests the creation of a user with an invalid email.
     * Expects the user creation to fail with a 422 Unprocessable Entity status.
     */
    @Test
    @DisplayName("Test User Creation with Invalid Email")
    public void Users_CreateUsers_Failure_InvalidEmail() {
        String invalidEmail = "invalid-email";
        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", invalidEmail, "active");
        Response response = GoRestService.createUser(createUserModel);
        response.then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data", notNullValue());
        logMetaData(response.then());
    }

    /**
     * Tests the creation of a user with a duplicate email.
     * Expects the user creation to fail with a 422 Unprocessable Entity status.
     */
    @Test
    @DisplayName("Test User Creation with Duplicate Email")
    public void Users_CreateUsers_Failure_DuplicateEmail() {
        String duplicateEmail = "qatest+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", duplicateEmail, "active");
        GoRestService.createUser(createUserModel)
                .then()
                .statusCode(SC_CREATED);

        Response response = GoRestService.createUser(createUserModel);
        response.then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data", notNullValue());
        logMetaData(response.then());
    }

    /**
     * Tests the creation of a user with empty fields.
     * Expects the user creation to fail with a 422 Unprocessable Entity status.
     */
    @Test
    @DisplayName("Test User Creation with Empty Fields")
    public void Users_CreateUsers_Failure_EmptyFields() {
        final CreateUserModel createUserModel = new CreateUserModel("", "", "", "");
        Response response = GoRestService.createUser(createUserModel);
        response.then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data", notNullValue());
        logMetaData(response.then());
    }

    /**
     * Tests the creation of a user without an authorization token.
     * Expects the user creation to fail with a 401 Unauthorized status.
     */
    @Test
    @DisplayName("Test User Creation without Authorization Token")
    public void Users_CreateUsers_Failure_NoToken() {
        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", "qatest@test.com", "active");
        Response response = GoRestService.createUserWithoutToken(createUserModel);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("meta", equalTo(null))
                .body("data.message", equalTo("Authentication failed"));
    }

    /**
     * Tests the creation of a user with an invalid authorization token.
     * Expects the user creation to fail with a 401 Unauthorized status.
     */
    @Test
    @DisplayName("Test User Creation with Invalid Authorization Token")
    public void Users_CreateUsers_Failure_InvalidToken() {
        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", "qatest@test.com", "active");
        Response response = GoRestService.createUserWithInvalidToken(createUserModel);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("meta", equalTo(null))
                .body("data.message", equalTo("Invalid token"));
    }
}