package Campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.*;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.*;

import java.util.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class _02_PositionManagement {
    Faker randomGenerator = new Faker();
    String positionCategoryId;
    RequestSpecification requestSpec;
    String rndPositionCategoryName;
    Map<String, String> newPositionCategory;
    @BeforeClass
    public void setUp() {
        baseURI = "https://test.mersys.io/";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        Cookies cookies =
                given()
                        .body(userCredential)
                        .contentType(ContentType.JSON)

                        .when()
                        .post("/auth/login")

                        .then()
                        .statusCode(200)
                        .extract().response().detailedCookies()
                ;

        requestSpec = new RequestSpecBuilder()
                .addCookies(cookies)
                .setContentType(ContentType.JSON)
                .build()
        ;
    }
    @Test
    public void createPositionCategory(){

        rndPositionCategoryName = randomGenerator.job().field() + randomGenerator.number().digits(5);

        newPositionCategory = new HashMap<>();
        newPositionCategory.put("name", rndPositionCategoryName);

        positionCategoryId =
                given()

                        .spec(requestSpec)
                        .body(newPositionCategory)

                        .when()
                        .post("school-service/api/position-category/")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
        System.out.println("positionCategoryId = " + positionCategoryId);
    }
    @Test (dependsOnMethods = "createPositionCategory")
    public void createPositionCategoryNegative(){

        given()

                .spec(requestSpec)
                .body(newPositionCategory)

                .when()
                .post("school-service/api/position-category/")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already exists."))

        ;
        System.out.println("positionCategoryId = " + positionCategoryId);
    }
    @Test (dependsOnMethods = "createPositionCategoryNegative")
    public void updatePositionCategory(){
        String newPositionCategoryName = randomGenerator.job().field() + randomGenerator.number().digits(5);

        Map<String, String> updatePositionCategory = new HashMap<>();
        updatePositionCategory.put("id", positionCategoryId);
        updatePositionCategory.put("name", newPositionCategoryName);

        given()

                .spec(requestSpec)
                .body(updatePositionCategory)

                .when()
                .put("school-service/api/position-category/")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(newPositionCategoryName))

        ;
        System.out.println("positionCategoryId = " + positionCategoryId);
        System.out.println("newPositionCategoryName = " + newPositionCategoryName);
    }
    @Test (dependsOnMethods = "updatePositionCategory")
    public void deletePositionCategory(){

        given()
                .spec(requestSpec)

                .when()
                .delete("school-service/api/position-category/" + positionCategoryId)

                .then()
                .log().body()
                .statusCode(204)

        ;
        System.out.println("positionCategoryId = " + positionCategoryId);
    }
}
