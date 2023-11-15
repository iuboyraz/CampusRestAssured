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

public class _06_HRPosition {
    Faker randomGenerator = new Faker();
    String positionsID = "";
    String tenantId = "646cb816433c0f46e7d44cb0";


    String PositionsName = randomGenerator.name().fullName();
    String PositionsShortName = randomGenerator.number().digits(2);


    Map<String, String> HumanPositionsGroup;
    RequestSpecification requestSpecification;


    @BeforeClass
    public void login() {
        baseURI = "https://test.mersys.io/school-service/api/employee-position";

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
                        .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();
        requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();


    }

    @Test
    public void createHumanPositions() {

        HumanPositionsGroup = new HashMap<>();
        HumanPositionsGroup.put("name", PositionsName);
        HumanPositionsGroup.put("shortName", PositionsShortName);
        HumanPositionsGroup.put("tenantId", tenantId);

        positionsID =
                given()

                        .spec(requestSpecification)
                        .body(HumanPositionsGroup)
                        .log().body()
                        .when()
                        .post("")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
        System.out.println("positionsID=" + positionsID);

    }

    @Test(dependsOnMethods = "createHumanPositions")
    public void createHumanPositionNegative() {

        given()
                .spec(requestSpecification)
                .body(HumanPositionsGroup)
                .log().body()
                .when()
                .post("")
                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString(PositionsName))


        ;


    }

    @Test(dependsOnMethods = "createHumanPositionNegative")
    public void updateHumanPositions() {


        HumanPositionsGroup.put("id", positionsID);
        HumanPositionsGroup.put("name", PositionsName);
        HumanPositionsGroup.put("shortName", PositionsShortName);
        given()
                .spec(requestSpecification)
                .body(HumanPositionsGroup)
                .log().body()
                .when()
                .put("")
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(PositionsName))
        ;
        System.out.println(positionsID);

    }

    @Test(dependsOnMethods = "updateHumanPositions")
    public void deleteHumanPositions() {

        given()
                .spec(requestSpecification)
                .body(HumanPositionsGroup)
                .log().body()
                .when()
                .delete(positionsID)
                .then()
                .log().body()
                .statusCode(204)
        ;
    }
}
