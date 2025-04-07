package matching.teamify.dto.apply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationReferenceRequest {
    private Long memberId;
    private Long projectId;
}
