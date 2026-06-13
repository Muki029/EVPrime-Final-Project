import client.EVPrimeClient;
import data.SignUpLoginDataFactory;
import io.restassured.response.Response;
import models.request.SignUpLoginRequest;
import objectBuilder.SignUpLoginObjectBuilder;
import org.junit.jupiter.api.Test;
import signupAndLoginResponse.SignUpResponse;
import signupAndLoginResponse.SignupErrorsResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SignUpTests {

    EVPrimeClient client = new EVPrimeClient();

    @Test
    public void signupWithExistingEmailTest() {
        SignUpLoginRequest requestBody = new SignUpLoginDataFactory(SignUpLoginObjectBuilder.createBodyForSignUpLogin())
                .setEmail("test98786@hotmail.com")
                .setPassword("test12345")
                .createRequest();

        Response response = client.signUp(requestBody);

        assertEquals(422, response.statusCode());

        SignupErrorsResponse responseBody = response.body().as(SignupErrorsResponse.class);

        assertEquals("User signup failed due to validation errors.", responseBody.getMessage());
        assertEquals("Email exists already.", responseBody.getErrors().getEmail());
    }

    @Test
    public void signupWithInvalidEmailFormatTest() {
        SignUpLoginRequest requestBody = new SignUpLoginDataFactory(SignUpLoginObjectBuilder.createBodyForSignUpLogin())
                .setEmail("invalid_email.com")
                .setPassword("test12345")
                .createRequest();

        Response response = client.signUp(requestBody);

        assertEquals(422, response.statusCode());

        SignupErrorsResponse responseBody = response.body().as(SignupErrorsResponse.class);

        assertEquals("User signup failed due to validation errors.", responseBody.getMessage());
        assertEquals("Invalid email.", responseBody.getErrors().getEmail());
    }

    @Test
    public void successfulSignupTest() {

        SignUpLoginRequest requestBody = new SignUpLoginDataFactory(SignUpLoginObjectBuilder.createBodyForSignUpLogin())
                .setEmail("muki_newemail@mail.com")
                .setPassword("test12345")
                .createRequest();

        Response response = client.signUp(requestBody);

        assertEquals(201, response.statusCode());

        SignUpResponse responseBody = response.body().as(SignUpResponse.class);

        assertEquals("User created.", responseBody.getMessage());
        assertNotNull(responseBody.getToken());
        assertNotNull(responseBody.getUser().getId());
        assertEquals("muki_newemail@mail.com", responseBody.getUser().getEmail());
    }

}
