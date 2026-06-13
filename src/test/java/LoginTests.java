import client.EVPrimeClient;
import data.SignUpLoginDataFactory;
import io.restassured.response.Response;
import models.request.SignUpLoginRequest;
import objectBuilder.SignUpLoginObjectBuilder;
import org.junit.jupiter.api.Test;
import signupAndLoginResponse.LoginErrorsResponse;
import signupAndLoginResponse.LoginResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LoginTests {

  private EVPrimeClient client = new EVPrimeClient();

    @Test
    public void loginWithNonExistingEmailTest() {
        SignUpLoginRequest requestBody = new SignUpLoginDataFactory(SignUpLoginObjectBuilder.createBodyForSignUpLogin())
                .setEmail("nonexistinguser@mail.com")
                .setPassword("test12345")
                .createRequest();

        Response response = client.login(requestBody);

        LoginErrorsResponse responseBody = response.body().as(LoginErrorsResponse.class);

        assertEquals(401, response.statusCode());
        assertEquals("Authentication failed.", responseBody.getMessage());
    }

    @Test
    public void loginWithWrongPasswordTest() {
        SignUpLoginRequest requestBody = new SignUpLoginDataFactory(SignUpLoginObjectBuilder.createBodyForSignUpLogin())
                .setEmail("test98786@hotmail.com")
                .setPassword("wrongpassword")
                .createRequest();

        Response response = client.login(requestBody);

        LoginErrorsResponse responseBody = response.body().as(LoginErrorsResponse.class);

        assertEquals(422, response.statusCode());
        assertEquals("Invalid credentials.", responseBody.getMessage());
        assertEquals("Invalid email or password entered.", responseBody.getErrors().getCredentials());
    }

    @Test
    public void sucessfullLoginTest() {
        SignUpLoginRequest requestBody = new SignUpLoginDataFactory(SignUpLoginObjectBuilder.createBodyForSignUpLogin())
                .createRequest();

        Response response = client.login(requestBody);

        LoginResponse responseBody = response.body().as(LoginResponse.class);

        assertEquals(200, response.statusCode());
        assertNotNull(responseBody.getToken());
        assertNotNull(responseBody.getExpirationTime());
    }
}
