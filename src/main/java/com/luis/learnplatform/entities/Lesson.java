package com.luis.learnplatform.entities;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name="tb_lesson")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer position;

    @ManyToOne
    @JoinColumn(name="section_id")
    private Section section;

    @ManyToMany
    @JoinTable(name="tb_lessons_done",joinColumns = @JoinColumn(name="lesson_id"),inverseJoinColumns = {@JoinColumn(name="user_id"),@JoinColumn(name="offer_id")})
    private Set<Enrollment> enrollmentsDone=new HashSet<>();

    @OneToMany(mappedBy = "lesson")
    private List<Deliver> delivers=new ArrayList<>();


    public Lesson() {
    }


    public Lesson(Long id, String title, Integer position, Section section) {
        this.id = id;
        this.title = title;
        this.position = position;
        this.section = section;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Set<Enrollment> getEnrollmentsDone() {
        return enrollmentsDone;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(id, lesson.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
