package com.luis.learnplatform.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name="tb_topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String body;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant moment;

    @ManyToOne
    @JoinColumn(name="lesson_id")
    private Lesson lesson;
    @ManyToOne
    @JoinColumn(name = "reply_id")
    private Reply answer;
    @OneToMany(mappedBy = "topic")
    private List<Reply> replies=new ArrayList<>();
    @ManyToOne
    @JoinColumn(name="author_id")
    private User author;
    @ManyToMany
    @JoinTable(name="tb_topic_likes",joinColumns = @JoinColumn(name="topic_id"),inverseJoinColumns = @JoinColumn(name="user_id"))
    private Set<User> likes=new HashSet<>();
    @ManyToOne
    @JoinColumn(name="offer_id")
    private Offer offer;


    public Topic() {
    }

    public Topic(Long id, String title, String body, Instant moment, Lesson lesson, Reply answer, User author, Offer offer) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.moment = moment;
        this.lesson = lesson;
        this.answer = answer;
        this.author = author;
        this.offer = offer;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Set<User> getLikes() {
        return likes;
    }


    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Reply getAnswer() {
        return answer;
    }

    public void setAnswer(Reply answer) {
        this.answer = answer;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return Objects.equals(id, topic.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
