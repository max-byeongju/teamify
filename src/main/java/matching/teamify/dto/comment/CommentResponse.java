package matching.teamify.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String name;
    private String imageUrl;
    private LocalDateTime localDateTime;
    private String comment;
}
