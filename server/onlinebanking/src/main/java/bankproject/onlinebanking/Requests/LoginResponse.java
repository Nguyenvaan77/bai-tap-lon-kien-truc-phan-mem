package bankproject.onlinebanking.Requests;

import bankproject.onlinebanking.Model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class LoginResponse {

    private String jwtToken;
    private User user;

}
