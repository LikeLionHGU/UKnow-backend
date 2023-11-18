package hgu.likelion.uknow.repository;

import hgu.likelion.uknow.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
}
