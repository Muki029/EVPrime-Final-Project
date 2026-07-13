import client.EVPrimeClient;
import data.PostEventDataFactory;
import database.DBClient;
import io.restassured.response.Response;
import models.request.PostUpdateEventRequest;
import models.response.ErrorsResponse;
import models.response.LoginResponse;
import models.response.PostUpdateDeleteEventResponse;
import objectBuilder.PostEventObjectBuilder;
import objectBuilder.SignUpLoginObjectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static objectBuilder.PostEventObjectBuilder.createBodyForPostEvent;
import static objectBuilder.SignUpLoginObjectBuilder.createBodyForSignUpLogin;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostEventTests {

    private LoginResponse loginResponseBody;
    private PostUpdateEventRequest postEventRequest;
    private static String id;
    private DBClient dbClient = new DBClient();

    @BeforeEach
    public void setUp() {

        //sign up only in this page and in others we gonna use only Login
        new EVPrimeClient()
                .signUp(createBodyForSignUpLogin());

        //login
        Response loginResponse = new EVPrimeClient()
                .login(SignUpLoginObjectBuilder.createBodyForSignUpLogin());

        loginResponseBody = loginResponse.body().as(LoginResponse.class);
    }

    @Test
    public void successfulPostEventCustomValuesTest() throws SQLException {
        postEventRequest = new PostEventDataFactory(createBodyForPostEvent())
                .setTitle("Liverpool - Manchester United football match")
                .setImage("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.goal.com%2Fen-sq%2Fnews%2Fliverpool-vs-manchester-united-lineups-live-updates%2Fbltf4a9e3c")
                .setDate("2025-11-20")
                .setLocation("Anfield")
                .setDescription("The match between the biggest rivals.")
                .createRequest();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());

        System.out.println("token -> " + loginResponseBody.getToken());

        PostUpdateDeleteEventResponse postResponse = response.body().as(PostUpdateDeleteEventResponse.class);

        id = postResponse.getMessage().substring(39);


        assertEquals(201, response.statusCode());
        assertTrue(postResponse.getMessage().contains("Successfully created an event with id: " + id));

        assertEquals(postEventRequest.getTitle(), dbClient.getEventFromDB(id).getTitle());
        assertEquals(postEventRequest.getImage(), dbClient.getEventFromDB(id).getImage());
        assertEquals(postEventRequest.getDate(), dbClient.getEventFromDB(id).getDate());
        assertEquals(postEventRequest.getLocation(), dbClient.getEventFromDB(id).getLocation());
        assertEquals(postEventRequest.getDescription(), dbClient.getEventFromDB(id).getDescription());


    }

    @Test
    public void sucessfullPostEventHealthCheckTest() throws SQLException {
        postEventRequest = new PostEventDataFactory(createBodyForPostEvent())
                .createRequest();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());

        PostUpdateDeleteEventResponse postResponse = response.body().as(PostUpdateDeleteEventResponse.class);

        id = postResponse.getMessage().substring(39);

        assertEquals(201, response.statusCode());
        assertTrue(postResponse.getMessage().contains("Successfully created an event with id: " + id));


        assertEquals(postEventRequest.getTitle(), dbClient.getEventFromDB(id).getTitle());
        assertEquals(postEventRequest.getImage(), dbClient.getEventFromDB(id).getImage());
        assertEquals(postEventRequest.getDate(), dbClient.getEventFromDB(id).getDate());
        assertEquals(postEventRequest.getLocation(), dbClient.getEventFromDB(id).getLocation());
        assertEquals(postEventRequest.getDescription(), dbClient.getEventFromDB(id).getDescription());

    }

    @Test
    public void unsucessfullPostEventInvalidImageTest() {
        postEventRequest = new PostEventDataFactory(PostEventObjectBuilder.createBodyForPostEvent())
                .setImage("not-a-valid-url")
                .createRequest();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());

        assertEquals(422, response.statusCode());

        ErrorsResponse errorsResponse = response.body().as(ErrorsResponse.class);
        assertEquals("Adding the event failed due to validation errors.", errorsResponse.getMessage());
        assertEquals("Invalid image.", errorsResponse.getErrors().getImage());
    }

    @Test
    public void postEventEmptyTitleTest() {
        postEventRequest = new PostEventDataFactory(PostEventObjectBuilder.createBodyForPostEvent())
                .setTitle("")
                .createRequest();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());

        assertEquals(422, response.statusCode());

        ErrorsResponse errorsResponse = response.body().as(ErrorsResponse.class);
        assertEquals("Invalid title.", errorsResponse.getErrors().getTitle());
        assertEquals("Adding the event failed due to validation errors.", errorsResponse.getMessage());
    }

    @Test
    public void postEventEmptyLocationTest() {
        postEventRequest = new PostEventDataFactory(PostEventObjectBuilder.createBodyForPostEvent())
                .setLocation("")
                .createRequest();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());

        assertEquals(422, response.statusCode());

        ErrorsResponse errorsResponse = response.body().as(ErrorsResponse.class);
        assertEquals("Invalid location.", errorsResponse.getErrors().getDescription());
        assertEquals("Adding the event failed due to validation errors.", errorsResponse.getMessage());
    }

    @Test
    public void postEventMissingTokenTest() {
        postEventRequest = PostEventObjectBuilder.createBodyForPostEvent();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, "");

        assertEquals(401, response.statusCode());

        ErrorsResponse errorsResponse = response.body().as(ErrorsResponse.class);
        assertEquals("Not authenticated.", errorsResponse.getMessage());
    }


    @AfterEach
    public void cleanUp() throws SQLException {
        boolean isDeleted = dbClient.isEventDeleted(id);
        assertTrue(isDeleted);
    }
}