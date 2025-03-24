package com.luis.learnplatform.entities.DTO;

import com.luis.learnplatform.entities.Enrollment;

import java.time.Instant;

public class EnrollmentDTO {

    private Long offerId;
    private String courseName;
    private String edition;
    private Instant startMoment;
    private Instant endMoment;
    private String imgUri;
    private String imgGrayUri;

    public EnrollmentDTO() {
    }

    public EnrollmentDTO(Long offerId, String courseName, String edition, Instant startMoment, Instant endMoment, String imgUri, String imgGrayUri) {
        this.offerId = offerId;
        this.courseName = courseName;
        this.edition = edition;
        this.startMoment = startMoment;
        this.endMoment = endMoment;
        this.imgUri = imgUri;
        this.imgGrayUri = imgGrayUri;
    }

    public EnrollmentDTO(Enrollment enrollment) {
        offerId = enrollment.getOffer().getId();
        courseName = enrollment.getOffer().getCourse().getName();
        edition = enrollment.getOffer().getEdition();
        startMoment = enrollment.getOffer().getStartMoment();
        endMoment = enrollment.getOffer().getEndMoment();
        imgUri = enrollment.getOffer().getCourse().getImgUri();
        imgGrayUri = enrollment.getOffer().getCourse().getImgGrayUri();
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public Instant getStartMoment() {
        return startMoment;
    }

    public void setStartMoment(Instant startMoment) {
        this.startMoment = startMoment;
    }

    public Instant getEndMoment() {
        return endMoment;
    }

    public void setEndMoment(Instant endMoment) {
        this.endMoment = endMoment;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getImgGrayUri() {
        return imgGrayUri;
    }

    public void setImgGrayUri(String imgGrayUri) {
        this.imgGrayUri = imgGrayUri;
    }
}
