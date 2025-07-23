package matching.teamify.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String name;
    private String imageUrl;
    private LocalDate createdDate;
    private String comment;
}
