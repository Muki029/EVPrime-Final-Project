import client.EVPrimeClient;
import data.PostEventDataFactory;
import database.DBClient;
import io.restassured.response.Response;
import models.request.PostUpdateEventRequest;
import models.response.EventResponseGET;
import models.response.LoginResponse;
import models.response.PostUpdateDeleteEventResponse;
import objectBuilder.PostEventObjectBuilder;
import objectBuilder.SignUpLoginObjectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GetEventsTests {


    private LoginResponse loginResponseBody;
    private PostUpdateEventRequest postEventRequest;
    private static String id;
    private DBClient dbClient = new DBClient();


    @BeforeEach
    public void setUp() {

        //before each test here we post a post with default body to do the GET test in already created post

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
    public void getAllEventsTest() {

        Response getAllEvents = new EVPrimeClient()
                .getAllEvents();

        List<EventResponseGET> getResponse = getAllEvents.body().jsonPath().getList("events");

        assertEquals(200, getAllEvents.getStatusCode());
        assertFalse(getResponse.isEmpty());
    }

    @Test
    public void getEventByIdTest() throws SQLException {
        Response getById = new EVPrimeClient()
                .getEventByID(id);

        assertEquals(200, getById.statusCode());

        EventResponseGET postResponse = getById.body().as(EventResponseGET.class);


        //we assert locally but after that we get result from the DataBase
        assertEquals("2026-06-05", postResponse.getEvents().get(0).getDate());
        assertEquals("default title", postResponse.getEvents().get(0).getTitle());
        assertEquals("https://default image", postResponse.getEvents().get(0).getImage());
        assertEquals("default description", postResponse.getEvents().get(0).getDescription());


        assertEquals(postResponse.getEvents().get(0).getDate(), dbClient.getEventFromDB(id).getDate());
        assertEquals(postResponse.getEvents().get(0).getTitle(), dbClient.getEventFromDB(id).getTitle());
        assertEquals(postResponse.getEvents().get(0).getImage(), dbClient.getEventFromDB(id).getImage());
        assertEquals(postResponse.getEvents().get(0).getDescription(), dbClient.getEventFromDB(id).getDescription());
        assertEquals(postResponse.getEvents().get(0).getId(), id);

    }

    @Test
    public void getEventByFalseIDTest() throws SQLException {
        Response getById = new EVPrimeClient()
                .getEventByID("random id");

        assertEquals(200, getById.statusCode());

        List<EventResponseGET> getResponse = getById.body().jsonPath().getList("events");

        assertTrue(getResponse.isEmpty());


    }

    @AfterEach
    public void cleanUp() throws SQLException {
        if (id == null) {
            return;
        }

        boolean isDeleted = dbClient.isEventDeleted(id);
        assertTrue(isDeleted);

        id = null;
    }
}
