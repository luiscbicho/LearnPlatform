package com.luis.learnplatform.entities.PK;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class EnrollmentPK {

    @Column(name="user_id")
    private Long userId;

    @Column(name="offer_id")
    private Long offerId;

    public EnrollmentPK() {
    }

    public EnrollmentPK(Long userId, Long offerId) {
        this.userId = userId;
        this.offerId = offerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EnrollmentPK that = (EnrollmentPK) o;
        return Objects.equals(userId, that.userId) && Objects.equals(offerId, that.offerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, offerId);
    }
}
