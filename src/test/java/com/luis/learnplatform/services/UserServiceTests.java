package com.luis.learnplatform.services;


import com.luis.learnplatform.entities.DTO.RoleDTO;
import com.luis.learnplatform.entities.DTO.UserDTO;
import com.luis.learnplatform.entities.DTO.UserInsertDTO;
import com.luis.learnplatform.entities.Role;
import com.luis.learnplatform.entities.User;
import com.luis.learnplatform.entities.projections.UserDetailsProjection;
import com.luis.learnplatform.factories.UserFactory;
import com.luis.learnplatform.repositories.RoleRepository;
import com.luis.learnplatform.repositories.UserRepository;

import com.luis.learnplatform.services.exceptions.DatabaseException;
import com.luis.learnplatform.services.exceptions.ForbiddenException;
import com.luis.learnplatform.services.exceptions.ResourceNotFoundException;
import com.luis.learnplatform.util.CustomUserUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CustomUserUtil customUserUtil;

    private Long existingUserId, nonExistingUserId, existingRoleId, nonExistingRoleId, existingUserAdminId, dependentUserId;
    private String existingName, nonExistingName, existingUserEmail, nonExistingUserEmail;
    private PageImpl<User> users;
    private User user,userAdmin;
    private UserDTO userDTO;
    private UserInsertDTO userInsertDTO;
    private Role role;
    private Long repositoryCountExcpected;
    private List<UserDetailsProjection> userDetailsProjections;


    @BeforeEach
    void setUp() {

        userDTO = new UserDTO();
        user = UserFactory.createUser();
        userAdmin = UserFactory.createUserAdmin();
        userInsertDTO = new UserInsertDTO(user);
        users=new PageImpl<>(Arrays.asList(user));
        existingName=user.getName();
        nonExistingName="xxxx";
        existingUserId=user.getId();
        nonExistingUserId=10L;
        role=new Role(1L,"ROLE_STUDENT");
        existingRoleId=role.getId();
        dependentUserId=4L;
        nonExistingRoleId=10L;
        existingUserAdminId=userAdmin.getId();
        repositoryCountExcpected=0L;
        existingUserEmail=user.getEmail();
        nonExistingUserEmail="joanita@gmail.com";
        userDetailsProjections=new ArrayList<>();
        UserDetailsProjection projection = Mockito.mock(UserDetailsProjection.class);


        Mockito.when(repository.findAll(any(Pageable.class))).thenReturn(users);
        Mockito.when(repository.findByNameContainingIgnoreCase(eq(existingName),any(Pageable.class))).thenReturn(users);
        Mockito.when(repository.findByNameContainingIgnoreCase(eq(nonExistingName),any(Pageable.class))).thenReturn(Page.empty());
        Mockito.when(repository.findById(existingUserId)).thenReturn(Optional.of(user));
        Mockito.when(repository.findById(nonExistingUserId)).thenReturn(Optional.empty());
        Mockito.when(repository.save(any(User.class))).thenReturn(user);
        Mockito.when(roleRepository.getReferenceById(existingRoleId)).thenReturn(role);
        Mockito.when(roleRepository.getReferenceById(nonExistingRoleId)).thenThrow(EntityNotFoundException.class);
        Mockito.when(repository.getReferenceById(existingUserId)).thenReturn(user);
        Mockito.when(repository.getReferenceById(nonExistingUserId)).thenThrow(ResourceNotFoundException.class);
        Mockito.when(repository.existsById(existingUserId)).thenReturn(true);
        Mockito.when(repository.existsById(dependentUserId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingUserId)).thenReturn(false);
        Mockito.doNothing().when(repository).deleteById(existingUserId);
        Mockito.doThrow(DatabaseException.class).when(repository).deleteById(dependentUserId);
        Mockito.when(passwordEncoder.encode(Mockito.any(CharSequence.class))).thenReturn("hashedPassword");
        Mockito.when(projection.getPassword()).thenReturn("teste");
        Mockito.when(projection.getUsername()).thenReturn("luis@luis.com");
        Mockito.when(projection.getRoleId()).thenReturn(1L);
        Mockito.when(projection.getAuthority()).thenReturn("ROLE_STUDENT");

        userDetailsProjections.add(projection);

        Mockito.when(repository.searchUserAndRolesByEmail(existingUserEmail)).thenReturn(userDetailsProjections);
        Mockito.when(repository.searchUserAndRolesByEmail(nonExistingUserEmail)).thenThrow(UsernameNotFoundException.class);

        Mockito.when(repository.findByEmail(existingUserEmail)).thenReturn(Optional.of(user));
        Mockito.when(repository.findByEmail(nonExistingUserEmail)).thenReturn(Optional.empty());

    }

    @Test
    public void findAllShouldReturnAllUsersWhenNameIsNullWithPagination() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<UserDTO> dtos=service.findAll(null,pageable);
        Assertions.assertNotNull(dtos);
        Assertions.assertEquals(1, dtos.getContent().size());
    }

    @Test
    public void findAllShouldReturnSpecificUsersByExistingNameWithPagination() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<UserDTO> dtos=service.findAll(existingName,pageable);

        Assertions.assertNotNull(dtos);
        Assertions.assertEquals(1, dtos.getContent().size());
        Assertions.assertEquals(existingName,dtos.getContent().get(0).getName());

    }

    @Test
    public void findAllShouldReturnEmptyPageWhenNameDoesNotExistWithPagination() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<UserDTO> dtos=service.findAll(nonExistingName,pageable);
        Assertions.assertTrue(dtos.isEmpty());
    }

    @Test
    public void findByIdShouldReturnUserDTOWhenIdExists(){
        userDTO  = service.findById(existingUserId);
        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(existingUserId,userDTO.getId());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, ()-> service.findById(nonExistingUserId));
    }

    @Test
    public void insertShouldPersistNewUserAndReturnUserDTO(){
        userInsertDTO.setId(null);
        userDTO = service.insert(userInsertDTO);
        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(1L,userDTO.getId());
    }

    @Test
    public void insertShouldThrowResourceNotFoundExceptionWhenRoleIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, ()-> {
            User newUser = new User();
            userInsertDTO.getRoles().clear();
            userInsertDTO.getRoles().add(new RoleDTO(nonExistingRoleId," "));
            UserService userServiceSpy = Mockito.spy(service);
            Mockito.doThrow(ResourceNotFoundException.class).when(userServiceSpy).update(new User(), userInsertDTO);
            service.insert(userInsertDTO);
        });
    }

    @Test
    public void updateShouldUpdateExistingUserAndReturnUserDTOWhenIdExistsAndAdminLogged() throws ParseException {
        userInsertDTO.setId(null);
        userInsertDTO.setName("Joel");
        userInsertDTO.setEmail("joel@email.com");
        userInsertDTO.setPassword("novapassword");
        userInsertDTO.getRoles().clear();
        userInsertDTO.getRoles().add(new RoleDTO(existingRoleId,"ROLE_STUDENT"));

        Mockito.when(customUserUtil.getLoggedUser()).thenReturn(userAdmin.getEmail());
        Mockito.when(repository.findByEmail(any(String.class))).thenReturn(Optional.of(userAdmin));
        UserService userServiceSpy = Mockito.spy(service);
        Mockito.doCallRealMethod().when(userServiceSpy).validateSelfOrAdmin(existingUserAdminId);
        Mockito.when(userServiceSpy.authenticated()).thenReturn(userAdmin);

        userDTO = service.update(existingUserId,userInsertDTO);

        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(existingUserId,userDTO.getId());
        Assertions.assertTrue(userDTO.getName().equals(userInsertDTO.getName()));
        Assertions.assertTrue(userDTO.getEmail().equals(userInsertDTO.getEmail()));
        for(RoleDTO x: userDTO.getRoles()){
            for(RoleDTO y: userInsertDTO.getRoles() ){
                Assertions.assertTrue(x.getId().equals(y.getId()));
                Assertions.assertTrue(x.getAuthority().equals(y.getAuthority()));
            }
        }

    }

    @Test
    public void updateShouldUpdateExistingUserAndReturnUserDTOWhenIdExistsAndSelfUserLogged() throws ParseException {
        userInsertDTO.setId(null);
        userInsertDTO.setName("Joel");
        userInsertDTO.setEmail("joel@email.com");
        userInsertDTO.setPassword("novapassword");
        userInsertDTO.getRoles().clear();
        userInsertDTO.getRoles().add(new RoleDTO(existingRoleId,"ROLE_STUDENT"));

        Mockito.when(customUserUtil.getLoggedUser()).thenReturn(user.getEmail());
        Mockito.when(repository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
        UserService userServiceSpy = Mockito.spy(service);
        Mockito.doCallRealMethod().when(userServiceSpy).validateSelfOrAdmin(user.getId());
        Mockito.when(userServiceSpy.authenticated()).thenReturn(user);

        userDTO = service.update(user.getId(),userInsertDTO);

        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(existingUserId,userDTO.getId());
        Assertions.assertTrue(userDTO.getName().equals(userInsertDTO.getName()));
        Assertions.assertTrue(userDTO.getEmail().equals(userInsertDTO.getEmail()));
        for(RoleDTO x: userDTO.getRoles()){
            for(RoleDTO y: userInsertDTO.getRoles() ){
                Assertions.assertTrue(x.getId().equals(y.getId()));
                Assertions.assertTrue(x.getAuthority().equals(y.getAuthority()));
            }
        }

    }

    @Test
    public void updateShouldThrowForbiddenExceptionWhenUserIdExistsAndOtherClientLogged() throws ParseException {
        Assertions.assertThrows(ForbiddenException.class, ()-> {
            userInsertDTO.setId(null);
            userInsertDTO.setName("Joel");
            userInsertDTO.setEmail("joel@email.com");
            userInsertDTO.setPassword("novapassword");
            userInsertDTO.getRoles().clear();
            userInsertDTO.getRoles().add(new RoleDTO(existingRoleId, "ROLE_STUDENT"));

            Mockito.when(customUserUtil.getLoggedUser()).thenReturn(user.getEmail());
            Mockito.when(repository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
            UserService userServiceSpy = Mockito.spy(service);
            Mockito.doCallRealMethod().when(userServiceSpy).validateSelfOrAdmin(user.getId());
            Mockito.when(userServiceSpy.authenticated()).thenReturn(user);

            userDTO = service.update(userAdmin.getId(), userInsertDTO);
        });
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenUserIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, ()-> {
            userInsertDTO.setId(null);
            userInsertDTO.setName("Joel");
            userInsertDTO.setEmail("joel@email.com");
            userInsertDTO.setPassword("novapassword");
            userInsertDTO.getRoles().clear();
            userInsertDTO.getRoles().add(new RoleDTO(existingRoleId, "ROLE_STUDENT"));

            Mockito.when(customUserUtil.getLoggedUser()).thenReturn(userAdmin.getEmail());
            Mockito.when(repository.findByEmail(any(String.class))).thenReturn(Optional.of(userAdmin));
            UserService userServiceSpy = Mockito.spy(service);
            Mockito.doCallRealMethod().when(userServiceSpy).validateSelfOrAdmin(userAdmin.getId());
            Mockito.when(userServiceSpy.authenticated()).thenReturn(userAdmin);

            userDTO = service.update(nonExistingUserId, userInsertDTO);
        });
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenRoleIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, ()-> {
            userInsertDTO.setId(null);
            userInsertDTO.setName("Joel");
            userInsertDTO.setEmail("joel@email.com");
            userInsertDTO.setPassword("novapassword");
            userInsertDTO.getRoles().clear();
            userInsertDTO.getRoles().add(new RoleDTO(nonExistingRoleId, " "));

            Mockito.when(customUserUtil.getLoggedUser()).thenReturn(userAdmin.getEmail());
            Mockito.when(repository.findByEmail(any(String.class))).thenReturn(Optional.of(userAdmin));
            UserService userServiceSpy = Mockito.spy(service);
            Mockito.doCallRealMethod().when(userServiceSpy).validateSelfOrAdmin(userAdmin.getId());
            Mockito.when(userServiceSpy.authenticated()).thenReturn(userAdmin);

            userDTO = service.update(nonExistingUserId, userInsertDTO);
        });
    }

    @Test
    public void deleteShouldDeleteUserWhenUserIdExists(){
        service.delete(existingUserId);
        Assertions.assertEquals(repositoryCountExcpected,repository.count());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenUserIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, ()-> {
            service.delete(nonExistingUserId);
        });
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentUserId(){
        Assertions.assertThrows(DatabaseException.class, ()-> {
            service.delete(dependentUserId);
        });
    }

    @Test
    public void loadUserByUsernameShouldReturnUserWhenUsernameExists(){
        UserDetails newUser = service.loadUserByUsername(existingUserEmail);
        for(UserDetailsProjection x:userDetailsProjections){
            Assertions.assertEquals(x.getUsername(), newUser.getUsername());
            Assertions.assertEquals(x.getPassword(), newUser.getPassword());
            for(GrantedAuthority y:newUser.getAuthorities()){
                Assertions.assertEquals(x.getAuthority(), y.getAuthority());
            }
        }
    }

    @Test
    public void loadUserByEmailShouldThrowUsernameNotFoundExceptionWhenUsernameDoesNotExist(){
        Assertions.assertThrows(UsernameNotFoundException.class, ()-> {
            service.loadUserByUsername(nonExistingUserEmail);
        });
    }

    @Test
    public void authenticatedShouldReturnUserWhenUsernameExists() throws ParseException {

        Mockito.when(customUserUtil.getLoggedUser()).thenReturn(existingUserEmail);
        User newUser = service.authenticated();

        Assertions.assertNotNull(newUser);
        Assertions.assertEquals(existingUserEmail, newUser.getEmail());

    }

    @Test
    public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUsernameDoesNotExist(){
        Assertions.assertThrows(UsernameNotFoundException.class, ()-> {
            Mockito.when(customUserUtil.getLoggedUser()).thenReturn(nonExistingUserEmail);
            service.authenticated();
        });
    }

    @Test
    public void getMeShouldReturnUserLogged() throws ParseException {
        Mockito.when(customUserUtil.getLoggedUser()).thenReturn(user.getEmail());
        UserService userServiceSpy = Mockito.spy(service);
        Mockito.when(userServiceSpy.authenticated()).thenReturn(user);
        userDTO = service.getMe();
        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(user.getEmail(), userDTO.getEmail());
        Assertions.assertEquals(user.getName(), userDTO.getName());
        Assertions.assertEquals(user.getId(), userDTO.getId());
    }

    @Test
    public void validateSelfOrAdminShouldPassWhenAdmin() throws ParseException {
        Assertions.assertDoesNotThrow(() -> {
            Mockito.when(customUserUtil.getLoggedUser()).thenReturn(userAdmin.getEmail());
            Mockito.when(repository.findByEmail(userAdmin.getEmail())).thenReturn(Optional.of(userAdmin));
            UserService userServiceSpy = Mockito.spy(service);
            Mockito.when(userServiceSpy.authenticated()).thenReturn(userAdmin);
            service.validateSelfOrAdmin(userAdmin.getId());
        });
    }

    @Test
    public void validateSelfOrAdminShouldPassWhenSelfUser() throws ParseException {
        Assertions.assertDoesNotThrow(() -> {
            Mockito.when(customUserUtil.getLoggedUser()).thenReturn(user.getEmail());
            UserService userServiceSpy = Mockito.spy(service);
            Mockito.when(userServiceSpy.authenticated()).thenReturn(user);
            service.validateSelfOrAdmin(user.getId());
        });
    }

    @Test
    public void validateSelfOrAdminShouldThrowForbiddenExceptionWhenOtherUser() throws ParseException {
        Assertions.assertThrows(ForbiddenException.class, () -> {
            Mockito.when(customUserUtil.getLoggedUser()).thenReturn(user.getEmail());
            UserService userServiceSpy = Mockito.spy(service);
            Mockito.when(userServiceSpy.authenticated()).thenReturn(user);
            service.validateSelfOrAdmin(userAdmin.getId());
        });
    }

}
