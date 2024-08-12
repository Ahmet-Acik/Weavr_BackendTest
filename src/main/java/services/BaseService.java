//package services;
//
//import io.restassured.RestAssured;
//import io.restassured.config.EncoderConfig;
//import io.restassured.config.RestAssuredConfig;
//import io.restassured.specification.RequestSpecification;
//import utils.ConfigUtil;
//import static io.restassured.RestAssured.given;
//
//public class BaseService {
//
//    protected static RequestSpecification defaultRequestSpecification(){
//        return restAssured()
//                .header("Content-type", "application/json")
//                .header("Authorization", "Bearer " + ConfigUtil.getProperty("token"));
//    }
//
//    protected static RequestSpecification restAssured() {
//        RestAssured.baseURI = "https://gorest.co.in";
//        RestAssured.useRelaxedHTTPSValidation();
//        RestAssured.urlEncodingEnabled = false;
//
//        return given()
//                .config(RestAssuredConfig.config()
//                        .encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)));
//    }
//}

package services;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import utils.ConfigUtil;
import static io.restassured.RestAssured.given;

public class BaseService {


    /*
    defaultRequestSpecification(): This method builds upon the restAssured() method by adding default headers (like Content-type and Authorization).
    It ensures that every request made using this specification includes these headers, which are often required for API authentication and content type specification.
     */
    protected static RequestSpecification defaultRequestSpecification(){
        return restAssured()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + ConfigUtil.getProperty("token"));
    }

    /*In summary, restAssured() provides the foundational setup, while defaultRequestSpecification() adds specific headers needed for your API requests.
     This separation allows for more modular and reusable code.
     restAssured(): This method sets up the base configuration for RestAssured, such as the base URI, relaxed HTTPS validation, and URL encoding settings.
     It returns a RequestSpecification object that can be further customized.
     */

    protected static RequestSpecification restAssured() {
        RestAssured.baseURI = "https://gorest.co.in";
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.urlEncodingEnabled = false;

        return given()
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)));
    }
}