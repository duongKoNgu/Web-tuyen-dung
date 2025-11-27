package com.example.demo.repository;

import com.example.demo.model.Candidate;
import com.example.demo.model.CandidateJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateJobRepository extends JpaRepository<CandidateJob, Integer> {

    @Query("SELECT c FROM Candidate c WHERE c.userCandidate.idUser = :userId")
    Candidate findCandidateByUserId(@Param("userId") Integer userId);

    @Query("SELECT COUNT(cj) > 0 FROM CandidateJob cj " +
            "WHERE cj.candidate.candidateId = :candidateId " +
            "AND cj.job.jobId = :jobId")
    boolean checkIfAlreadyApplied(
            @Param("candidateId") Integer candidateId,
            @Param("jobId") Integer jobId
    );
}