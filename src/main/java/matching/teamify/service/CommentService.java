package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching.teamify.domain.*;
import matching.teamify.dto.comment.CommentRequest;
import matching.teamify.dto.comment.CommentResponse;
import matching.teamify.common.exception.ErrorCode;
import matching.teamify.common.exception.TeamifyException;
import matching.teamify.repository.CommentRepository;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.ProjectRepository;
import matching.teamify.repository.StudyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void createProjectComment(Long projectId, Long memberId, CommentRequest commentRequest) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        Comment comment = Comment.builder()
                .project(project)
                .member(member)
                .comment(commentRequest.getComment())
                .type(CommentType.PROJECT)
                .build();
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findProjectComments(Long projectId) {
        List<CommentResponse> comments = commentRepository.findByProjectId(projectId);
        for (CommentResponse comment : comments) {
            String s3Key = comment.getImageUrl();
            comment.setImageUrl(s3ImageService.generatePresignedUrl(s3Key));
        }
        return comments;
    }

    @Transactional
    public void createStudyComment(Long studyId, Long memberId, CommentRequest commentRequest) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        Comment comment = Comment.builder()
                .study(study)
                .member(member)
                .comment(commentRequest.getComment())
                .type(CommentType.STUDY)
                .build();
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findStudyComments(Long studyId) {
        List<CommentResponse> comments = commentRepository.findByStudyId(studyId);
        for (CommentResponse comment : comments) {
            String s3Key = comment.getImageUrl();
            comment.setImageUrl(s3ImageService.generatePresignedUrl(s3Key));
        }
        return comments;
    }

    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        if (!comment.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }
        commentRepository.delete(comment);
    }
}
