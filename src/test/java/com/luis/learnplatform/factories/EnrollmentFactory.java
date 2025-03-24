package com.luis.learnplatform.factories;

import com.luis.learnplatform.entities.DTO.EnrollmentDTO;
import com.luis.learnplatform.entities.Enrollment;

import java.time.Instant;

public class EnrollmentFactory {

    public static EnrollmentDTO createEnrollmentDTO() {
        return new EnrollmentDTO(1L,"Bootcamp HTML","1.0", Instant.parse("2020-11-20T03:00:00Z"),Instant.parse("2021-11-20T03:00:00Z"),"https://cdn.pixabay.com/photo/2018/03/22/10/55/training-course-3250007_1280.jpg","https://upload.wikimedia.org/wikipedia/commons/1/1f/Switch-course-book-grey.svg");
    }

}
