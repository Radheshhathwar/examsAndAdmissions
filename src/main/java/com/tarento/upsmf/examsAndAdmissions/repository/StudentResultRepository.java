package com.tarento.upsmf.examsAndAdmissions.repository;

import com.tarento.upsmf.examsAndAdmissions.model.StudentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudentResultRepository extends JpaRepository<StudentResult, Long> {
    StudentResult findByStudent_EnrollmentNumber(String enrolmentNumber);
    List<StudentResult> findByCourse_IdAndExam_ExamCycleIdAndPublished(Long courseId, Long examCycleId, boolean b);

    StudentResult findByStudent_EnrollmentNumberAndStudent_DateOfBirthAndPublished(String enrollmentNumber, LocalDate dateOfBirth, boolean b);
}