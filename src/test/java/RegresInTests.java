import org.junit.jupiter.api.Test;
import lombok.User;
import lombok.UserData;
import lombok.Data;
import lombok.Builder;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegresInTests {

    @Test
    void userNotFound() {
        Specs.request
                .when()
                .get("/users/23")
                .then()
                .statusCode(404);
    }

    @Test
    void createUser() {
        Specs.request
                .body("{ \"name\": \"morpheus\", \"job\": \"leader\" }")
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("name", is("morpheus"));
    }

    @Test
    void updateUserInfo() {
        Specs.request
                .body("{ \"name\": \"morpheus\", \"job\": \"zion resident\" }")
                .when()
                .put("/users/2")
                .then()
                .spec(Specs.responseSpec)
                .log().body()
                .body("job", is("zion resident"));


    }

    @Test
    void deleteUser() {
        Specs.request
                .when()
                .delete("/users/2")
                .then()
                .statusCode(204);
    }

    @Test
    void loginWithNoPassword() {
        Specs.request
                .body("{ \"email\": \"peter@klaven\" }")
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("error", is("Missing password"));
    }

    @Test
    void singleUserWithLombokModel() {
        UserData data = Specs.request
                .when()
                .get("/users/2")
                .then()
                .spec(Specs.responseSpec)
                .log().body()
                .extract().as(lombok.UserData.class);

        User user = new User();
        user.setId(2);

        assertEquals(2, data.getUser().getId());
    }

    @Test
    void listUsersTestGroovy() {
        Specs.request
                .when()
                .get("/users?page=2")
                .then()
                .log().body()
                .spec(Specs.responseSpec)
                .body("data.findAll{it.last_name = 'Edwards'}.email.flatten()",
                        hasItem("george.edwards@reqres.in"));
    }


}
