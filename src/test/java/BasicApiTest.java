import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class BasicApiTest extends BaseTest {

    static String id = "";

    @Test
    public void T01_GetLocation() {
        RestAssured.given()
                // When
                .when()
                .get("https://api.mapbox.com/geocoding/v5/mapbox.places/Los%20Angeles.json?access_token=sk.eyJ1IjoienViYWlyYWxhbTEyMyIsImEiOiJjbDByOGJ6MWkwMHpkM2pxZDE1OXptbnM4In0.ukuCkfJWZWJ9yEMwhMmACw")
                // Then
                .then()
                .statusCode(200);
    }

    @Test
    public void T02_AddLocation(){
        Response res = given()
                .contentType("application/json")
                .body("{\"name\":\"Testing Location Services\", \"description\":\"Testing Location Services\" }")
                .when()
                .post("https://api.mapbox.com/datasets/v1/zubairalam123?access_token=sk.eyJ1IjoienViYWlyYWxhbTEyMyIsImEiOiJjbDByOGJ6MWkwMHpkM2pxZDE1OXptbnM4In0.ukuCkfJWZWJ9yEMwhMmACw")
                ;
        JsonPath body = res.getBody().jsonPath();
        System.out.println("body: "+  res.getBody().asString());
        String name = body.get("name");
        id = body.get("id");
        Assert.assertEquals(name,"Testing Location Services");
    }

    @Test
    // This is actually a bug it should not accept invalid key "name123" in request body
    public void T07_AddLocationWithInvalidBody(){
        Response res = given()
                .contentType("application/json")
                .body("{\"name123\":\"Testing Location Services\", \"description\":\"Testing Location Services\" }")
                .when()
                .post("https://api.mapbox.com/datasets/v1/zubairalam123?access_token=sk.eyJ1IjoienViYWlyYWxhbTEyMyIsImEiOiJjbDByOGJ6MWkwMHpkM2pxZDE1OXptbnM4In0.ukuCkfJWZWJ9yEMwhMmACw")
                ;
        JsonPath body = res.getBody().jsonPath();
        System.out.println("body: "+  res.getBody().asString());
        String name = body.get("name");
        Assert.assertEquals(name,null);
    }


    @Test
    public void T03_UpdateLocation(){
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Testing Location Services\", \"description\":\"Testing Location Services Updated\" }")
                // WHEN
                .when()
                .patch("https://api.mapbox.com/datasets/v1/zubairalam123/"+id+"?access_token=sk.eyJ1IjoienViYWlyYWxhbTEyMyIsImEiOiJjbDByOGJ6MWkwMHpkM2pxZDE1OXptbnM4In0.ukuCkfJWZWJ9yEMwhMmACw")
                // THEN
                .then()
                .assertThat()
                .statusCode(200)
                .body("description", Matchers.equalTo("Testing Location Services Updated"));
    }

    @Test
    public void T04_RemoveLocation(){
        RestAssured
                .given()
                .contentType("application/json")
                // WHEN
                .when()
                .delete("https://api.mapbox.com/datasets/v1/zubairalam123/"+id+"?access_token=sk.eyJ1IjoienViYWlyYWxhbTEyMyIsImEiOiJjbDByOGJ6MWkwMHpkM2pxZDE1OXptbnM4In0.ukuCkfJWZWJ9yEMwhMmACw")
                // THEN
                .then()
                .assertThat()
                .statusCode(204);
    }

    @Test
    public void T05_Validate401() {

        RestAssured.given()
                // When
                .when()
                .get("https://api.mapbox.com/datasets/v1/zubairalam123/Los%20Angeles.json?access_token=")
                // Then
                .then()
                .statusCode(401);
    }

    @Test
    public void T06_Validate404() {
        RestAssured.given()
                // When
                .when()
                .get("https://api.mapbox.com/geocoding/v1/mapbox.places/Los%20Angeles.json?access_token=sk.eyJ1IjoienViYWlyYWxhbTEyMyIsImEiOiJjbDByOGJ6MWkwMHpkM2pxZDE1OXptbnM4In0.ukuCkfJWZWJ9yEMwhMmACw")
                // Then
                .then()
                .statusCode(404);
    }
}
