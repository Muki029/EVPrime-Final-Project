import client.EVPrimeClient;
import data.PostEventDataFactory;
import database.DBClient;
import io.restassured.response.Response;
import models.request.PostUpdateEventRequest;
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

public class DeleteEventTests {

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
    public void SuccessfullDeleteTest() {
        //delete event (delete event with the token id from before method)
        Response response = new EVPrimeClient()
                .deleteEvent(id, loginResponseBody.getToken());

        assertEquals(200, response.statusCode());

        PostUpdateDeleteEventResponse responseBody = response.body().as(PostUpdateDeleteEventResponse.class);
        assertEquals("Successfully deleted the event with id: " + id, responseBody.getMessage());
    }


    @Test
    public void UnsuccessfuldeleteWithoutTokenTest() {
        //delete event (delete event with the token id from before method)
        Response response = new EVPrimeClient()
                .deleteEvent(id,"");

        PostUpdateDeleteEventResponse responseBody = response.body().as(PostUpdateDeleteEventResponse.class);

        assertEquals(401, response.statusCode());
        assertEquals("Not authenticated.", responseBody.getMessage());
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
