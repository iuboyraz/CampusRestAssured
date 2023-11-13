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

public class _08_SchoolSetup {
    Faker faker = new Faker();
    RequestSpecification requestSpecification;
    String locID="";
    String locName="";
    String locCode="";
    String schoolID="646cbb07acf2ee0d37c6d984";
    Map<String,Object> newLoc;
    @BeforeClass
    public void setup(){

        baseURI = "https://test.mersys.io";
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", "turkeyts");
        userInfo.put("password", "TechnoStudy123");
        userInfo.put("rememberMe", "true");

        Cookies cookies =
                given()
                        .body(userInfo)
                        .contentType(ContentType.JSON)
                        .when()
                        .post("/auth/login")

                        .then()
                        .contentType(ContentType.JSON)
                        .extract().response().getDetailedCookies();

        requestSpecification=new RequestSpecBuilder()
                .addCookies(cookies)
                .setContentType(ContentType.JSON)
                .build()
        ;
    }

    @Test
    public void addNewLocation(){
        newLoc = new HashMap<>();

        locName=faker.name().firstName();
        locCode=faker.name().title();

        newLoc.put("name",locName);
        newLoc.put("shortName",locCode);
        newLoc.put("capacity",10);
        newLoc.put("type","CLASS");
        newLoc.put("school",schoolID);
        newLoc.put("active",true);

        locID=
                given()
                        .spec(requestSpecification)
                        .body(newLoc)
                        .log().body()
                        .when()
                        .post("/school-service/api/location")

                        .then()
                        .log().body()
                        .contentType(ContentType.JSON)
                        .statusCode(201)
                        .extract().path("id")

        ;

    }
    @Test(dependsOnMethods = "addNewLocation")
    public void addLocationNegativeTest(){


        given()
                .spec(requestSpecification)
                .body(newLoc)
                .log().body()
                .when()
                .post("/school-service/api/location")

                .then()
                .log().body()
                .contentType(ContentType.JSON)
                .body("message",containsString("already"))
                .statusCode(400);




    }
    @Test(dependsOnMethods = "addNewLocation")
    public void editLocation(){

        newLoc = new HashMap<>();

        locName=faker.name().firstName();
        locCode=faker.name().title();

        newLoc.put("id",locID);
        newLoc.put("name",locName);
        newLoc.put("shortName",locCode);
        newLoc.put("capacity",10);
        newLoc.put("type","CLASS");
        newLoc.put("school",schoolID);
        newLoc.put("active",true);

        given()
                .spec(requestSpecification)
                .body(newLoc)
                .log().body()
                .when()
                .put("/school-service/api/location")

                .then()
                .log().body()
                .contentType(ContentType.JSON)
                .body("name",equalTo(locName))
                .statusCode(200);

    }
    @Test(dependsOnMethods = "editLocation")
    public void deletePositiveTest(){

        given()
                .spec(requestSpecification)
                .log().body()

                .delete("school-service/api/location/"+locID)
                .then()
                .log().body()
                .statusCode(200);




    }

    @Test(dependsOnMethods = "deletePositiveTest")
    public void deleteNegativeTest(){

        given()
                .spec(requestSpecification)
                .log().body()

                .delete("school-service/api/location/"+locID)
                .then()
                .statusCode(400);

    }


}
