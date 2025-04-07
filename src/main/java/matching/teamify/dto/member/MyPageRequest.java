package matching.teamify.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyPageRequest {

    private String nickName;
    private String university;
    private String email;
    private String imageUrl;

    public MyPageRequest(String nickName, String university, String email) {
        this.nickName = nickName;
        this.university = university;
        this.email = email;
    }
}
