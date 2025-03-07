package com.luis.learnplatform.repositories;

import com.luis.learnplatform.entities.User;
import com.luis.learnplatform.entities.projections.UserDetailsProjection;
import com.luis.learnplatform.services.exceptions.DatabaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    private Long existingId;
    private Long nonExistingId;
    private String existingEmail;
    private String nonExistingEmail;
    private Long expectedCountTotal;
    private Long expectedId;
    private User user=new User();

    @BeforeEach
    void setUp() {

        existingId = 1L;
        nonExistingId = 100L;
        existingEmail = "bob@gmail.com";
        nonExistingEmail = "julio@gdsdmail.com";
        expectedCountTotal = 4L;
        user.setName("Luis");
        user.setEmail("luis@gmail.com");
        user.setPassword("dsdsdasd");
        expectedId = 4L;
    }

    @Test
    public void findAllShouldReturnAllUsers() {
        List<User> users = userRepository.findAll();
        Assertions.assertFalse(users.isEmpty());
    }

    @Test
    public void findByIdShouldReturnOptionalUserWhenIdExists() {
        Optional<User> user = userRepository.findById(existingId);
        Assertions.assertTrue(user.isPresent());
    }

    @Test
    public void findByIdShouldReturnOptionalUserNullWhenIdDoesNotExist() {
        Optional<User> user = userRepository.findById(nonExistingId);
        Assertions.assertFalse(user.isPresent());
    }

    @Test
    public void searchUserSearchUserAndRolesByEmailShouldReturnListOfUserDetailsProjectionWhenEmailExists() {
        List<UserDetailsProjection> userDetails = userRepository.searchUserAndRolesByEmail(existingEmail);
        Assertions.assertFalse(userDetails.isEmpty());
    }

    @Test
    public void searchUserSearchUserAndRolesByEmailShouldReturnListOfUserDetailsProjectionEmptyWhenEmailDoesNotExist() {
        List<UserDetailsProjection> userDetails = userRepository.searchUserAndRolesByEmail(nonExistingEmail);
        Assertions.assertTrue(userDetails.isEmpty());
    }

    @Test
    public void findByEmailShouldReturnOptionalUserWhenEmailExists() {
        Optional<User> user = userRepository.findByEmail(existingEmail);
        Assertions.assertTrue(user.isPresent());
    }

    @Test
    public void findByEmailShouldReturnOptionalUserNullWhenEmailDoesNotExist() {
        Optional<User> user = userRepository.findByEmail(nonExistingEmail);
        Assertions.assertFalse(user.isPresent());
    }

    @Test
    public void saveShouldPersistWhenUserIsSaved() {
        user=userRepository.save(user);
        Assertions.assertEquals(userRepository.count(), expectedCountTotal);
        Assertions.assertEquals(user.getId(), expectedId);

    }





}
