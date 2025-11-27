package com.example.demo.repository;

import com.example.demo.model.Job; // Giả sử Job model nằm trong package này
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {

    @Query("SELECT j FROM Job j")
    List<Job> findAllJobs(); // Giữ phương thức lấy tất cả (nếu cần)

    /**
     * Phân trang, tìm kiếm theo tên/mô tả và lọc theo trạng thái (status)
     * Giống với CandidateRepository
     */
    @Query("SELECT j FROM Job j WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " + // Tìm kiếm theo tiêu đề (title)
            "LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " + // Tìm kiếm theo mô tả (description)
            "(:status IS NULL OR :status = '' OR j.status = :status)")
    Page<Job> findWithFilters(
            @Param("search") String search,
            @Param("status") String status,
            Pageable pageable
    );

    // 2. Query MỚI dành cho User/Recruiter (Chỉ lấy job của chính họ)
    // Query cho Recruiter (SỬA LẠI TÊN TRƯỜNG Ở ĐÂY)
    @Query("SELECT j FROM Job j WHERE " +
            "j.createBy = :userId AND " +  // <--- Đã sửa từ j.userId thành j.createBy
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:status IS NULL OR :status = '' OR j.status = :status)")
    Page<Job> findMyJobsWithFilters(@Param("userId") int userId,
                                    @Param("search") String search,
                                    @Param("status") String status,
                                    Pageable pageable);

}