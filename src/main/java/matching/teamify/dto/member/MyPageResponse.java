package matching.teamify.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyPageResponse {

    private String nickName;
    private String university;
    private String email;
    private String imageUrl;

    public MyPageResponse(String nickName, String university, String email) {
        this.nickName = nickName;
        this.university = university;
        this.email = email;
    }
}
