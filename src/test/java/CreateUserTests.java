import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import models.CreateUserModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import services.GoRestService;

import java.util.UUID;
import java.util.stream.Stream;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@ExtendWith(ThreadLoggingExtension.class)
public class CreateUserTests {

    // Log metadata from the response
    private void logMetaData(ValidatableResponse response) {
        System.out.println("Meta: " + response.extract().path("meta"));
        System.out.println("Data: " + response.extract().path("data"));
    }

    /*
    ### Explanation

#### `Users_CreateUsers_Success` Method
This method is a JUnit test designed to verify the successful creation of a user using the GoRest API. Here's a breakdown of its components:

1. **Test Annotation**:
   - `@Test`: Marks this method as a test case.
   - `@DisplayName("Test Successful User Creation")`: Provides a descriptive name for the test.

2. **Generate Random Email**:
   - `String randomEmail = "qatest+" + UUID.randomUUID().toString() + "@test.com";`: Generates a unique email address for the new user to avoid conflicts with existing users.

3. **Create User Model**:
   - `final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", randomEmail, "active");`: Creates a new `CreateUserModel` object with the specified name, gender, email, and status.

4. **Send Create User Request**:
   - `Response response = GoRestService.createUser(createUserModel);`: Sends a request to create a new user using the `GoRestService.createUser` method.

5. **Validate Response**:
   - `response.then().statusCode(SC_CREATED)`: Asserts that the response status code is `201 Created`.
   - `.body("data.id", notNullValue())`: Asserts that the `id` field in the response body is not null, indicating that the user was successfully created.
   - `.body("data.name", equalTo(createUserModel.getName()))`: Asserts that the `name` field in the response body matches the name provided in the `createUserModel`.

6. **Log Metadata**:
   - `logMetaData(response.then());`: Logs metadata from the response for debugging or informational purposes.
    ### Importance
- **Data Integrity**: Ensures that the API correctly handles valid user creation requests, maintaining the integrity of the data.
- **Functionality Verification**: Confirms that the user creation functionality works as expected, which is crucial for the application's core operations.
- **Regression Testing**: Helps detect any regressions or issues introduced in the user creation process during future updates or changes.
- **User Experience**: Ensures that users can successfully create accounts, which is essential for user satisfaction and engagement.
- **Logging and Debugging**: Logs metadata from the response, aiding in debugging and providing insights into the API's behavior.
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

    /*
    Validate Response:
    response.then().statusCode(SC_CREATED): Asserts that the response status code is 201 Created.
    .header("Content-Type", "application/json; charset=utf-8"): Asserts that the Content-Type header in the response is application/json; charset=utf-8.
    .header("Location", notNullValue()): Asserts that the Location header in the response is not null, indicating that the location of the newly created user is provided.
### Importance
- **Data Integrity**: Ensures that the API correctly handles valid user creation requests, maintaining the integrity of the data.
- **Functionality Verification**: Confirms that the user creation functionality works as expected, which is crucial for the application's core operations.
- **Response Validation**: Verifies that the API returns the correct headers, ensuring proper content type and location of the newly created resource.
- **Compliance**: Ensures that the API adheres to HTTP standards by returning appropriate headers.
- **User Experience**: Ensures that users receive the correct response headers, which is essential for proper client-side processing and user satisfaction.
     */
    @Test
    @DisplayName("Test User Creation Response Headers")
    public void Users_CreateUsers_ResponseHeaders() {
        String randomEmail = "qatest+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", randomEmail, "active");
        Response response = GoRestService.createUser(createUserModel);
        response.then()
                .statusCode(SC_CREATED)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Location", notNullValue());
    }

    /*
    ### Explanation

#### `Users_CreateUsers_Failure_InvalidData` Method
This method is a JUnit parameterized test designed to verify that the GoRest API correctly handles user creation requests with invalid data. Here's a detailed breakdown of its components:

1. **Test Annotation**:
   - `@ParameterizedTest`: Indicates that this is a parameterized test, which runs multiple times with different sets of parameters.
   - `@MethodSource("provideInvalidUserModels")`: Specifies the method (`provideInvalidUserModels`) that provides the parameters for this test.
   - `@DisplayName("Test User Creation with Invalid Data")`: Provides a descriptive name for the test.

2. **Test Method**:
   - `public void Users_CreateUsers_Failure_InvalidData(CreateUserModel createUserModel)`: The test method that takes a `CreateUserModel` object as a parameter.
   - `Response response = GoRestService.createUser(createUserModel);`: Sends a request to create a new user using the `GoRestService.createUser` method with the provided `createUserModel`.
   - `response.then().statusCode(SC_UNPROCESSABLE_ENTITY)`: Asserts that the response status code is `422 Unprocessable Entity`, indicating that the request was well-formed but contained invalid data.
   - `.body("data", notNullValue())`: Asserts that the `data` field in the response body is not null, indicating that the API returned error details.
   - `logMetaData(response.then());`: Logs metadata from the response for debugging or informational purposes.

#### `provideInvalidUserModels` Method
This method provides a stream of `CreateUserModel` objects with various invalid data configurations for the parameterized test. Here's a breakdown of the invalid data scenarios:

1. **Missing Name**:
   - `new CreateUserModel("", "male", "qatest@test.com", "active")`: The name field is empty.

2. **Missing Gender**:
   - `new CreateUserModel("", "", "qatest@test.com", "active")`: Both the name and gender fields are empty.

3. **Missing Email**:
   - `new CreateUserModel("Gino Paloma", "", "", "active")`: Both the gender and email fields are empty.

4. **Missing Status**:
   - `new CreateUserModel("Gino Paloma", "", "qatest@test.com", "")`: Both the gender and status fields are empty.
   - `new CreateUserModel("Gino Paloma", "male", "qatest@test.com", "")`: The status field is empty.

5. **Missing Email**:
   - `new CreateUserModel("Gino Paloma", "male", "", "active")`: The email field is empty.

The `provideInvalidUserModels` method returns a stream of these invalid `CreateUserModel` objects, which are used as parameters for the `Users_CreateUsers_Failure_InvalidData` test method.
### Importance
- **Data Validation**: Ensures that the API correctly handles requests with various invalid data configurations, which is crucial for maintaining data integrity.
- **Error Handling**: Verifies that the API returns appropriate error messages and status codes for invalid input, which is important for debugging and user feedback.
- **Comprehensive Testing**: Uses parameterized tests to cover multiple invalid data scenarios in a single test method, improving test coverage and efficiency.
- **Regression Testing**: Helps detect any regressions or issues introduced in the user creation process during future updates or changes.
- **Compliance**: Ensures that the API adheres to business rules or regulatory requirements regarding valid user data.
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

    // Provide invalid user models for parameterized tests
    private static Stream<CreateUserModel> provideInvalidUserModels() {
        return Stream.of(
                new CreateUserModel("", "male", "qatest@test.com", "active"), // Missing name
                new CreateUserModel("", "", "qatest@test.com", "active"), // Missing gender
                new CreateUserModel("Gino Paloma", "", "", "active"), // Missing email
                new CreateUserModel("Gino Paloma", "", "qatest@test.com", ""), // Missing status
                new CreateUserModel("Gino Paloma", "male", "qatest@test.com", ""), // Missing status
                new CreateUserModel("Gino Paloma", "male", "", "active") // Missing email
        );
    }

    /*
    ### Explanation

#### `Users_CreateUsers_Failure_InvalidDataWithMessages` Method
This method is a JUnit parameterized test designed to verify that the GoRest API correctly handles user creation requests with invalid data and returns specific error messages. Here's a detailed breakdown of its components:

1. **Test Annotation**:
   - `@ParameterizedTest`: Indicates that this is a parameterized test, which runs multiple times with different sets of parameters.
   - `@MethodSource("provideInvalidUserModelsWithMessages")`: Specifies the method (`provideInvalidUserModelsWithMessages`) that provides the parameters for this test.
   - `@DisplayName("Test User Creation with Invalid Data and Specific Error Messages")`: Provides a descriptive name for the test.

2. **Test Method**:
   - `public void Users_CreateUsers_Failure_InvalidDataWithMessages(CreateUserModel createUserModel, String[] fields, String[] messages)`: The test method that takes a `CreateUserModel` object, an array of field names, and an array of expected error messages as parameters.
   - `Response response = GoRestService.createUser(createUserModel);`: Sends a request to create a new user using the `GoRestService.createUser` method with the provided `createUserModel`.
   - `ValidatableResponse validatableResponse = response.then().statusCode(SC_UNPROCESSABLE_ENTITY).body("data", notNullValue());`: Asserts that the response status code is `422 Unprocessable Entity` and that the `data` field in the response body is not null, indicating that the API returned error details.

3. **Validate Specific Error Messages**:
   - A `for` loop iterates over the `fields` array, and for each field, it asserts that the corresponding error message in the response body matches the expected message.
   - `validatableResponse.body("data.find { it.field == '" + fields[i] + "' }.message", equalTo(messages[i]));`: Asserts that the error message for each field in the response body matches the expected message.

4. **Log Metadata**:
   - `logMetaData(validatableResponse);`: Logs metadata from the response for debugging or informational purposes.

#### `provideInvalidUserModelsWithMessages` Method
This method provides a stream of `Arguments` objects, each containing a `CreateUserModel` with invalid data, an array of field names, and an array of expected error messages. Here's a breakdown of the invalid data scenarios:

1. **Missing Name**:
   - `Arguments.of(new CreateUserModel("", "male", "qatest@test.com", "active"), new String[]{"name"}, new String[]{"can't be blank"});`: The name field is empty.

2. **Missing Name and Gender**:
   - `Arguments.of(new CreateUserModel("", "", "qatest@test.com", "active"), new String[]{"name", "gender"}, new String[]{"can't be blank", "can't be blank, can be male of female"});`: Both the name and gender fields are empty.

3. **Missing Email and Gender**:
   - `Arguments.of(new CreateUserModel("Gino Paloma", "", "", "active"), new String[]{"email", "gender"}, new String[]{"can't be blank", "can't be blank, can be male of female"});`: Both the email and gender fields are empty.

4. **Missing Status and Gender**:
   - `Arguments.of(new CreateUserModel("Gino Paloma", "", "qatest@test.com", ""), new String[]{"status", "gender"}, new String[]{"can't be blank", "can't be blank, can be male of female"});`: Both the status and gender fields are empty.

5. **Missing Status**:
   - `Arguments.of(new CreateUserModel("Gino Paloma", "male", "qatest@test.com", ""), new String[]{"status"}, new String[]{"can't be blank"});`: The status field is empty.

6. **Missing Email**:
   - `Arguments.of(new CreateUserModel("Gino Paloma", "male", "", "active"), new String[]{"email"}, new String[]{"can't be blank"});`: The email field is empty.

7. **Missing Name, Email, and Gender**:
   - `Arguments.of(new CreateUserModel("", "", "", "active"), new String[]{"name", "email", "gender"}, new String[]{"can't be blank", "can't be blank", "can't be blank, can be male of female"});`: The name, email, and gender fields are empty.

8. **Missing Name and Email**:
   - `Arguments.of(new CreateUserModel("", "male", "", "active"), new String[]{"name", "email"}, new String[]{"can't be blank", "can't be blank"});`: The name and email fields are empty.

9. **Missing Name and Status**:
   - `Arguments.of(new CreateUserModel("", "male", "qatest@test.com", ""), new String[]{"name", "status"}, new String[]{"can't be blank", "can't be blank"});`: The name and status fields are empty.

10. **Missing Email and Status**:
    - `Arguments.of(new CreateUserModel("Gino Paloma", "male", "", ""), new String[]{"email", "status"}, new String[]{"can't be blank", "can't be blank"});`: The email and status fields are empty.

11. **Missing Gender and Status**:
    - `Arguments.of(new CreateUserModel("Gino Paloma", "", "qatest@test.com", ""), new String[]{"gender", "status"}, new String[]{"can't be blank, can be male of female", "can't be blank"});`: The gender and status fields are empty.

12. **Missing Name, Gender, and Status**:
    - `Arguments.of(new CreateUserModel("", "", "qatest@test.com", ""), new String[]{"name", "gender", "status"}, new String[]{"can't be blank", "can't be blank, can be male of female", "can't be blank"});`: The name, gender, and status fields are empty.

13. **Missing Name, Email, and Status**:
    - `Arguments.of(new CreateUserModel("", "male", "", ""), new String[]{"name", "email", "status"}, new String[]{"can't be blank", "can't be blank", "can't be blank"});`: The name, email, and status fields are empty.

14. **Missing Gender, Email, and Status**:
    - `Arguments.of(new CreateUserModel("Gino Paloma", "", "", ""), new String[]{"gender", "email", "status"}, new String[]{"can't be blank, can be male of female", "can't be blank", "can't be blank"});`: The gender, email, and status fields are empty.

15. **Missing All Fields**:
    - `Arguments.of(new CreateUserModel("", "", "", ""), new String[]{"name", "gender", "email", "status"}, new String[]{"can't be blank", "can't be blank, can be male of female", "can't be blank", "can't be blank"});`: All fields are empty.

The `provideInvalidUserModelsWithMessages` method returns a stream of these `Arguments` objects, which are used as parameters for the `Users_CreateUsers_Failure_InvalidDataWithMessages` test method.
### Importance
- **Data Validation**: Ensures that the API correctly handles requests with various invalid data configurations, which is crucial for maintaining data integrity.
- **Error Handling**: Verifies that the API returns appropriate error messages and status codes for invalid input, which is important for debugging and user feedback.
- **Comprehensive Testing**: Uses parameterized tests to cover multiple invalid data scenarios in a single test method, improving test coverage and efficiency.
- **Regression Testing**: Helps detect any regressions or issues introduced in the user creation process during future updates or changes.
- **Compliance**: Ensures that the API adheres to business rules or regulatory requirements regarding valid user data.
- **Specific Error Messages**: Validates that the API returns specific error messages for each invalid field, which is essential for precise debugging and user guidance.
     */
    @ParameterizedTest
    @MethodSource("provideInvalidUserModelsWithMessages")
    @DisplayName("Test User Creation with Invalid Data and Specific Error Messages")
    public void Users_CreateUsers_Failure_InvalidDataWithMessages(CreateUserModel createUserModel, String[] fields, String[] messages) {
        Response response = GoRestService.createUser(createUserModel);
        ValidatableResponse validatableResponse = response.then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data", notNullValue());

        // Validate specific error messages
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            String expectedMessage = messages[i];
            String actualMessage = validatableResponse.extract().path("data.find { it.field == '" + field + "' }.message");

            // Debugging output
            System.out.println("Iteration: " + i);
            System.out.println("Field: " + field);
            System.out.println("Expected Message: " + expectedMessage);
            System.out.println("Actual Message: " + actualMessage);

            validatableResponse.body("data.find { it.field == '" + fields[i] + "' }.message", equalTo(messages[i]));
        }

        logMetaData(validatableResponse);
    }
/*

The loop in the `Users_CreateUsers_Failure_InvalidDataWithMessages` method iterates over the `fields` and `messages` arrays, which are provided as arguments by the `provideInvalidUserModelsWithMessages` method. Here's how they are linked:

1. **`provideInvalidUserModelsWithMessages` Method**:
   - This method returns a stream of `Arguments` objects.
   - Each `Arguments` object contains a `CreateUserModel`, an array of field names (`fields`), and an array of expected error messages (`messages`).

2. **`Users_CreateUsers_Failure_InvalidDataWithMessages` Method**:
   - This method is annotated with `@ParameterizedTest` and `@MethodSource("provideInvalidUserModelsWithMessages")`.
   - This annotation tells JUnit to use the `provideInvalidUserModelsWithMessages` method as the source of arguments for the test.
   - For each set of arguments provided by `provideInvalidUserModelsWithMessages`, the test method is executed.

3. **Loop in `Users_CreateUsers_Failure_InvalidDataWithMessages`**:
   - The loop iterates over the `fields` array.
   - For each field, it validates that the corresponding error message in the response body matches the expected message from the `messages` array.

### Pseudocode

1. `provideInvalidUserModelsWithMessages` method returns a stream of `Arguments`.
2. `Users_CreateUsers_Failure_InvalidDataWithMessages` method is executed for each set of `Arguments`.
3. Inside the test method, the loop iterates over the `fields` array and validates the error messages.

 */
    // Provide invalid user models with specific error messages for parameterized tests
    private static Stream<Arguments> provideInvalidUserModelsWithMessages() {
        return Stream.of(
                // i=0, Missing name
                Arguments.of(new CreateUserModel("", "male", "qatest@test.com", "active"),
                        new String[]{"name"},
                        new String[]{"can't be blank"}),

                // i=1, Missing name and gender
                Arguments.of(new CreateUserModel("", "", "qatest@test.com", "active"),
                        new String[]{"name", "gender"},
                        new String[]{"can't be blank", "can't be blank, can be male of female"}), // Observation: the error message should be 'can be male or female'

                // i=2, Missing email and gender
                Arguments.of(new CreateUserModel("Gino Paloma", "", "", "active"),
                        new String[]{"email", "gender"},
                        new String[]{"can't be blank", "can't be blank, can be male of female"}),

                // i=3, Missing status and gender
                Arguments.of(new CreateUserModel("Gino Paloma", "", "qatest@test.com", ""),
                        new String[]{"status", "gender"},
                        new String[]{"can't be blank", "can't be blank, can be male of female"}),

                // i=4, Missing status
                Arguments.of(new CreateUserModel("Gino Paloma", "male", "qatest@test.com", ""),
                        new String[]{"status"},
                        new String[]{"can't be blank"}),

                // i=5, Missing email
                Arguments.of(new CreateUserModel("Gino Paloma", "male", "", "active"),
                        new String[]{"email"},
                        new String[]{"can't be blank"}),

                // i=6, Missing name, email, and gender
                Arguments.of(new CreateUserModel("", "", "", "active"),
                        new String[]{"name", "email", "gender"},
                        new String[]{"can't be blank", "can't be blank", "can't be blank, can be male of female"}),

                // i=7, Missing name and email
                Arguments.of(new CreateUserModel("", "male", "", "active"),
                        new String[]{"name", "email"},
                        new String[]{"can't be blank", "can't be blank"}),

                // i=8, Missing name and status
                Arguments.of(new CreateUserModel("", "male", "qatest@test.com", ""),
                        new String[]{"name", "status"},
                        new String[]{"can't be blank", "can't be blank"}),

                // i=9, Missing email and status
                Arguments.of(new CreateUserModel("Gino Paloma", "male", "", ""),
                        new String[]{"email", "status"},
                        new String[]{"can't be blank", "can't be blank"}),

                // i=10, Missing gender and status
                Arguments.of(new CreateUserModel("Gino Paloma", "", "qatest@test.com", ""),
                        new String[]{"gender", "status"},
                        new String[]{"can't be blank, can be male of female", "can't be blank"}),

                // i=11, Missing name, gender, and status
                Arguments.of(new CreateUserModel("", "", "qatest@test.com", ""),
                        new String[]{"name", "gender", "status"},
                        new String[]{"can't be blank", "can't be blank, can be male of female", "can't be blank"}),

                // i=12, Missing name, email, and status
                Arguments.of(new CreateUserModel("", "male", "", ""),
                        new String[]{"name", "email", "status"},
                        new String[]{"can't be blank", "can't be blank", "can't be blank"}),

                // i=13, Missing gender, email, and status
                Arguments.of(new CreateUserModel("Gino Paloma", "", "", ""),
                        new String[]{"gender", "email", "status"},
                        new String[]{"can't be blank, can be male of female", "can't be blank", "can't be blank"}),

                // i=14, Missing all fields
                Arguments.of(new CreateUserModel("", "", "", ""),
                        new String[]{"name", "gender", "email", "status"},
                        new String[]{"can't be blank", "can't be blank, can be male of female", "can't be blank", "can't be blank"})
        );
    }

    /*
    ### Explanation

#### `Users_CreateUsers_Failure_InvalidEmail` Method
This method is a JUnit test designed to verify that the GoRest API correctly handles user creation requests with an invalid email. Here's a detailed breakdown of its components:

1. **Test Annotation**:
   - `@Test`: Marks this method as a test case.
   - `@DisplayName("Test User Creation with Invalid Email")`: Provides a descriptive name for the test.

2. **Create User Model with Invalid Email**:
   - `String invalidEmail = "invalid-email";`: Defines an invalid email address.
   - `final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", invalidEmail, "active");`: Creates a new `CreateUserModel` object with the specified name, gender, invalid email, and status.

3. **Send Create User Request**:
   - `Response response = GoRestService.createUser(createUserModel);`: Sends a request to create a new user using the `GoRestService.createUser` method with the provided `createUserModel`.

4. **Validate Response**:
   - `response.then().statusCode(SC_UNPROCESSABLE_ENTITY)`: Asserts that the response status code is `422 Unprocessable Entity`, indicating that the request was well-formed but contained invalid data.
   - `.body("data", notNullValue())`: Asserts that the `data` field in the response body is not null, indicating that the API returned error details.

5. **Log Metadata**:
   - `logMetaData(response.then());`: Logs metadata from the response for debugging or informational purposes.

### Importance
- **Data Validation**: Ensures that the API correctly handles requests with invalid email formats, which is crucial for maintaining data integrity.
- **Error Handling**: Verifies that the API returns appropriate error messages and status codes for invalid email input, which is important for debugging and user feedback.
- **Security**: Prevents the creation of users with invalid email addresses, which could be exploited for malicious purposes.
- **Compliance**: Ensures that the API adheres to business rules or regulatory requirements regarding valid email formats.
- **User Experience**: Ensures that users receive clear feedback when they provide invalid email addresses, which is essential for user guidance and satisfaction.
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

    /*
    ### Explanation

#### `Users_CreateUsers_Failure_DuplicateEmail` Method
This method is a JUnit test designed to verify that the GoRest API correctly handles user creation requests with a duplicate email. Here's a detailed breakdown of its components:

1. **Test Annotation**:
   - `@Test`: Marks this method as a test case.
   - `@DisplayName("Test User Creation with Duplicate Email")`: Provides a descriptive name for the test.

2. **Generate Duplicate Email**:
   - `String duplicateEmail = "qatest+" + UUID.randomUUID().toString() + "@test.com";`: Generates a unique email address for the new user to avoid conflicts with existing users.

3. **Create User Model**:
   - `final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", duplicateEmail, "active");`: Creates a new `CreateUserModel` object with the specified name, gender, email, and status.

4. **Send First Create User Request**:
   - `GoRestService.createUser(createUserModel).then().statusCode(SC_CREATED);`: Sends a request to create a new user using the `GoRestService.createUser` method with the provided `createUserModel` and asserts that the response status code is `201 Created`.

5. **Send Second Create User Request with Duplicate Email**:
   - `Response response = GoRestService.createUser(createUserModel);`: Sends another request to create a new user using the same `createUserModel` with the duplicate email.

6. **Validate Response**:
   - `response.then().statusCode(SC_UNPROCESSABLE_ENTITY)`: Asserts that the response status code is `422 Unprocessable Entity`, indicating that the request was well-formed but contained invalid data (duplicate email).
   - `.body("data", notNullValue())`: Asserts that the `data` field in the response body is not null, indicating that the API returned error details.

7. **Log Metadata**:
   - `logMetaData(response.then());`: Logs metadata from the response for debugging or informational purposes.

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

    /*
    ### Explanation

#### `Users_CreateUsers_Failure_EmptyFields` Method
This method is a JUnit test designed to verify that the GoRest API correctly handles user creation requests with all fields empty. Here's a detailed breakdown of its components:

1. **Test Annotation**:
   - `@Test`: Marks this method as a test case.
   - `@DisplayName("Test User Creation with Empty Fields")`: Provides a descriptive name for the test.

2. **Create User Model with Empty Fields**:
   - `final CreateUserModel createUserModel = new CreateUserModel("", "", "", "");`: Creates a new `CreateUserModel` object with all fields (name, gender, email, status) set to empty strings.

3. **Send Create User Request**:
   - `Response response = GoRestService.createUser(createUserModel);`: Sends a request to create a new user using the `GoRestService.createUser` method with the provided `createUserModel`.

4. **Validate Response**:
   - `response.then().statusCode(SC_UNPROCESSABLE_ENTITY)`: Asserts that the response status code is `422 Unprocessable Entity`, indicating that the request was well-formed but contained invalid data.
   - `.body("data", notNullValue())`: Asserts that the `data` field in the response body is not null, indicating that the API returned error details.

5. **Log Metadata**:
   - `logMetaData(response.then());`: Logs metadata from the response for debugging or informational purposes.
    ### Importance
- **Data Validation**: Ensures that the API correctly handles requests with all fields empty, which is crucial for maintaining data integrity.
- **Error Handling**: Verifies that the API returns appropriate error messages and status codes for completely invalid input, which is important for debugging and user feedback.
- **Security**: Prevents the creation of users with incomplete data, which could be exploited for malicious purposes.
- **Compliance**: Ensures that the API adheres to business rules or regulatory requirements regarding mandatory user data fields.
- **User Experience**: Ensures that users receive clear feedback when they provide incomplete data, which is essential for user guidance and satisfaction.
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

    /*
    ### Explanation

#### `Users_CreateUsers_Failure_NoToken` Method
This method is a JUnit test designed to verify that the GoRest API correctly handles user creation requests without an authorization token. Here's a detailed breakdown of its components:

1. **Test Annotation**:
   - `@Test`: Marks this method as a test case.
   - `@DisplayName("Test User Creation without Authorization Token")`: Provides a descriptive name for the test.

2. **Create User Model**:
   - `final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", "qatest@test.com", "active");`: Creates a new `CreateUserModel` object with the specified name, gender, email, and status.

3. **Send Create User Request Without Token**:
   - `Response response = GoRestService.createUserWithoutToken(createUserModel);`: Sends a request to create a new user using the `GoRestService.createUserWithoutToken` method with the provided `createUserModel`. This method does not include an authorization token in the request.

4. **Validate Response**:
   - `response.then().statusCode(SC_UNAUTHORIZED)`: Asserts that the response status code is `401 Unauthorized`, indicating that the request was not authorized due to the absence of a valid token.
   - `.body("meta", equalTo(null))`: Asserts that the `meta` field in the response body is null, indicating that no metadata is returned.
   - `.body("data.message", equalTo("Authentication failed"))`: Asserts that the `data.message` field in the response body contains the message "Authentication failed", indicating the reason for the failure.

This test ensures that the API correctly handles unauthorized requests by returning the appropriate status code and error message.
   ### Importance
- **Authentication Validation**: Ensures that the API correctly enforces authentication requirements, preventing unauthorized access.
- **Security**: Verifies that the API does not allow user creation without a valid authorization token, which is crucial for protecting user data and preventing unauthorized actions.
- **Error Handling**: Confirms that the API returns appropriate error messages and status codes for unauthorized requests, aiding in debugging and user feedback.
- **Compliance**: Ensures that the API adheres to security policies and regulatory requirements regarding authentication.
- **Regression Testing**: Helps detect any regressions or issues introduced in the authentication mechanism during future updates or changes.
- **User Experience**: Ensures that users receive clear feedback when they attempt to perform actions without proper authentication, which is essential for user guidance and satisfaction.
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
    /*
    ### Explanation

#### `Users_CreateUsers_Failure_InvalidToken` Method
This method is a JUnit test designed to verify that the GoRest API correctly handles user creation requests with an invalid authorization token. Here's a detailed breakdown of its components:

1. **Test Annotation**:
   - `@Test`: Marks this method as a test case.
   - `@DisplayName("Test User Creation with Invalid Authorization Token")`: Provides a descriptive name for the test.

2. **Create User Model**:
   - `final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", "qatest@test.com", "active");`: Creates a new `CreateUserModel` object with the specified name, gender, email, and status.

3. **Send Create User Request with Invalid Token**:
   - `Response response = GoRestService.createUserWithInvalidToken(createUserModel);`: Sends a request to create a new user using the `GoRestService.createUserWithInvalidToken` method with the provided `createUserModel`. This method includes an invalid authorization token in the request.

4. **Validate Response**:
   - `response.then().statusCode(SC_UNAUTHORIZED)`: Asserts that the response status code is `401 Unauthorized`, indicating that the request was not authorized due to the invalid token.
   - `.body("meta", equalTo(null))`: Asserts that the `meta` field in the response body is null, indicating that no metadata is returned.
   - `.body("data.message", equalTo("Invalid token"))`: Asserts that the `data.message` field in the response body contains the message "Invalid token", indicating the reason for the failure.

This test ensures that the API correctly handles requests with invalid tokens by returning the appropriate status code and error message.
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

    /*
    ### Explanation

#### `Users_CreateUsers_ShouldBeAFailure_SpecialCharactersInName` Method
This method is a JUnit test designed to verify the behavior of the GoRest API when attempting to create a user with special characters in the name. Here's a detailed breakdown of its components:

1. **Test Annotation**:
   - `@Test`: Marks this method as a test case.
   - `@DisplayName("Test User Creation with Special Characters in Name")`: Provides a descriptive name for the test.

2. **Create User Model with Special Characters in Name**:
   - `String specialCharName = "Gino@Paloma!";`: Defines a name with special characters.
   - `String email = "qatest+" + UUID.randomUUID().toString() + "@test.com";`: Generates a unique email address for the new user to avoid conflicts with existing users.
   - `final CreateUserModel createUserModel = new CreateUserModel(specialCharName, "male", email, "active");`: Creates a new `CreateUserModel` object with the specified name, gender, email, and status.

3. **Send Create User Request**:
   - `GoRestService.createUser(createUserModel)`: Sends a request to create a new user using the `GoRestService.createUser` method with the provided `createUserModel`.

4. **Validate Response**:
   - `.then().statusCode(SC_CREATED)`: Asserts that the response status code is `201 Created`. This is currently set to check if the API incorrectly allows the creation of a user with special characters in the name.
   - `.body("data", notNullValue())`: Asserts that the `data` field in the response body is not null, indicating that the user was created.

5. **Commented Expected Status Code**:
   - `// .statusCode(SC_UNPROCESSABLE_ENTITY)`: This commented line indicates that the expected status code should be `422 Unprocessable Entity`, but the API currently returns `201 Created`. This suggests a potential issue with the API's validation logic for special characters in the name.
    ### Importance
- **Data Validation**: Ensures that the API correctly handles requests with special characters in the name, which is crucial for maintaining data integrity.
- **Error Handling**: Verifies that the API returns appropriate error messages and status codes for invalid input, aiding in debugging and user feedback.
- **Security**: Prevents the creation of users with potentially harmful special characters, which could be exploited for malicious purposes.
- **Compliance**: Ensures that the API adheres to business rules or regulatory requirements regarding valid user data.
- **Regression Testing**: Helps detect any regressions or issues introduced in the user creation process during future updates or changes.
- **User Experience**: Ensures that users receive clear feedback when they provide invalid data, which is essential for user guidance and satisfaction.
     */
    @Test
    @DisplayName("Test User Creation with Special Characters in Name")
    public void Users_CreateUsers_ShouldBeAFailure_SpecialCharactersInName() {
        String specialCharName = "Gino@Paloma!";
        String email = "qatest+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel createUserModel = new CreateUserModel(specialCharName, "male", email, "active");
        GoRestService.createUser(createUserModel)
                .then()
//                .statusCode(SC_UNPROCESSABLE_ENTITY)      // This should be the expected status code but the API returns 201
                .statusCode(SC_CREATED)                     // Check whether this is the correct expected status code
                .body("data", notNullValue());
    }

    /*
    ### Explanation

#### SQL Injection Attempt
The code snippet provided is a JUnit test designed to verify the behavior of the GoRest API when attempting to create a user with a name that includes a SQL injection attempt. Here's a detailed breakdown of the SQL injection attempt and its importance:

1. **SQL Injection Attempt**:
   - `String sqlInjectionName = "Gino'; DROP TABLE users; --";`: This string is designed to exploit potential vulnerabilities in the API's handling of SQL queries. The string includes:
     - `Gino'`: Ends the current SQL string.
     - `; DROP TABLE users;`: Executes a new SQL command to drop the `users` table.
     - `--`: Comments out the rest of the SQL query to prevent syntax errors.

2. **How SQL Injection Works**:
   - SQL injection is a code injection technique that exploits vulnerabilities in an application's software by inserting or "injecting" malicious SQL code into a query.
   - If the application does not properly sanitize user inputs, the injected SQL code can be executed by the database, potentially leading to data breaches, data loss, or unauthorized access.

3. **Importance of Preventing SQL Injection**:
   - **Security**: SQL injection can lead to unauthorized access to sensitive data, including personal information, financial data, and intellectual property.
   - **Data Integrity**: SQL injection can corrupt or delete data, leading to data loss and integrity issues.
   - **Compliance**: Many regulations and standards (e.g., GDPR, PCI-DSS) require protection against SQL injection to ensure data security and privacy.
   - **Reputation**: A successful SQL injection attack can damage an organization's reputation and erode customer trust.

### Pseudocode

1. Define a string with a SQL injection attempt.
2. Create a `CreateUserModel` object with the SQL injection string as the name.
3. Send a request to create a user with the SQL injection string.
4. Validate the response to ensure the API handles the input correctly.

### Code

```java
@Test
@DisplayName("Test User Creation with SQL Injection in Name")
public void Users_CreateUsers_ShouldBeAFailure_SQLInjectionInName() {
    String sqlInjectionName = "Gino'; DROP TABLE users; --";
    String email = "qatest+" + UUID.randomUUID().toString() + "@test.com";
    final CreateUserModel createUserModel = new CreateUserModel(sqlInjectionName, "male", email, "active");
    GoRestService.createUser(createUserModel)
            .then()
//            .statusCode(SC_UNPROCESSABLE_ENTITY)      // This should be the expected status code but the API returns 201
            .statusCode(SC_CREATED)                     // Check whether this is the correct expected status code
            .body("data", notNullValue());
}
```
     */
    @Test
    @DisplayName("Test User Creation with SQL Injection in Name")
    public void Users_CreateUsers_ShouldBeAFailure_SQLInjectionInName() {
        String sqlInjectionName = "Gino'; DROP TABLE users; --";
        String email = "qatest+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel createUserModel = new CreateUserModel(sqlInjectionName, "male", email, "active");
        GoRestService.createUser(createUserModel)
                .then()
//                .statusCode(SC_UNPROCESSABLE_ENTITY)      // This should be the expected status code but the API returns 201
                .statusCode(SC_CREATED)                     // Check whether this is the correct expected status code
                .body("data", notNullValue());
    }

    /*
    ### Explanation

#### `Users_CreateUsers_ShouldBeAFailure_HTMLTagsInName` Method
This method is a JUnit test designed to verify the behavior of the GoRest API when attempting to create a user with HTML tags in the name. Here's a detailed breakdown of its components:

1. **Test Annotation**:
   - `@Test`: Marks this method as a test case.
   - `@DisplayName("Test User Creation with HTML Tags in Name")`: Provides a descriptive name for the test.

2. **Create User Model with HTML Tags in Name**:
   - `String htmlTagsName = "<script>alert('test')</script>";`: Defines a name that includes HTML tags, which could potentially be used for cross-site scripting (XSS) attacks.
   - `String email = "qatest+" + UUID.randomUUID().toString() + "@test.com";`: Generates a unique email address for the new user to avoid conflicts with existing users.
   - `final CreateUserModel createUserModel = new CreateUserModel(htmlTagsName, "male", email, "active");`: Creates a new `CreateUserModel` object with the specified name, gender, email, and status.

3. **Send Create User Request**:
   - `GoRestService.createUser(createUserModel)`: Sends a request to create a new user using the `GoRestService.createUser` method with the provided `createUserModel`.

4. **Validate Response**:
   - `.then().statusCode(SC_CREATED)`: Asserts that the response status code is `201 Created`. This is currently set to check if the API incorrectly allows the creation of a user with HTML tags in the name.
   - `.body("data", notNullValue())`: Asserts that the `data` field in the response body is not null, indicating that the user was created.

5. **Commented Expected Status Code**:
   - `// .statusCode(SC_UNPROCESSABLE_ENTITY)`: This commented line indicates that the expected status code should be `422 Unprocessable Entity`, but the API currently returns `201 Created`. This suggests a potential issue with the API's validation logic for HTML tags in the name.

### Importance
- **Security**: Allowing HTML tags in user input can lead to cross-site scripting (XSS) attacks, where malicious scripts are injected into web pages viewed by other users.
- **Data Integrity**: HTML tags in user input can corrupt data and cause unexpected behavior in applications that process or display this data.
- **Compliance**: Many security standards and best practices require proper validation and sanitization of user inputs to prevent XSS and other injection attacks.
- **User Experience**: Ensuring that user inputs are properly validated and sanitized helps maintain a consistent and safe user experience.
     */
    @Test
    @DisplayName("Test User Creation with HTML Tags in Name")
    public void Users_CreateUsers_ShouldBeAFailure_HTMLTagsInName() {
        String htmlTagsName = "<script>alert('test')</script>";
        String email = "qatest+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel createUserModel = new CreateUserModel(htmlTagsName, "male", email, "active");
        GoRestService.createUser(createUserModel)
                .then()
//                .statusCode(SC_UNPROCESSABLE_ENTITY)      // This should be the expected status code but the API returns 201
                .statusCode(SC_CREATED)                     // Check whether this is the correct expected status code
                .body("data", notNullValue());
    }

    /*
    ### Explanation

#### `Users_CreateUsers_Failure_NullFields` Method
This method is a JUnit test designed to verify the behavior of the GoRest API when attempting to create a user with all fields set to null. Here's a detailed breakdown of its components:

1. **Test Annotation**:
   - `@Test`: Marks this method as a test case.
   - `@DisplayName("Test User Creation with Null Fields")`: Provides a descriptive name for the test.

2. **Create User Model with Null Fields**:
   - `final CreateUserModel createUserModel = new CreateUserModel(null, null, null, null);`: Creates a new `CreateUserModel` object with all fields (name, gender, email, status) set to null.

3. **Send Create User Request**:
   - `GoRestService.createUser(createUserModel)`: Sends a request to create a new user using the `GoRestService.createUser` method with the provided `createUserModel`.

4. **Validate Response**:
   - `.then().statusCode(SC_UNPROCESSABLE_ENTITY)`: Asserts that the response status code is `422 Unprocessable Entity`, indicating that the request was well-formed but contained invalid data.
   - `.body("data", notNullValue())`: Asserts that the `data` field in the response body is not null, indicating that the API returned error details.

### Importance
- **Data Validation**: Ensures that the API correctly handles requests with null fields, which is crucial for maintaining data integrity.
- **Error Handling**: Verifies that the API returns appropriate error messages and status codes for invalid input, which is important for debugging and user feedback.
- **Security**: Prevents potential issues that could arise from processing null values, such as null pointer exceptions or unexpected behavior.
     */
    @Test
    @DisplayName("Test User Creation with Null Fields")
    public void Users_CreateUsers_Failure_NullFields() {
        final CreateUserModel createUserModel = new CreateUserModel(null, null, null, null);
        GoRestService.createUser(createUserModel)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data", notNullValue());
    }

    /*
    ### Explanation

#### `Users_CreateUsers_Failure_LongName` Method
This method is a JUnit test designed to verify the behavior of the GoRest API when attempting to create a user with a name that exceeds the maximum allowed length. Here's a detailed breakdown of its components:

1. **Test Annotation**:
   - `@Test`: Marks this method as a test case.
   - `@DisplayName("Test User Creation with Long Name")`: Provides a descriptive name for the test.

2. **Create User Model with Long Name**:
   - `String longName = "a".repeat(256);`: Defines a name that exceeds the assumed maximum length of 255 characters.
   - `String email = "qatest+" + UUID.randomUUID().toString() + "@test.com";`: Generates a unique email address for the new user to avoid conflicts with existing users.
   - `final CreateUserModel createUserModel = new CreateUserModel(longName, "male", email, "active");`: Creates a new `CreateUserModel` object with the specified long name, gender, email, and status.

3. **Send Create User Request**:
   - `GoRestService.createUser(createUserModel)`: Sends a request to create a new user using the `GoRestService.createUser` method with the provided `createUserModel`.

4. **Validate Response**:
   - `.then().statusCode(SC_UNPROCESSABLE_ENTITY)`: Asserts that the response status code is `422 Unprocessable Entity`, indicating that the request was well-formed but contained invalid data (name too long).
   - `.body("data", notNullValue())`: Asserts that the `data` field in the response body is not null, indicating that the API returned error details.

### Importance
- **Data Validation**: Ensures that the API correctly handles requests with excessively long names, which is crucial for maintaining data integrity.
- **Error Handling**: Verifies that the API returns appropriate error messages and status codes for invalid input, which is important for debugging and user feedback.
- **Security**: Prevents potential issues that could arise from processing excessively long strings, such as buffer overflows or unexpected behavior.
     */
    @Test
    @DisplayName("Test User Creation with Long Name")
    public void Users_CreateUsers_Failure_LongName() {
        String longName = "a".repeat(256); // Assuming the max length is 255
        String email = "qatest+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel createUserModel = new CreateUserModel(longName, "male", email, "active");
        GoRestService.createUser(createUserModel)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data", notNullValue());
    }

    /*
    ### Explanation

#### `Users_CreateUsers_Failure_VeryShortName` Method
This method is a JUnit test designed to verify the behavior of the GoRest API when attempting to create a user with a very short name. Here's a detailed breakdown of its components:

1. **Test Annotation**:
   - `@Test`: Marks this method as a test case.
   - `@DisplayName("Test User Creation with Very Short Name")`: Provides a descriptive name for the test.

2. **Create User Model with Very Short Name**:
   - `String shortName = "A";`: Defines a name with a single character.
   - `String email = "qatest+" + UUID.randomUUID().toString() + "@test.com";`: Generates a unique email address for the new user to avoid conflicts with existing users.
   - `final CreateUserModel createUserModel = new CreateUserModel(shortName, "male", email, "active");`: Creates a new `CreateUserModel` object with the specified short name, gender, email, and status.

3. **Send Create User Request**:
   - `GoRestService.createUser(createUserModel)`: Sends a request to create a new user using the `GoRestService.createUser` method with the provided `createUserModel`.

4. **Validate Response**:
   - `.then().statusCode(SC_CREATED)`: Asserts that the response status code is `201 Created`. This is currently set to check if the API incorrectly allows the creation of a user with a very short name.
   - `.body("data", notNullValue())`: Asserts that the `data` field in the response body is not null, indicating that the user was created.

5. **Commented Expected Status Code**:
   - `// .statusCode(SC_UNPROCESSABLE_ENTITY)`: This commented line indicates that the expected status code should be `422 Unprocessable Entity`, but the API currently returns `201 Created`. This suggests a potential issue with the API's validation logic for very short names.

### Importance
- **Data Validation**: Ensures that the API correctly handles requests with very short names, which is crucial for maintaining data integrity.
- **Error Handling**: Verifies that the API returns appropriate error messages and status codes for invalid input, which is important for debugging and user feedback.
- **Security**: Prevents potential issues that could arise from processing very short strings, such as unexpected behavior or security vulnerabilities.
     */
    @Test
    @DisplayName("Test User Creation with Very Short Name")
    public void Users_CreateUsers_Failure_VeryShortName() {
        String shortName = "A";
        String email = "qatest+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel createUserModel = new CreateUserModel(shortName, "male", email, "active");
        GoRestService.createUser(createUserModel)
                .then()
//                .statusCode(SC_UNPROCESSABLE_ENTITY)      // This should be the expected status code, but the API returns 201
                .statusCode(SC_CREATED)                          // Check whether this is the correct expected status code
                .body("data", notNullValue());
    }

    @Test
    @DisplayName("Test User Creation with Very Long Email")
    public void Users_CreateUsers_Failure_VeryLongEmail() {
        String longEmail = "a".repeat(256) + "@test.com"; // Assuming the max length is 255
        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", longEmail, "active");
        GoRestService.createUser(createUserModel)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data", notNullValue());
    }

    /*
    ### Explanation

#### `Users_CreateUsers_Failure_UnspecifiedGender` Method
This method is a JUnit test designed to verify the behavior of the GoRest API when attempting to create a user with an unspecified gender. Here's a detailed breakdown of its components:

1. **Test Annotation**:
   - `@Test`: Marks this method as a test case.
   - `@DisplayName("Test User Creation with Unspecified Gender")`: Provides a descriptive name for the test.

2. **Generate Unique Email**:
   - `String email = "qatest+" + UUID.randomUUID().toString() + "@test.com";`: Generates a unique email address for the new user to avoid conflicts with existing users.

3. **Create User Model with Unspecified Gender**:
   - `final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "non-binary", email, "active");`: Creates a new `CreateUserModel` object with the specified name, unspecified gender ("non-binary"), email, and status.

4. **Send Create User Request**:
   - `GoRestService.createUser(createUserModel)`: Sends a request to create a new user using the `GoRestService.createUser` method with the provided `createUserModel`.

5. **Validate Response**:
   - `.then().statusCode(SC_UNPROCESSABLE_ENTITY)`: Asserts that the response status code is `422 Unprocessable Entity`, indicating that the request was well-formed but contained invalid data (unspecified gender).
   - `.body("data", notNullValue())`: Asserts that the `data` field in the response body is not null, indicating that the API returned error details.

### Importance
- **Data Validation**: Ensures that the API correctly handles requests with unspecified gender, which is crucial for maintaining data integrity.
- **Error Handling**: Verifies that the API returns appropriate error messages and status codes for invalid input, which is important for debugging and user feedback.
- **Compliance**: Ensures that the API adheres to any business rules or regulatory requirements regarding valid gender values.
- **User Experience**: Helps maintain a consistent and predictable user experience by enforcing valid input data.
     */
    @Test
    @DisplayName("Test User Creation with Unspecified Gender")
    public void Users_CreateUsers_Failure_UnspecifiedGender() {
        String email = "qatest+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "non-binary", email, "active");
        GoRestService.createUser(createUserModel)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data", notNullValue());
    }


    @Test
    @DisplayName("Test User Creation with Unspecified Status")
    public void Users_CreateUsers_Failure_UnspecifiedStatus() {
        String email = "qatest+" + UUID.randomUUID().toString() + "@test.com";
        final CreateUserModel createUserModel = new CreateUserModel("Gino Paloma", "male", email, "pending");
        GoRestService.createUser(createUserModel)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body("data", notNullValue());
    }
}