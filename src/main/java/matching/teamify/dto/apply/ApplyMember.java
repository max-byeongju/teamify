package matching.teamify.dto.apply;

import lombok.AllArgsConstructor;
import lombok.Data;
import matching.teamify.domain.ApplyStatus;
import matching.teamify.domain.ProjectRole;

@Data
@AllArgsConstructor
public class ApplyMember {

    private Long memberId;
    private ApplyStatus applyStatus;
    private String imageUrl;
    private String nickName;
    private ProjectRole role;
    private String application;

}
