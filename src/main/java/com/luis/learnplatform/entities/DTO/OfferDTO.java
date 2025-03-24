package com.luis.learnplatform.entities.DTO;

import com.luis.learnplatform.entities.Offer;

import java.time.Instant;

public class OfferDTO {

    private Long id;
    private String edition;
    private Instant startMoment;
    private Instant endMoment;
    private CourseDTO course;

    public OfferDTO() {
    }

    public OfferDTO(Long id, String edition, Instant startMoment, Instant endMoment, CourseDTO course) {
        this.id = id;
        this.edition = edition;
        this.startMoment = startMoment;
        this.endMoment = endMoment;
        this.course = course;
    }

    public OfferDTO(Offer offer) {
        id = offer.getId();
        edition = offer.getEdition();
        startMoment = offer.getStartMoment();
        endMoment = offer.getEndMoment();
        course = new CourseDTO(offer.getCourse());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }
}
