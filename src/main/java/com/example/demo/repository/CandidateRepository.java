package com.example.demo.repository;

import com.example.demo.model.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Integer> {

    // 1. Dành cho Admin
    // SỬA: SELECT c, j thay vì c, j.title... để lấy cả object Job
    @Query("SELECT c, j FROM Candidate c " +
            "LEFT JOIN c.candidates cj " +
            "LEFT JOIN cj.job j " +
            "WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            " LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:status IS NULL OR :status = '' OR :status = 'ALL' OR c.status = :status)")
    Page<Object[]> findWithFilters(
            @Param("search") String search,
            @Param("status") String status,
            Pageable pageable
    );

    // 2. Dành cho Recruiter: Chỉ lấy ứng viên đã nộp vào Job do mình tạo
    @Query("SELECT c, j FROM Candidate c " +
            "JOIN c.candidates cj " +       // INNER JOIN: Bắt buộc phải có trong bảng CandidateJob
            "JOIN cj.job j " +
            "WHERE j.createBy = :userId " + // Bắt buộc Job phải do userId này tạo
            "AND (:search IS NULL OR :search = '' OR " +
            " LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:status IS NULL OR :status = '' OR :status = 'ALL' OR c.status = :status)")
    Page<Object[]> findMyCandidatesWithFilters(
            @Param("userId") Integer userId,
            @Param("search") String search,
            @Param("status") String status,
            Pageable pageable
    );

    // ... Các hàm khác giữ nguyên
    Candidate findByUserCandidate_IdUser(Integer userId);

    @Query("SELECT c FROM Candidate c WHERE c.userCandidate.idUser = :userId")
    Candidate findCandidateByUserId(@Param("userId") Integer userId);

    @Query("SELECT c FROM Candidate c")
    List<Candidate> candidates();
}