package com.luis.learnplatform.services;

import com.luis.learnplatform.entities.DTO.UserDTO;
import com.luis.learnplatform.entities.DTO.UserInsertDTO;
import com.luis.learnplatform.entities.User;
import com.luis.learnplatform.factories.UserFactory;
import com.luis.learnplatform.repositories.UserRepository;
import com.luis.learnplatform.services.exceptions.DatabaseException;
import com.luis.learnplatform.services.exceptions.ForbiddenException;
import com.luis.learnplatform.services.exceptions.ResourceNotFoundException;
import com.luis.learnplatform.util.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class UserServiceIT {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repository;

    @Autowired
    private CustomUserUtil customUserUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long existingUserId, nonExistingUserId, dependentId, loggedUserId, userAdminId, selfUserId;
    private String existingUserEmail, nonExistingUserEmail;
    private UserDTO userDTO;
    private UserInsertDTO userInsertDTO;


    @BeforeEach
    void setUp() {

        userInsertDTO = new UserInsertDTO(UserFactory.createUser());
        userInsertDTO.setId(null);
        existingUserId = 4L;
        selfUserId = 4L;
        userAdminId = 3L;
        loggedUserId = 3L;
        nonExistingUserId = 100L;
        dependentId = 2L;
        existingUserEmail = repository.getReferenceById(loggedUserId).getEmail();
        nonExistingUserEmail = "gato@gmail.com";

        // Criando um JWT mockado com o nome de usuário (email) correto
        Jwt jwt = Jwt.withTokenValue("mockJwt")
                .header("alg", "Hs256")
                .claim("username", repository.getReferenceById(loggedUserId).getEmail())
                .build();

        // Criando um Authentication com o Jwt mockado
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(jwt,null);

        // Configurando o SecurityContext com a autenticação baseada no JWT
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    @Test
    public void findByIdShouldReturnUserDTOWhenIdExists(){

        userDTO = service.findById(existingUserId);

        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals("Joana Santos", userDTO.getName());
        Assertions.assertEquals("joana@gmail.com", userDTO.getEmail());

    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){

        Assertions.assertThrows(ResourceNotFoundException.class, ()-> service.findById(nonExistingUserId));
    }

    @Test
    public void insertShouldPersistNewUserAndReturnUserDTO(){

        userDTO = service.insert(userInsertDTO);

        Assertions.assertEquals(5L,userDTO.getId());
        Assertions.assertEquals(userInsertDTO.getName(),userDTO.getName());
        Assertions.assertEquals(userInsertDTO.getEmail(),userDTO.getEmail());

    }

    @Test
    public void updateShouldUpdateUserAndReturnUserDTOWhenIdExists(){

        userDTO = service.update(existingUserId,userInsertDTO);

        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(existingUserId,userDTO.getId());
        Assertions.assertEquals(userInsertDTO.getName(),userDTO.getName());
        Assertions.assertEquals(userInsertDTO.getEmail(),userDTO.getEmail());

    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, ()-> service.update(nonExistingUserId,userInsertDTO));
    }

    @Test
    public void deleteShouldDeleteUserWhenIdExists(){

        service.delete(existingUserId);

        Assertions.assertFalse(repository.existsById(existingUserId));
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingUserId));
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {

        Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependentId));
    }

    @Test
    public void loadUserByUsernameShouldReturnUserWhenEmailExists(){
        UserDetails user = service.loadUserByUsername(existingUserEmail);

        Assertions.assertNotNull(existingUserEmail,user.getUsername());
    }

    @Test
    public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenEmailDoesNotExist(){
        Assertions.assertThrows(UsernameNotFoundException.class, ()-> service.loadUserByUsername(nonExistingUserEmail));
    }

    @Test
    public void authenticatedShouldReturnUserWhenLoggedWithExistingEmail(){
        User user = service.authenticated();

        Assertions.assertNotNull(user);
    }

    @Test
    public void authenticatedShouldThrowUsernameNotFoundExceptionWhenNotLoggedWithExistingEmail(){

        Assertions.assertThrows(UsernameNotFoundException.class, ()-> {
            SecurityContextHolder.getContext().setAuthentication(null);
            service.authenticated();
        });

    }

    @Test
    public void getMeShouldReturnSelfUserDtoWhenLogged(){
        userDTO = service.getMe();

        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(loggedUserId,userDTO.getId());
    }

    @Test
    public void validateSelfOrAdminShouldPassWhenAdmin(){

        Jwt jwt = Jwt.withTokenValue("mockJwt")
                .header("alg", "Hs256")
                .claim("username", repository.getReferenceById(userAdminId).getEmail())
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(jwt,null);

        SecurityContextHolder.getContext().setAuthentication(authentication);


        Assertions.assertDoesNotThrow(() -> service.validateSelfOrAdmin(existingUserId));

    }

    @Test
    public void validateSelfOrAdminShouldPassWhenSelfUser(){

        Jwt jwt = Jwt.withTokenValue("mockJwt")
                .header("alg", "Hs256")
                .claim("username", repository.getReferenceById(selfUserId).getEmail())
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(jwt,null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Assertions.assertDoesNotThrow(() -> service.validateSelfOrAdmin(selfUserId));

    }

    @Test
    public void validateSelfOrAdminShouldThrowForbiddenExceptionWhenOtherUser(){

        Assertions.assertThrows(ForbiddenException.class, () -> {
            Jwt jwt = Jwt.withTokenValue("mockJwt")
                    .header("alg", "Hs256")
                    .claim("username", repository.getReferenceById(selfUserId).getEmail())
                    .build();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(jwt,null);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            service.validateSelfOrAdmin(userAdminId);
        });

        }

    @Test
    public void checkDependentUserDependencies() {
        User dependentUser = repository.getReferenceById(dependentId);
        System.out.println("Dependent user: " + dependentUser.getName());
        System.out.println("Roles: " + dependentUser.getRoles().size()); // Example for roles dependency
    }

}
