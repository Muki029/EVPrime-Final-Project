package signupAndLoginResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LoginErrorsResponse {

    String message;
    LoginErrors errors;
}
