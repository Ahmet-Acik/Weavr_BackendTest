### QA Engineer test

### This is a sample project to test APIs using JUnit and RestAssured. In this project you will find the configuration required to write tests for a number of endpoints by Go Rest (https://gorest.co.in/), as well as a sample test class to start with.

##### Requirements:

- Using the already implemented create user endpoint, write a number of tests to cover the functionality of this endpoint
- Implement whatever is required to write tests for the update user endpoint

###### The information required to perform those tasks can be found here -> https://gorest.co.in/. Feel free to update the project in any way you see fit.

## Project Overview
This project is designed to test the GoRest API's user creation and update functionalities. It includes various test cases to ensure the API handles different scenarios correctly, such as creating users with valid and invalid data, and updating users with and without authorization tokens.

## Technologies Used
- Java
- Maven
- JUnit 5
- RestAssured

## Main Dependencies
- **Java**: `11`
- **JUnit 5**: `5.8.1`
- **RestAssured**: `4.4.0`
- **Maven**: `3.8.1`

## Setup Instructions
1. **Extract the ZIP file**:
    ```sh
    unzip <zip-file-name>.zip
    cd <extracted-directory>
    ```

2. **Install dependencies**:
    ```sh
    mvn clean install
    ```

3. **Run the tests**:
    ```sh
    mvn test
    ```

## Running Tests
The tests are located in the `src/test/java` directory. You can run all tests using Maven with the following command:
```sh
mvn test
```

## Parallel Test Execution

### Configuration

1. **Maven Surefire Plugin**:
   ```xml
   <build>
       <plugins>
           <plugin>
               <groupId>org.apache.maven.plugins</groupId>
               <artifactId>maven-surefire-plugin</artifactId>
               <version>3.0.0-M5</version>
               <configuration>
                   <parallel>methods</parallel>
                   <threadCount>4</threadCount>
               </configuration>
           </plugin>
       </plugins>
   </build>
   ```

2. **JUnit Platform Properties**:
   ```properties
   junit.jupiter.execution.parallel.enabled=true
   junit.jupiter.execution.parallel.mode.default=concurrent
   junit.jupiter.execution.parallel.mode.classes.default=concurrent
   ```

3. **Thread Logging Extension**:
   ```java
   import org.junit.jupiter.api.extension.BeforeEachCallback;
   import org.junit.jupiter.api.extension.ExtensionContext;

   public class ThreadLoggingExtension implements BeforeEachCallback {
       @Override
       public void beforeEach(ExtensionContext context) {
           System.out.println("Running test on thread: " + Thread.currentThread().getName());
       }
   }
   ```

4. **Inject Extension**:
   ```java
   import org.junit.jupiter.api.extension.ExtendWith;

   @ExtendWith(ThreadLoggingExtension.class)
   public class CreateUserTests {
       // Test methods
   }
   ```

### Benefits

- **Improved Test Execution Speed**: Parallel execution significantly reduces the time required to run tests.
- **Thread Logging**: The `ThreadLoggingExtension` provides insights into the thread usage during test execution.
```

```
## Test Coverage
The tests cover the following key scenarios, among others:
- **Successful User Creation**: Verifies that a user can be created with valid data.
- **User Creation with Invalid Data**: Ensures that the API returns appropriate errors for invalid user data.
- **User Creation with Invalid Email**: Checks that the API handles invalid email formats correctly.
- **User Creation with Duplicate Email**: Tests the API's response to creating a user with an email that already exists.
- **User Creation without Authorization Token**: Verifies that the API rejects user creation requests without an authorization token.
- **User Creation with Invalid Authorization Token**: Ensures that the API rejects user creation requests with an invalid token.
- **User Update without Authorization Token**: Checks if the API enforces authentication for update operations.
- **User Update with Invalid Authorization Token**: Validates that the API correctly handles updates with invalid tokens.
- **Successful User Update**: Verifies that an existing user can be updated with valid data.
- **User Update with Invalid Data**: Ensures that the API returns appropriate errors for invalid update data.
- **User Update with Invalid Email**: Checks that the API handles invalid email formats correctly during updates.
- **User Update with Duplicate Email**: Tests the API's response to updating a user with an email that already exists.

## API Endpoints
The project tests the following API endpoints:

1. **Successful User Creation**:
   - **Method**: `POST`
   - **Endpoint**: `/public/v1/users`
   - **Description**: Verifies that a user can be created with valid data.

2. **Successful User Update**:
   - **Method**: `PUT`
   - **Endpoint**: `/public/v1/users/{userId}`
   - **Description**: Verifies that an existing user can be updated with valid data.

3. **Create User Without Token**:
   - **Method**: `POST`
   - **Endpoint**: `/public/v1/users`
   - **Description**: Tests the creation of a user without providing an authorization token. This helps verify that the API correctly handles requests that lack proper authentication.

4. **Create User With Invalid Token**:
   - **Method**: `POST`
   - **Endpoint**: `/public/v1/users`
   - **Description**: Tests the creation of a user with an invalid authorization token. This ensures that the API properly rejects requests with invalid tokens.

5. **Update User Without Token**:
   - **Method**: `PUT`
   - **Endpoint**: `/public/v1/users/{userId}`
   - **Description**: Tests the update of an existing user without providing an authorization token. This checks if the API enforces authentication for update operations.

6. **Update User With Invalid Token**:
   - **Method**: `PUT`
   - **Endpoint**: `/public/v1/users/{userId}`
   - **Description**: Tests the update of an existing user with an invalid authorization token. This validates that the API correctly handles updates with invalid tokens.