package matching.teamify.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column
    private String nickName;

    @Column
    private String university;

    @Column(unique = true, nullable = false)
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
}
