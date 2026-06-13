package objectBuilder;

import models.request.PostUpdateEventRequest;

public class PostEventObjectBuilder {

    public static PostUpdateEventRequest createBodyForPostEvent() {
        return PostUpdateEventRequest.builder()
                .title("default title")
                .image("https://default image")
                .date("2026-06-05")
                .location("default location")
                .description("default description")
                .build();
    }
}
