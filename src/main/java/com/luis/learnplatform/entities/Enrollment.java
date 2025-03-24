package com.luis.learnplatform.entities;


import com.luis.learnplatform.entities.PK.EnrollmentPK;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name="tb_enrollment")
public class Enrollment {

    @EmbeddedId
    private EnrollmentPK id;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant enrollMoment;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant refundMoment;
    private boolean available;
    private boolean onlyUpdate;

    @ManyToOne
    @MapsId("userId") // Relaciona o campo userId da chave primaria
    @JoinColumn(name="user_id", insertable=false, updatable=false)
    private User user;

    @ManyToOne
    @MapsId("offerId") // Relaciona o campo offerId da chave primaria
    @JoinColumn(name="offer_id", insertable=false, updatable=false)
    private Offer offer;

    /*Atributos insertable = false, updatable = false são usados nas anotações @JoinColumn quando se está mapeando
    um relacionamento usando uma chave primária composta. Esses atributos indicam que o JPA não deve incluir esses
    campos em comandos INSERT ou UPDATE, pois os valores já estão sendo gerenciados pelo @EmbeddedId.*/

    @ManyToMany(mappedBy = "enrollmentsDone")
    private Set<Lesson> lessonsDone=new HashSet<>();

    @OneToMany(mappedBy = "enrollment")
    private List<Deliver> deliveries=new ArrayList<>();


    public Enrollment() {
    }

    public Enrollment(User user,Offer offer, Instant enrollMoment, Instant refundMoment, boolean available, boolean onlyUpdate) {
        id=new EnrollmentPK(user.getId(),offer.getId());
        this.enrollMoment = enrollMoment;
        this.refundMoment = refundMoment;
        this.available = available;
        this.onlyUpdate = onlyUpdate;
    }

   public User getStudent() {
        return user;
   }

   public void setStudent(User user) {
        this.user = user;
   }

   public Offer getOffer() {
        return offer;
   }

   public void setOffer(Offer offer) {
        this.offer = offer;
   }

    public Instant getEnrollMoment() {
        return enrollMoment;
    }

    public void setEnrollMoment(Instant enrollMoment) {
        this.enrollMoment = enrollMoment;
    }

    public Instant getRefundMoment() {
        return refundMoment;
    }

    public void setRefundMoment(Instant refundMoment) {
        this.refundMoment = refundMoment;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isOnlyUpdate() {
        return onlyUpdate;
    }

    public void setOnlyUpdate(boolean onlyUpdate) {
        this.onlyUpdate = onlyUpdate;
    }

    public Set<Lesson> getLessonsDone() {
        return lessonsDone;
    }

    public List<Deliver> getDeliveries() {
        return deliveries;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment that = (Enrollment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
