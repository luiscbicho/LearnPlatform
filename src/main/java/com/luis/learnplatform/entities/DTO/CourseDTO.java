package com.luis.learnplatform.entities.DTO;

import com.luis.learnplatform.entities.Course;

public class CourseDTO {

    private Long id;
    private String name;
    private String imgUri;
    private String imgGrayUri;

    public CourseDTO() {
    }

    public CourseDTO(Long id, String name, String imgUri, String imgGrayUri) {
        this.id = id;
        this.name = name;
        this.imgUri = imgUri;
        this.imgGrayUri = imgGrayUri;
    }

    public CourseDTO(Course course) {
        id = course.getId();
        name = course.getName();
        imgUri = course.getImgUri();
        imgGrayUri = course.getImgGrayUri();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
