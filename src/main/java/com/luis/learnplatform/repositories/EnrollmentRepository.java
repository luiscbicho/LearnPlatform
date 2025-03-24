package com.luis.learnplatform.repositories;

import com.luis.learnplatform.entities.Enrollment;
import com.luis.learnplatform.entities.PK.EnrollmentPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentPK> {

    //@Query("SELECT e FROM Enrollment e WHERE e.id.user.id = :userId") nao é necessário o JPA assume logo pelo nome do metodo que é por UserId. mas caso nao funcionasse...
    List<Enrollment> findByUserId(Long userId);

}
