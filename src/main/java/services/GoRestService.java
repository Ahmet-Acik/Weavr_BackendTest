package services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.CreateUserModel;
import repo.UserRepository;

import static io.restassured.RestAssured.given;

public class GoRestService extends BaseService {


    private static UserRepository userRepository = new UserRepository();


    public static Response getAllUsers() {
        return defaultRequestSpecification()
                .when()
                .get("/public/v1/users")
                .then()
                .extract()
                .response();
    }


    public static Response createUser(final CreateUserModel createUserModel) {
        return defaultRequestSpecification()
                .body(createUserModel)
                .when()
                .post("/public/v1/users");
    }

    public static Response updateUser(String userId, CreateUserModel updateUserModel) {
        return defaultRequestSpecification()
                .body(updateUserModel)
                .when()
                .put("/public/v1/users/" + userId)
                .then()
                .extract()
                .response();
    }


    public static String createUserAndReturnId(CreateUserModel user) {
        Response response = createUser(user);
        return response.then().extract().path("data.id").toString();
    }


    public static Response createUserWithoutToken(CreateUserModel createUserModel) {
        /* Tests the creation of a user without providing an authorization token.
        This helps verify that the API correctly handles requests that lack proper authentication.

        The defaultRequestSpecification() method is not used in the updateUserWithInvalidToken method
        because this method is designed to test the API's behavior when an invalid token is provided.
        Using defaultRequestSpecification() would automatically include a valid token from the configuration,
        which would defeat the purpose of the test.
        Instead, the method manually sets an invalid token to ensure the API correctly handles requests with invalid tokens.
         */
        return given()
                .contentType(ContentType.JSON)
                .body(createUserModel)
                .when()
                .post("/public/v1/users");
    }

    public static Response createUserWithInvalidToken(CreateUserModel createUserModel) {
        // Tests the creation of a user with an invalid authorization token.
        // This ensures that the API properly rejects requests with invalid tokens.
        //
        // The defaultRequestSpecification() method is not used in the createUserWithInvalidToken method because this method is designed to test the API's behavior when an invalid token is provided.
        // Using defaultRequestSpecification() would automatically include a valid token from the configuration, which would defeat the purpose of the test.
        // Instead, the method manually sets an invalid token to ensure the API correctly handles requests with invalid tokens.
        return given()
                .header("Authorization", "Bearer invalid_token")
                .contentType(ContentType.JSON)
                .body(createUserModel)
                .when()
                .post("/public/v1/users");
    }


    public static Response updateUserWithoutToken(String userId, CreateUserModel updateUserModel) {
        // Tests the update of an existing user without providing an authorization token.
        // This checks if the API enforces authentication for update operations.
        // The defaultRequestSpecification() method is not used in the updateUserWithoutToken method because this method is designed to test the API's behavior when no token is provided.
        return given()
                .contentType(ContentType.JSON)
                .body(updateUserModel)
                .when()
                .put("/public/v1/users/" + userId);
    }


    public static Response updateUserWithInvalidToken(String userId, CreateUserModel updateUserModel) {
        // Tests the update of an existing user with an invalid authorization token.
        // This validates that the API correctly handles updates with invalid tokens.
        // The defaultRequestSpecification() method is not used in the updateUserWithInvalidToken method because this method is designed to test the API's behavior when an invalid token is provided.
        return given()
                .header("Authorization", "Bearer invalid_token")
                .contentType(ContentType.JSON)
                .body(updateUserModel)
                .when()
                .put("/public/v1/users/" + userId);
    }

    public static Response findById(String userId) {
        return given()
                .pathParam("id", userId)
                .when()
                .get("/users/{id}");
    }
}