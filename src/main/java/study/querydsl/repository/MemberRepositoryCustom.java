package study.querydsl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.querydsl.entity.MemberSearchFilter;
import study.querydsl.entity.MemberTeamDto;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSearchFilter filter);
    Page<MemberTeamDto> searchPageSimple(MemberSearchFilter filter, Pageable pageable);
    Page<MemberTeamDto> searchPageComplex(MemberSearchFilter filter, Pageable pageable);
    Page<MemberTeamDto> searchPageComplex2(MemberSearchFilter filter, Pageable pageable);
}
