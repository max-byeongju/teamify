package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching.teamify.domain.Comment;
import matching.teamify.domain.Member;
import matching.teamify.domain.Project;
import matching.teamify.domain.Study;
import matching.teamify.dto.comment.CommentRequest;
import matching.teamify.dto.comment.CommentResponse;
import matching.teamify.exception.common.EntityNotFoundException;
import matching.teamify.repository.CommentRepository;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.ProjectRepository;
import matching.teamify.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final ProjectRepository projectRepository;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final S3ImageService s3ImageService;

    @Value("${app.default-profile-image-url}")
    private String defaultProfileImageUrl;

    @Transactional
    public void createProjectComment(Long projectId, Long memberId, CommentRequest commentRequest) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project", projectId));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member", memberId));
        Comment comment = Comment.builder()
                .project(project)
                .member(member)
                .localDateTime(LocalDateTime.now())
                .comment(commentRequest.getComment())
                .build();
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findProjectComments(Long projectId) {
        List<CommentResponse> comments = commentRepository.findByProjectId(projectId);
        for (CommentResponse comment : comments) {
            String s3Key = comment.getImageUrl();
            if (s3Key == null || s3Key.trim().isEmpty()) {
                comment.setImageUrl(defaultProfileImageUrl);
            } else {
                String imageUrl = s3ImageService.getImageUrl(s3Key);
                comment.setImageUrl(imageUrl);
            }
        }
        return comments;
    }

    @Transactional
    public void createStudyComment(Long studyId, Long memberId, CommentRequest commentRequest) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new EntityNotFoundException("Study", studyId));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member", memberId));
        Comment comment = Comment.builder()
                .study(study)
                .member(member)
                .localDateTime(LocalDateTime.now())
                .comment(commentRequest.getComment())
                .build();
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findStudyComments(Long studyId) {
        List<CommentResponse> comments = commentRepository.findByStudyId(studyId);
        for (CommentResponse comment : comments) {
            String s3Key = comment.getImageUrl();
            if (s3Key == null || s3Key.trim().isEmpty()) {
                comment.setImageUrl(defaultProfileImageUrl);
            } else {
                String imageUrl = s3ImageService.getImageUrl(s3Key);
                comment.setImageUrl(imageUrl);
            }
        }
        return comments;
    }

    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment", commentId));
        if (!comment.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }
        commentRepository.delete(comment);
    }
}
