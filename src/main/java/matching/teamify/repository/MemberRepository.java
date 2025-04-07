package matching.teamify.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import matching.teamify.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public Member findById(Long memberId) {
        return em.find(Member.class, memberId);
    }

    public Optional<Member> findByLoginId(String loginId) {
        return em.createQuery("select m from Member m where m.loginId = :loginId", Member.class)
                .setParameter("loginId", loginId)
                .getResultStream()
                .findFirst();
    }

}

