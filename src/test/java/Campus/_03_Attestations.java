package Campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.*;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.*;

import java.util.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class _03_Attestations {
    Faker randomGenerator = new Faker();
    String attestationId;
    RequestSpecification requestSpec;
    String rndAttestationName;
    Map<String, String> newAttestation;
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
    public void createAttestation(){

        rndAttestationName = randomGenerator.name().fullName() + randomGenerator.number().digits(5);

        newAttestation = new HashMap<>();
        newAttestation.put("name", rndAttestationName);

        attestationId =
                given()

                        .spec(requestSpec)
                        .body(newAttestation)

                        .when()
                        .post("school-service/api/attestation/")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
        System.out.println("attestationId = " + attestationId);
    }
    @Test (dependsOnMethods = "createAttestation")
    public void createAttestationNegative(){

        given()

                .spec(requestSpec)
                .body(newAttestation)

                .when()
                .put("school-service/api/attestation/")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("Please, provide valid data to update 'Attestation', your 'Attestation' is not created to update"))

        ;
        System.out.println("attestationId = " + attestationId);
    }
    @Test (dependsOnMethods = "createAttestationNegative")
    public void updateAttestation(){
        String newAttestationName = randomGenerator.job().field() + randomGenerator.number().digits(5);

        Map<String, String> updateAttestation = new HashMap<>();
        updateAttestation.put("id", attestationId);
        updateAttestation.put("name", newAttestationName);

        given()

                .spec(requestSpec)
                .body(updateAttestation)

                .when()
                .put("school-service/api/attestation/")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(newAttestationName))

        ;
        System.out.println("positionCategoryId = " + attestationId);
        System.out.println("newAttestationName = " + newAttestationName);
    }
    @Test (dependsOnMethods = "updateAttestation")
    public void deleteAttestation(){

        given()
                .spec(requestSpec)

                .when()
                .delete("school-service/api/attestation/" + attestationId)

                .then()
                .log().body()
                .statusCode(204)

        ;
        System.out.println("attestationId = " + attestationId);
    }
    @Test (dependsOnMethods = "deleteAttestation")
    public void deleteAttestationNegative(){

        given()
                .spec(requestSpec)

                .when()
                .delete("school-service/api/attestation/" + attestationId)

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("attestation not found"))

        ;
        System.out.println("attestationId = " + attestationId);
    }
}
