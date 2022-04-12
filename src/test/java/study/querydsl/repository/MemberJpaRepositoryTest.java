package study.querydsl.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.MemberSearchFilter;
import study.querydsl.entity.MemberTeamDto;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void basicTest() throws Exception {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get();
        assertThat(member).isEqualTo(findMember);

        List<Member> result1 = memberJpaRepository.findAll();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberJpaRepository.findByUsername("member1");
        assertThat(result2).containsExactly(member);

        List<Member> result3 = memberJpaRepository.findAll_QueryDsl();
        assertThat(result3).containsExactly(member);

        List<Member> result4 = memberJpaRepository.findByUsername_QueryDsl("member1");
        assertThat(result4).containsExactly(member);
    }

    @Test
    public void searchTest1() throws Exception {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchFilter memberSearchFilter = new MemberSearchFilter();
        memberSearchFilter.setAgeGoe(35);
        memberSearchFilter.setAgeLoe(40);
        memberSearchFilter.setTeamName("teamB");

        List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(memberSearchFilter);
        assertThat(result).extracting("userName").containsExactly("member4");
    }

    @Test
    public void searchTest2() throws Exception {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchFilter memberSearchFilter = new MemberSearchFilter();
        memberSearchFilter.setTeamName("teamB");

        List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(memberSearchFilter);
        assertThat(result).extracting("userName").containsExactly("member3","member4");
    }

    @Test
    public void searchTest3() throws Exception {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchFilter memberSearchFilter = new MemberSearchFilter();
        memberSearchFilter.setTeamName("teamB");
        memberSearchFilter.setAgeGoe(35);
        memberSearchFilter.setAgeLoe(40);

        List<MemberTeamDto> result = memberJpaRepository.search(memberSearchFilter);
        assertThat(result).extracting("userName").containsExactly("member4");
    }
}