package Campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class _07_SubjectCategories {
    Faker randomGenerator = new Faker();
    String SubjectCategoryID ="";
    String SubjectCategoryName = randomGenerator.name().fullName();
    String SubjectCategoryCode = randomGenerator.number().digits(5);
    Map<String, String> SubjectCategoryGroup;
    RequestSpecification requestSpecification;

    @BeforeClass
    public void login() {
        baseURI = "https://test.mersys.io/school-service/api/subject-categories";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        Cookies cookies =
                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)
                        .when()
                        .post("https://test.mersys.io/auth/login")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();
        requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();


    }

    @Test
    public void createEducation() {
        SubjectCategoryGroup = new HashMap<>();
        SubjectCategoryGroup.put("name", SubjectCategoryName);
        SubjectCategoryGroup.put("code", SubjectCategoryCode);
        SubjectCategoryGroup.put("translateName",null);


        SubjectCategoryID =
                given()
                        .spec(requestSpecification)
                        .body(SubjectCategoryGroup)
                        .log().body()
                        .when()
                        .post("")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
        System.out.println("SubjectCategoryID=" + SubjectCategoryID);

    }

    @Test(dependsOnMethods = "createEducation")
    public void createEducationNegative() {

        given()
                .spec(requestSpecification)
                .body(SubjectCategoryGroup)
                .log().body()
                .when()
                .post("")
                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))


        ;


    }

    @Test(dependsOnMethods = "createEducationNegative")
    public void aditEducation() {

        SubjectCategoryName = "user" + this.randomGenerator.number().digits(2);
        SubjectCategoryGroup.put("id", SubjectCategoryID);
        SubjectCategoryGroup.put("name", SubjectCategoryName);
        SubjectCategoryGroup.put("code", SubjectCategoryCode);
        given()
                .spec(requestSpecification)
                .body(SubjectCategoryGroup)
                .log().body()
                .when()
                .put("")
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(SubjectCategoryName))

        ;

    }

    @Test(dependsOnMethods = "aditEducation")
    public void deleteEducation() {


        given()
                .spec(requestSpecification)
                .body(SubjectCategoryGroup)
                .log().body()
                .when()
                .delete(SubjectCategoryID)
                .then()
                .log().body()
                .statusCode(200)

        ;
    }
}
