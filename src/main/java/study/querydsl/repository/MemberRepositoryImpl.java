package study.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import study.querydsl.entity.MemberSearchFilter;
import study.querydsl.entity.MemberTeamDto;
import study.querydsl.entity.QMemberTeamDto;

import javax.persistence.EntityManager;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MemberTeamDto> search(MemberSearchFilter filter) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(filter.getUsername()),
                        teamnameEq(filter.getTeamName()),
                        ageGoe(filter.getAgeGoe()),
                        ageLoe(filter.getAgeLoe())
                )
                .fetch();
    }

    private Predicate usernameEq(String username) {
        return hasText(username) ? member.username.eq(username) : null;
    }

    private Predicate teamnameEq(String teamName) {
        return hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private Predicate ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private Predicate ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }

    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchFilter filter, Pageable pageable) {
        QueryResults<MemberTeamDto> result = queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(filter.getUsername()),
                        teamnameEq(filter.getTeamName()),
                        ageGoe(filter.getAgeGoe()),
                        ageLoe(filter.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MemberTeamDto> content = result.getResults();
        long total = result.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchFilter filter, Pageable pageable) {
        List<MemberTeamDto> content = getMemberTeamDto(filter, pageable);
        long total = getTotal(filter);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex2(MemberSearchFilter filter, Pageable pageable) {
        List<MemberTeamDto> content = getMemberTeamDto(filter, pageable);
        JPAQuery<MemberTeamDto> countQuery = queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(filter.getUsername()),
                        teamnameEq(filter.getTeamName()),
                        ageGoe(filter.getAgeGoe()),
                        ageLoe(filter.getAgeLoe())
                );
        return PageableExecutionUtils.getPage(content, pageable, ()->countQuery.fetchCount());
    }

    private List<MemberTeamDto> getMemberTeamDto(MemberSearchFilter filter, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(filter.getUsername()),
                        teamnameEq(filter.getTeamName()),
                        ageGoe(filter.getAgeGoe()),
                        ageLoe(filter.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return content;
    }

    private long getTotal(MemberSearchFilter filter) {
        long total = queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(filter.getUsername()),
                        teamnameEq(filter.getTeamName()),
                        ageGoe(filter.getAgeGoe()),
                        ageLoe(filter.getAgeLoe())
                )
                .fetchCount();
        return total;
    }
}
