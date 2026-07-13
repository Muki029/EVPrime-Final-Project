import client.EVPrimeClient;
import data.PostEventDataFactory;
import database.DBClient;
import io.restassured.response.Response;
import models.request.PostUpdateEventRequest;
import models.response.Errors;
import models.response.ErrorsResponse;
import models.response.LoginResponse;
import models.response.PostUpdateDeleteEventResponse;
import objectBuilder.PostEventObjectBuilder;
import objectBuilder.SignUpLoginObjectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PutEventsTests {

    private LoginResponse loginResponseBody;
    private PostUpdateEventRequest postEventRequest;
    private static String id;
    private DBClient dbClient = new DBClient();


    @BeforeEach
    public void setUp() {

        Response loginResponse = new EVPrimeClient()
                .login(SignUpLoginObjectBuilder.createBodyForSignUpLogin());

        loginResponseBody = loginResponse.body().as(LoginResponse.class);

        postEventRequest = new PostEventDataFactory(PostEventObjectBuilder.createBodyForPostEvent())
                .createRequest();

        Response response = new EVPrimeClient()
                .postEvent(postEventRequest, loginResponseBody.getToken());


        PostUpdateDeleteEventResponse postResponse = response.body().as(PostUpdateDeleteEventResponse.class);

        id = postResponse.getMessage().substring(39);


    }

    @Test
    public void SucessfullUpdateCustomValues() throws SQLException {
        //update event (update event with the taken id from before method
        postEventRequest = new PostEventDataFactory(PostEventObjectBuilder.createBodyForPostEvent())
                .setImage("https://updated image")
                .setTitle("updated title")
                .setLocation("updated location")
                .setDate("2025-12-07")
                .setDescription("updated description")
                .setImage("https://updated image")
                .createRequest();

        Response response = new EVPrimeClient()
                .updateEvent(postEventRequest, id, loginResponseBody.getToken());

        PostUpdateDeleteEventResponse responseBody = response.body().as(PostUpdateDeleteEventResponse.class);

        assertEquals(201, response.statusCode());
        assertEquals("Successfully updated the event with id: " + id, responseBody.getMessage());
        assertEquals(postEventRequest.getTitle(), dbClient.getEventFromDB(id).getTitle());
        assertEquals(postEventRequest.getImage(), dbClient.getEventFromDB(id).getImage());
        assertEquals(postEventRequest.getLocation(), dbClient.getEventFromDB(id).getLocation());
        assertEquals(postEventRequest.getDate(), dbClient.getEventFromDB(id).getDate());
        assertEquals(postEventRequest.getDescription(), dbClient.getEventFromDB(id).getDescription());


    }


    @Test
    public void SucessfullUpdateHealthCheck() throws SQLException {
        //update event (update event with the taken id from before method
        postEventRequest = new PostEventDataFactory(PostEventObjectBuilder.createBodyForPostEvent())
                .createRequest();

        Response response = new EVPrimeClient()
                .updateEvent(postEventRequest, id, loginResponseBody.getToken());

        PostUpdateDeleteEventResponse responseBody = response.body().as(PostUpdateDeleteEventResponse.class);

        assertEquals(201, response.statusCode());
        assertEquals("Successfully updated the event with id: " + id, responseBody.getMessage());
        assertEquals(postEventRequest.getTitle(), dbClient.getEventFromDB(id).getTitle());
        assertEquals(postEventRequest.getImage(), dbClient.getEventFromDB(id).getImage());
        assertEquals(postEventRequest.getLocation(), dbClient.getEventFromDB(id).getLocation());
        assertEquals(postEventRequest.getDate(), dbClient.getEventFromDB(id).getDate());
        assertEquals(postEventRequest.getDescription(), dbClient.getEventFromDB(id).getDescription());


    }

    @Test
    public void UnSucessfullUpdateEmptyLocationTest() throws SQLException {
        //update event (update event with the taken id from before method
        postEventRequest = new PostEventDataFactory(PostEventObjectBuilder.createBodyForPostEvent())
                .setTitle("updated title")
                .setLocation("")
                .setDate("2025-12-07")
                .setDescription("updated description")
                .setImage("https://updated image")
                .createRequest();

        Response response = new EVPrimeClient()
                .updateEvent(postEventRequest, id, loginResponseBody.getToken());

        ErrorsResponse errorsResponse = response.body().as(ErrorsResponse.class);

        assertEquals(422, response.statusCode());
        assertEquals("Updating the event failed due to validation errors.", errorsResponse.getMessage());
        assertEquals("Invalid location.", errorsResponse.getErrors().getDescription());
    }

    @Test
    public void UnSucessfullUpdateEmptyImageTest() throws SQLException {
        //update event (update event with the taken id from before method
        postEventRequest = new PostEventDataFactory(PostEventObjectBuilder.createBodyForPostEvent())
                .setImage("https://updated image")
                .setTitle("updated title")
                .setLocation("updated location")
                .setDate("2025-12-07")
                .setDescription("updated description")
                .setImage("")
                .createRequest();

        Response response = new EVPrimeClient()
                .updateEvent(postEventRequest, id, loginResponseBody.getToken());

        ErrorsResponse errorsResponse = response.body().as(ErrorsResponse.class);

        assertEquals(422, response.statusCode());
        assertEquals("Updating the event failed due to validation errors.", errorsResponse.getMessage());
        assertEquals("Invalid image.", errorsResponse.getErrors().getImage());
    }


    @Test
    public void UnSucessfullUpdateEmptyDateTest() throws SQLException {
        //update event (update event with the taken id from before method
        postEventRequest = new PostEventDataFactory(PostEventObjectBuilder.createBodyForPostEvent())
                .setImage("https://updated image")
                .setTitle("updated title")
                .setLocation("updated location")
                .setDate("")
                .setDescription("updated description")
                .setImage("https://updated image")
                .createRequest();

        Response response = new EVPrimeClient()
                .updateEvent(postEventRequest, id, loginResponseBody.getToken());

        ErrorsResponse errorsResponse = response.body().as(ErrorsResponse.class);

        assertEquals(422, response.statusCode());
        assertEquals("Updating the event failed due to validation errors.", errorsResponse.getMessage());
        assertEquals("Invalid date.", errorsResponse.getErrors().getDate());
    }

    @Test
    public void UnSucessfullUpdateEmptyDescriptionTest() throws SQLException {
        //update event (update event with the taken id from before method
        postEventRequest = new PostEventDataFactory(PostEventObjectBuilder.createBodyForPostEvent())
                .setImage("https://updated image")
                .setTitle("updated title")
                .setLocation("updated location")
                .setDate("2025-12-07")
                .setDescription("")
                .setImage("https://updated image")
                .createRequest();

        Response response = new EVPrimeClient()
                .updateEvent(postEventRequest, id, loginResponseBody.getToken());

        ErrorsResponse errorsResponse = response.body().as(ErrorsResponse.class);

        assertEquals(422, response.statusCode());
        assertEquals("Updating the event failed due to validation errors.", errorsResponse.getMessage());
        assertEquals("Invalid description.", errorsResponse.getErrors().getDescription());
    }

    @Test
    public void UnSucessfullUpdateEmptyTitleTest() throws SQLException {
        //update event (update event with the taken id from before method
        postEventRequest = new PostEventDataFactory(PostEventObjectBuilder.createBodyForPostEvent())
                .setImage("https://updated image")
                .setTitle("")
                .setLocation("updated location")
                .setDate("2025-12-07")
                .setDescription("updated description")
                .setImage("https://updated image")
                .createRequest();

        Response response = new EVPrimeClient()
                .updateEvent(postEventRequest, id, loginResponseBody.getToken());

        ErrorsResponse errorsResponse = response.body().as(ErrorsResponse.class);

        assertEquals(422, response.statusCode());
        assertEquals("Updating the event failed due to validation errors.", errorsResponse.getMessage());
        assertEquals("Invalid title.", errorsResponse.getErrors().getTitle());
    }


    @AfterEach
    public void cleanUp() throws SQLException {

        boolean isDeleted = dbClient.isEventDeleted(id);
        assertTrue(isDeleted);

    }
}
