package matching.teamify.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class MyPageResponse {

    private String nickName;
    private String university;
    private String email;
    private String imageUrl;

}
