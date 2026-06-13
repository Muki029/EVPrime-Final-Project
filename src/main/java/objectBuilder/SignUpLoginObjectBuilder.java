package objectBuilder;

import models.request.SignUpLoginRequest;

public class SignUpLoginObjectBuilder {
    public static SignUpLoginRequest createBodyForSignUpLogin() {
        return SignUpLoginRequest.builder()
                .email("defaultEmail@mail.com")
                .password("defaultPassword")
                .build();
    }
}
