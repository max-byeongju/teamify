package matching.teamify.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignUpRequest {

    private String loginId;
    private String password;

    private String nickName;
    private String university;
    private String email;

}
