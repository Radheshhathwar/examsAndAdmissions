package com.tarento.upsmf.examsAndAdmissions.service;

import com.tarento.upsmf.examsAndAdmissions.enums.RetotallingStatus;
import com.tarento.upsmf.examsAndAdmissions.model.Exam;
import com.tarento.upsmf.examsAndAdmissions.model.RetotallingRequest;
import com.tarento.upsmf.examsAndAdmissions.model.Student;
import com.tarento.upsmf.examsAndAdmissions.model.dao.Payment;
import com.tarento.upsmf.examsAndAdmissions.repository.PaymentRepository;
import com.tarento.upsmf.examsAndAdmissions.repository.RetotallingRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RetotallingService {

    @Autowired
    private RetotallingRequestRepository retotallingRequestRepository;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private StudentResultService studentResultService;

    public void markRequestAsCompleted(Long requestId) {
        RetotallingRequest request = retotallingRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found."));
        request.setStatus(RetotallingStatus.COMPLETED);
        retotallingRequestRepository.save(request);
    }
    public RetotallingRequest requestRetotalling(RetotallingRequest retotallingRequest) {
        // Fetch the student from the database using the enrollmentNumber
        Student existingStudent = studentResultService.fetchStudentByEnrollmentNumber(retotallingRequest.getStudent().getEnrollmentNumber());
        if (existingStudent == null) {
            throw new RuntimeException("Student not found.");
        }

        // Set the fetched student to the retotallingRequest
        retotallingRequest.setStudent(existingStudent);

        // Check for each exam
        for (Exam exam : retotallingRequest.getExams()) {
            // Check if payment was successful
            if (!isPaymentSuccessful(existingStudent.getEnrollmentNumber(), exam.getId())) {
                throw new RuntimeException("Payment not completed for exam ID: " + exam.getId() + ". Please make the payment before requesting re-totalling.");
            }

            // Check if a re-totalling request already exists
            if (hasAlreadyRequestedRetotalling(existingStudent.getEnrollmentNumber(), exam.getId())) {
                throw new RuntimeException("You have already requested re-totalling for exam ID: " + exam.getId());
            }
        }

        // Save the re-totalling request
        retotallingRequest.setRequestDate(LocalDate.now());
        retotallingRequest.setStatus(RetotallingStatus.PENDING);
        return retotallingRequestRepository.save(retotallingRequest);
    }

    public List<RetotallingRequest> getAllPendingRequests() {
        return retotallingRequestRepository.findAll();
    }
    public boolean hasAlreadyRequestedRetotalling(String enrolmentNumber, Long examId) {
        return retotallingRequestRepository.existsByStudent_EnrollmentNumberAndExams_Id(enrolmentNumber, examId);
    }
    public boolean isPaymentSuccessful(String enrolmentNumber, Long examId) {
        Optional<Payment> paymentOptional = Optional.ofNullable(paymentRepository.findByEnrollmentNumber(enrolmentNumber));
        if (paymentOptional.isEmpty()) {
            throw new RuntimeException("Payment not found for the given enrollment number.");
        }

        Payment payment = paymentRepository.findByEnrollmentNumber(enrolmentNumber);
        if (payment != null && payment.getExams() != null) {
            return payment.getExams().stream().anyMatch(exam -> exam.getId().equals(examId));
        }
        return false;
    }
}