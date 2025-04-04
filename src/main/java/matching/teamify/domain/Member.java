package matching.teamify.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {

    @Id @GeneratedValue
    private Long id;

    @Column
    private String loginId;
    @Column
    private String password;
    @Column
    private String nickName;
    @Column
    private String university;
    @Column
    private String email;
    @Column
    private String picture;

    @Builder
    public Member(String loginId, String password, String nickName, String university, String email) {
        this.loginId = loginId;
        this.password = password;
        this.nickName = nickName;
        this.university = university;
        this.email = email;
    }

    public void updateProfile(String imgUrl, String nickName, String university, String email) {
        this.picture = imgUrl;
        this.nickName = nickName;
        this.university = university;
        this.email = email;
    }

    public void updateProfileNotImage(String nickName, String university, String email) {
        this.nickName = nickName;
        this.university = university;
        this.email = email;
    }

    public void updateProfileTest(String imgUrl) {
        this.picture = imgUrl;
    }


}
