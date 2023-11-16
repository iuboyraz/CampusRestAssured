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

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class _10_ParameterBank {

    String bankID = "";
    String bankName = "";
    String bankIban = "";
    RequestSpecification requestSpec;
    Faker randomGenerator = new Faker();

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
                        .log().all()
                        .extract().response().getDetailedCookies();

        requestSpec = new RequestSpecBuilder()
                .addCookies(cookies)
                .setContentType(ContentType.JSON)
                .build();
    }
    @Test
    public void createBankAccount() {
        String rndBankName = randomGenerator.name().firstName();
        String rndBankIban = randomGenerator.number().digits(3);

        bankName = rndBankName;
        bankIban = rndBankIban;

        Map<String, Object> newBank = new HashMap<>();
        newBank.put("name", rndBankName);
        newBank.put("iban", rndBankIban);
        newBank.put("integrationCode", "ucg");
        newBank.put("currency", "TRY");
        newBank.put("active", true);
        newBank.put("schoolId", "646cbb07acf2ee0d37c6d984");

        bankID =
                given()
                        .spec(requestSpec)
                        .body(newBank)
                        .when()
                        .post("school-service/api/bank-accounts")
                        .then()
                        .contentType(ContentType.JSON)
                        .statusCode(201)
                        .extract().path("id");
    }

    @Test(dependsOnMethods = "createBankAccount")
    public void createBankAccountNegative() {
        Map<String, Object> newBank = new HashMap<>();
        newBank.put("id", bankID);
        newBank.put("name", bankName);
        newBank.put("iban", bankIban);
        newBank.put("integrationCode", "ucg");
        newBank.put("currency", "TRY");
        newBank.put("active", true);
        newBank.put("schoolId", "646cbb07acf2ee0d37c6d984");

        given()
                .spec(requestSpec)
                .body(newBank)
                .when()
                .post("school-service/api/bank-accounts")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(400);
    }

    @Test(dependsOnMethods = "createBankAccountNegative")
    public void updateBankAccount() {
        Map<String, Object> updateNewBank = new HashMap<>();
        updateNewBank.put("id", bankID);
        updateNewBank.put("name", "team5-1234");
        updateNewBank.put("iban", bankIban);
        updateNewBank.put("integrationCode", "ucg");
        updateNewBank.put("currency", "TRY");
        updateNewBank.put("active", true);
        updateNewBank.put("schoolId", "646cbb07acf2ee0d37c6d984");

        given()
                .spec(requestSpec)
                .body(updateNewBank)
                .when()
                .put("school-service/api/bank-accounts")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("name", equalTo("team5-1234"));
    }

    @Test(dependsOnMethods = "updateBankAccount")
    public void deleteBankAccount() {
        given()
                .spec(requestSpec)
                .when()
                .delete("school-service/api/bank-accounts/" + bankID)
                .then()
                .statusCode(200);
    }
}