package matching.teamify.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import matching.teamify.domain.Member;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

}

