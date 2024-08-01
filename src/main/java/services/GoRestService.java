package services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.CreateUserModel;
import repo.UserRepository;

import static io.restassured.RestAssured.given;

/**
 * The GoRestService class provides methods to interact with the GoRest API.
 * It includes methods to get all users, create a user, update a user, and handle
 * various scenarios such as creating or updating a user without a token or with an invalid token.
 */
public class GoRestService extends BaseService {

    /**
     * A repository for managing user data.
     */
    private static UserRepository userRepository = new UserRepository();

    /**
     * Retrieves all users from the GoRest API.
     *
     * @return a Response object containing the API response
     */
    public static Response getAllUsers() {
        return defaultRequestSpecification()
                .when()
                .get("/public/v1/users")
                .then()
                .extract()
                .response();
    }

    /**
     * Creates a new user using the GoRest API.
     *
     * @param createUserModel the model containing user data to be created
     * @return a Response object containing the API response
     */
    public static Response createUser(final CreateUserModel createUserModel){
        return defaultRequestSpecification()
                .body(createUserModel)
                .when()
                .post("/public/v1/users");
    }

    /**
     * Updates an existing user using the GoRest API.
     *
     * @param userId the unique identifier of the user to be updated
     * @param updateUserModel the model containing updated user data
     * @return a Response object containing the API response
     */
    public static Response updateUser(String userId, CreateUserModel updateUserModel) {
        return defaultRequestSpecification()
                .body(updateUserModel)
                .when()
                .put("/public/v1/users/" + userId)
                .then()
                .extract()
                .response();
    }

    public static Response createUserWithoutToken(CreateUserModel createUserModel) {
        // Tests the creation of a user without providing an authorization token.
        // This helps verify that the API correctly handles requests that lack proper authentication.
        return given()
                .contentType(ContentType.JSON)
                .body(createUserModel)
                .when()
                .post("/public/v1/users");
    }

    /**
     * Creates a new user with an invalid authorization token using the GoRest API.
     *
     * @param createUserModel the model containing user data to be created
     * @return a Response object containing the API response
     */
    public static Response createUserWithInvalidToken(CreateUserModel createUserModel) {
        // Tests the creation of a user with an invalid authorization token.
        // This ensures that the API properly rejects requests with invalid tokens.
        return given()
                .header("Authorization", "Bearer invalid_token")
                .contentType(ContentType.JSON)
                .body(createUserModel)
                .when()
                .post("/public/v1/users");
    }

    /**
     * Updates an existing user without an authorization token using the GoRest API.
     *
     * @param userId the unique identifier of the user to be updated
     * @param updateUserModel the model containing updated user data
     * @return a Response object containing the API response
     */
    public static Response updateUserWithoutToken(String userId, CreateUserModel updateUserModel) {
        // Tests the update of an existing user without providing an authorization token.
        // This checks if the API enforces authentication for update operations.
        return given()
                .contentType(ContentType.JSON)
                .body(updateUserModel)
                .when()
                .put("/public/v1/users/" + userId);
    }

    /**
     * Updates an existing user with an invalid authorization token using the GoRest API.
     *
     * @param userId the unique identifier of the user to be updated
     * @param updateUserModel the model containing updated user data
     * @return a Response object containing the API response
     */
    public static Response updateUserWithInvalidToken(String userId, CreateUserModel updateUserModel) {
        // Tests the update of an existing user with an invalid authorization token.
        // This validates that the API correctly handles updates with invalid tokens.
        return given()
                .header("Authorization", "Bearer invalid_token")
                .contentType(ContentType.JSON)
                .body(updateUserModel)
                .when()
                .put("/public/v1/users/" + userId);
    }
    /**
     * Finds a user by their unique identifier using the GoRest API.
     *
     * @param userId the unique identifier of the user to be found
     * @return a Response object containing the API response
     */
    public static Response findById(String userId) {
        return given()
                .pathParam("id", userId)
                .when()
                .get("/users/{id}");
    }
}