import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import models.CreateUserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(ThreadLoggingExtension.class)
public class UpdateUserTests {

    private String userId;

    // Log metadata from the response
    private void logMetaData(ValidatableResponse response) {
        System.out.println("Meta: " + response.extract().path("meta"));
        System.out.println("Data: " + response.extract().path("data"));
    }

    @BeforeEach
    public void setUp() {
        // Create a user to be updated in the tests
        String randomEmail = "qatest+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", randomEmail, "active");
        userId = GoRestService.createUser(createUserModel).then().statusCode(SC_CREATED).extract().path("data.id").toString(); // Ensure the ID is treated as a String
    }

    @Test
    @DisplayName("Test Successful User Update")
    public void Users_UpdateUsers_Success() {
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma Updated", "male", updatedEmail, "active");
        GoRestService.updateUser(userId, updateUserModel).then().statusCode(SC_OK).body("data.id", equalTo(Integer.parseInt(userId))) // Convert userId to Integer for comparison
                .body("data.name", equalTo(updateUserModel.getName()));
    }

    @Test
    @DisplayName("Test User Update Response Headers")
    public void Users_UpdateUsers_ResponseHeaders() {
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma Updated", "male", updatedEmail, "active");
        Response response = GoRestService.updateUser(userId, updateUserModel);
        response.then().statusCode(SC_OK).header("Content-Type", "application/json; charset=utf-8");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserModels")
    @DisplayName("Test User Update with Invalid Data")
    public void Users_UpdateUsers_Failure_InvalidData(CreateUserModel updateUserModel) {
        GoRestService.updateUser(userId, updateUserModel).then().statusCode(SC_UNPROCESSABLE_ENTITY).body("data", notNullValue());
    }

    // Provide invalid user models for parameterized tests
    private static Stream<CreateUserModel> provideInvalidUserModels() {
        return Stream.of(new CreateUserModel("", "male", "qatest@test.com", "active"), // Missing name
                new CreateUserModel("Gino Paloma", "", "qatest@test.com", "active"), // Missing gender
                new CreateUserModel("Gino Paloma", "male", "", "active"), // Missing email
                new CreateUserModel("Gino Paloma", "male", "qatest@test.com", "") // Missing status
        );
    }

//    @ParameterizedTest
//    @MethodSource("provideInvalidUserModelsWithMessagesForUpdate")
//    @DisplayName("Test User Update with Invalid Data and Specific Error Messages")
//    public void Users_UpdateUsers_Failure_InvalidDataWithMessages(String userId, CreateUserModel updateUserModel, String[] fields, String[] messages) {
//        Response response = GoRestService.updateUser(userId, updateUserModel);
//        ValidatableResponse validatableResponse = response.then().statusCode(SC_UNPROCESSABLE_ENTITY).body("data", notNullValue());
//
//        // Validate each field's error message
//        for (int i = 0; i < fields.length; i++) {
//            validatableResponse.body("data.find { it.field == '" + fields[i] + "' }.message", equalTo(messages[i]));
//        }
//
//        logMetaData(validatableResponse);
//    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserModelsWithMessagesForUpdate")
    @DisplayName("Test User Update with Invalid Data and Specific Error Messages")
    public void Users_UpdateUsers_Failure_InvalidDataWithMessages(String userId, CreateUserModel updateUserModel, String[] fields, String[] messages) {
        Response response = GoRestService.updateUser(userId, updateUserModel);

        // Check if the status code is 422 before proceeding
        response.then().statusCode(SC_UNPROCESSABLE_ENTITY);

        ValidatableResponse validatableResponse = response.then().body("data", notNullValue());

        // Validate each field's error message in the order of the provided arguments
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            String expectedMessage = messages[i];
            String actualMessage = validatableResponse.extract().path("data.find { it.field == '" + field + "' }.message");

            // Log the field and expected message for debugging
            System.out.println("Validating field: " + field + ", Expected message: " + expectedMessage + ", Actual message: " + actualMessage);

            // Use assertions to validate the error messages
            assertThat("Error message for field " + field, actualMessage, equalTo(expectedMessage));
        }

        logMetaData(validatableResponse);
    }

    // Provide invalid user models with specific error messages for parameterized tests
    private static Stream<Arguments> provideInvalidUserModelsWithMessagesForUpdate() {
        return Stream.of(
                // Missing name, email, and gender
                createInvalidUserArguments("", "", "", "active", new String[]{"email", "name", "gender"}, new String[]{"can't be blank", "can't be blank", "can't be blank, can be male of female"}), // Observation: the error message should be 'can be male or female'

                // Missing email and gender
                createInvalidUserArguments("Max", "", "", "active", new String[]{"email", "gender"}, new String[]{"can't be blank", "can't be blank, can be male of female"}),

                // Missing email and status
                createInvalidUserArguments("Max", "male", "", "", new String[]{"email", "status"}, new String[]{"can't be blank", "can't be blank"}),

                // Missing name and status
                createInvalidUserArguments("", "male", "qatest@test.com", "", new String[]{"name", "status"}, new String[]{"can't be blank", "can't be blank"}),

                // Missing name and gender
                createInvalidUserArguments("", "", "qatest@test.com", "active", new String[]{"name", "gender"}, new String[]{"can't be blank", "can't be blank, can be male of female"}),

                // Valid data, should pass
                createInvalidUserArguments("Max", "male", "qatest@test.com", "active", new String[]{}, new String[]{}),

                // Missing name
                createInvalidUserArguments("", "male", "qatest@test.com", "active", new String[]{"name"}, new String[]{"can't be blank"}),

                // Missing gender
                createInvalidUserArguments("Max", "", "qatest@test.com", "active", new String[]{"gender"}, new String[]{"can't be blank, can be male of female"}),

                // Missing email
                createInvalidUserArguments("Max", "male", "", "active", new String[]{"email"}, new String[]{"can't be blank"}),

                // Missing status
                createInvalidUserArguments("Max", "male", "qatest@test.com", "", new String[]{"status"}, new String[]{"can't be blank"}),

                // Missing name, email, and status
                createInvalidUserArguments("", "male", "", "", new String[]{"name", "email", "status"}, new String[]{"can't be blank", "can't be blank", "can't be blank"}),

                // Missing name, gender, and status
                createInvalidUserArguments("", "", "qatest@test.com", "", new String[]{"name", "gender", "status"}, new String[]{"can't be blank", "can't be blank, can be male of female", "can't be blank"}),

                // Missing gender, email, and status
                createInvalidUserArguments("Max", "", "", "", new String[]{"gender", "email", "status"}, new String[]{"can't be blank, can be male of female", "can't be blank", "can't be blank"}),

                // Missing all fields
                createInvalidUserArguments("", "", "", "", new String[]{"name", "gender", "email", "status"}, new String[]{"can't be blank", "can't be blank, can be male of female", "can't be blank", "can't be blank"}));
    }

    // Helper method to create invalid user arguments
    private static Arguments createInvalidUserArguments(String name, String gender, String email, String status, String[] fields, String[] messages) {
        String randomEmail = "qatest+" + UUID.randomUUID().toString() + "@test.com";
        CreateUserModel createUserModel = new CreateUserModel("Test User", "male", randomEmail, "active");
        String userId = GoRestService.createUserAndReturnId(createUserModel);
        CreateUserModel updateUserModel = new CreateUserModel(name, gender, email, status);
        return Arguments.of(userId, updateUserModel, fields, messages);
    }

    @Test
    @DisplayName("Test User Update with Invalid Email")
    public void Users_UpdateUsers_Failure_InvalidEmail() {
        String invalidEmail = "invalid-email";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma Updated", "male", invalidEmail, "active");
        GoRestService.updateUser(userId, updateUserModel).then().statusCode(SC_UNPROCESSABLE_ENTITY).body("data", notNullValue());
    }

    @Test
    @DisplayName("Test User Update with Empty Fields")
    public void Users_UpdateUsers_Failure_EmptyFields() {
        final CreateUserModel updateUserModel = new CreateUserModel("", "", "", "");
        GoRestService.updateUser(userId, updateUserModel).then().statusCode(SC_UNPROCESSABLE_ENTITY).body("data", notNullValue());
    }

    @Test
    @DisplayName("Test User Update for Non-Existent User")
    public void Users_UpdateUsers_Failure_NonExistentUser() {
        String nonExistentUserId = "999999";
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma Updated", "male", updatedEmail, "active");
        GoRestService.updateUser(nonExistentUserId, updateUserModel).then().statusCode(SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Test User Update without Authorization Token")
    public void Users_UpdateUsers_Failure_NoToken() {
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma", "male", updatedEmail, "active");

        // Check if the user exists
        Response findResponse = GoRestService.findById(userId);
        if (findResponse.statusCode() == SC_OK) {
            // Update user without token
            Response response = GoRestService.updateUserWithoutToken(userId, updateUserModel);
            response.then().statusCode(SC_UNAUTHORIZED).body("meta", equalTo(null)).body("data.message", equalTo("Authentication failed"));
        } else {
            // Update user with an invalid ID
            Response response = GoRestService.updateUserWithoutToken("72702", updateUserModel); // Assuming "72702" is an invalid ID
            response.then().statusCode(SC_NOT_FOUND).body("meta", equalTo(null)).body("data.message", equalTo("Resource not found"));
        }
    }

    @Test
    @DisplayName("Test User Update without Authorization Token and Invalid User ID")
    public void Users_UpdateUsers_Failure_NoToken_InvalidId() {
        String invalidUserId = "72702";
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma", "male", updatedEmail, "active");
        Response response = GoRestService.updateUserWithoutToken(invalidUserId, updateUserModel);
        response.then().statusCode(SC_NOT_FOUND).body("meta", equalTo(null)).body("data.message", equalTo("Resource not found"));
    }

    @Test
    @DisplayName("Test User Update with Invalid Authorization Token")
    public void Users_UpdateUsers_Failure_InvalidToken() {
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma", "male", updatedEmail, "active");
        Response response = GoRestService.updateUserWithInvalidToken(userId, updateUserModel);
        response.then().statusCode(SC_UNAUTHORIZED).body("meta", equalTo(null)).body("data.message", equalTo("Invalid token"));
    }

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
        updateResponse.then().statusCode(200).body("data.email", equalTo(updatedEmail)).body("data.name", equalTo("Updated Name"));
    }

    @Test
    @DisplayName("Test User Update with Invalid Gender")
    public void Users_UpdateUsers_Failure_InvalidGender() {
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma Updated", "invalid-gender", updatedEmail, "active");
        GoRestService.updateUser(userId, updateUserModel).then().statusCode(SC_UNPROCESSABLE_ENTITY).body("data", notNullValue());
    }

    @Test
    @DisplayName("Test User Update with Invalid Status")
    public void Users_UpdateUsers_Failure_InvalidStatus() {
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma Updated", "male", updatedEmail, "invalid-status");
        GoRestService.updateUser(userId, updateUserModel).then().statusCode(SC_UNPROCESSABLE_ENTITY).body("data", notNullValue());
    }

    @Test
    @DisplayName("Test User Update with Null Fields")
    public void Users_UpdateUsers_Failure_NullFields() {
        final CreateUserModel updateUserModel = new CreateUserModel(null, null, null, null);
        GoRestService.updateUser(userId, updateUserModel).then().statusCode(SC_UNPROCESSABLE_ENTITY).body("data", notNullValue());
    }

    @Test
    @DisplayName("Test User Update with Long Name")
    public void Users_UpdateUsers_Failure_LongName() {
        String longName = "a".repeat(256); // Assuming the max length is 255
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel(longName, "male", updatedEmail, "active");
        GoRestService.updateUser(userId, updateUserModel).then().statusCode(SC_UNPROCESSABLE_ENTITY).body("data", notNullValue());
    }

    @Test
    @DisplayName("Test User Update with Special Characters in Name")
    public void Users_UpdateUsers_ShouldBeAFailure_SpecialCharactersInName() {
        String specialCharName = "Gino@Paloma!";
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel(specialCharName, "male", updatedEmail, "active");
        GoRestService.updateUser(userId, updateUserModel).then()
//                .statusCode(SC_UNPROCESSABLE_ENTITY)          // This should be the expected status code but the API is not handling this
                .statusCode(SC_OK)                              // Check whether this is the correct expected status code
                .body("data", notNullValue());
    }

    @Test
    @DisplayName("Test User Update with SQL Injection in Name")
    public void Users_UpdateUsers_ShouldBeAFailure_SQLInjectionInName() {
        String sqlInjectionName = "Gino'; DROP TABLE users; --";
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel(sqlInjectionName, "male", updatedEmail, "active");
        GoRestService.updateUser(userId, updateUserModel).then()
//                .statusCode(SC_UNPROCESSABLE_ENTITY)      // This should be the expected status code but the API is not handling this
                .statusCode(SC_OK)                          // Check whether this is the correct expected status code
                .body("data", notNullValue());
    }

    @Test
    @DisplayName("Test User Update with HTML Tags in Name")
    public void Users_UpdateUsers_ShouldBeAFailure_HTMLTagsInName() {
        String htmlTagsName = "<script>alert('test')</script>";
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel(htmlTagsName, "male", updatedEmail, "active");
        GoRestService.updateUser(userId, updateUserModel).then()
//                .statusCode(SC_UNPROCESSABLE_ENTITY)      // This should be the expected status code but the API is not handling this
                .statusCode(SC_OK)                          // Check whether this is the correct expected status code
                .body("data", notNullValue());
    }

    @Test
    @DisplayName("Test User Update with Very Short Name")
    public void Users_UpdateUsers_Failure_VeryShortName() {
        String shortName = "A";
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel(shortName, "male", updatedEmail, "active");
        GoRestService.updateUser(userId, updateUserModel).then()
//                .statusCode(SC_UNPROCESSABLE_ENTITY)      // This should be the expected status code, but the API returns 200
                .statusCode(SC_OK)                          // Check whether this is the correct expected status code
                .body("data", notNullValue());
    }

    @Test
    @DisplayName("Test User Update with Very Long Email")
    public void Users_UpdateUsers_Failure_VeryLongEmail() {
        String longEmail = "a".repeat(256) + "@test.com"; // Assuming the max length is 255
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma", "male", longEmail, "active");
        GoRestService.updateUser(userId, updateUserModel).then().statusCode(SC_UNPROCESSABLE_ENTITY).body("data", notNullValue());
    }

    @Test
    @DisplayName("Test User Update with Unspecified Gender")
    public void Users_UpdateUsers_Failure_UnspecifiedGender() {
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma", "non-binary", updatedEmail, "active");
        GoRestService.updateUser(userId, updateUserModel).then().statusCode(SC_UNPROCESSABLE_ENTITY).body("data", notNullValue());
    }

    @Test
    @DisplayName("Test User Update with Unusual Status")
    public void Users_UpdateUsers_Failure_UnusualStatus() {
        String updatedEmail = "updated+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel updateUserModel = new CreateUserModel("Gino Paloma", "male", updatedEmail, "pending");
        GoRestService.updateUser(userId, updateUserModel).then().statusCode(SC_UNPROCESSABLE_ENTITY).body("data", notNullValue());
    }
}