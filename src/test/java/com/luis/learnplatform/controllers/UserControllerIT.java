package com.luis.learnplatform.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luis.learnplatform.entities.DTO.UserInsertDTO;
import com.luis.learnplatform.factories.UserFactory;
import com.luis.learnplatform.repositories.UserRepository;
import com.luis.learnplatform.util.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private UserRepository repository;

    @Autowired
    private ObjectMapper objectMapper;


    private Long existingUserId, nonExistingUserId, loggedUserId, newId, selfUserId, dependentId;
    private String tokenAdmin, tokenStudent, tokenInstructor, existingUserName;
    private UserInsertDTO userInsertDTO;


    @BeforeEach
    void setUp() throws Exception {

        existingUserId = 4L;
        existingUserName = repository.getReferenceById(existingUserId).getName();
        nonExistingUserId = 100L;
        loggedUserId = 1L;
        selfUserId = 1L;
        dependentId = 2L;
        newId = 5L;
        tokenAdmin = tokenUtil.obtainAccessToken(mockMvc,"maria@gmail.com","12345678");
        tokenStudent = tokenUtil.obtainAccessToken(mockMvc,"alex@gmail.com","12345678");
        tokenInstructor = tokenUtil.obtainAccessToken(mockMvc,"bob@gmail.com","12345678");
        userInsertDTO = new UserInsertDTO(UserFactory.createUser());
        userInsertDTO.setId(null);


    }

    @Test
    public void findAllShouldReturnPageOfUserDTOs() throws Exception {
        ResultActions result=
                mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.totalElements").value(4));
    }

    @Test
    public void findAllShouldReturnPageWithSpecificUsersByName() throws Exception {
        ResultActions result=
                mockMvc.perform(get("/users?name={name}", existingUserName)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.totalElements").value(1));
        result.andExpect(jsonPath("$.content[0].name").value(existingUserName));
    }

    @Test
    public void findAllShouldReturnPageSortedByName() throws Exception {

        ResultActions result=
                mockMvc.perform(get("/users?sort={x},asc", "name")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].name").value("Alex Brown"));
        result.andExpect(jsonPath("$.content[1].name").value("Bob Brown"));
        result.andExpect(jsonPath("$.content[2].name").value("Joana Santos"));
        result.andExpect(jsonPath("$.content[3].name").value("Maria Green"));

    }

    @Test
    public void findAllShouldReturnPageSortedById() throws Exception {

        ResultActions result=
                mockMvc.perform(get("/users?sort={x},asc", "id")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].name").value("Alex Brown"));
        result.andExpect(jsonPath("$.content[1].name").value("Bob Brown"));
        result.andExpect(jsonPath("$.content[2].name").value("Maria Green"));
        result.andExpect(jsonPath("$.content[3].name").value("Joana Santos"));

    }



    @Test
    public void findByIdShouldReturnUserDTOWhenIdExists() throws Exception {

        ResultActions result =
                mockMvc.perform(get("/users/{id}",existingUserId).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        result.andExpect(jsonPath("$.id").value(existingUserId));
        result.andExpect(jsonPath("$.name").value("Joana Santos"));
        result.andExpect(jsonPath("$.email").value("joana@gmail.com"));


    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/users/{id}",nonExistingUserId).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test
    public void getMeShouldReturnUserDTO() throws Exception {

        Jwt jwt = Jwt.withTokenValue("mockJwt")
                .header("alg", "Hs256")
                .claim("username", repository.getReferenceById(loggedUserId).getEmail())
                .build();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(jwt,null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResultActions result =
                mockMvc.perform(get("/users/me").with(jwt().jwt(jwt)).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        result.andExpect(jsonPath("$.id").value(loggedUserId));
        result.andExpect(jsonPath("$.name").value(repository.getReferenceById(loggedUserId).getName()));
        result.andExpect(jsonPath("$.email").value(repository.getReferenceById(loggedUserId).getEmail()));
    }

    @Test
    public void insertShouldReturnCreatedUserDTO() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(userInsertDTO);

        ResultActions result =
                mockMvc.perform(post("/users").content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$").exists());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.id").value(newId));
        result.andExpect(jsonPath("$.name").value(userInsertDTO.getName()));
        result.andExpect(jsonPath("$.email").value(userInsertDTO.getEmail()));


    }

    @Test
    public void updateShouldReturnUpdatedUserDTOWhenIdExistsAndSelfUser() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(userInsertDTO);

        ResultActions result =
                mockMvc.perform(put("/users/{id}",selfUserId)
                        .header("Authorization", "Bearer " + tokenStudent)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        result.andExpect(jsonPath("$.id").value(selfUserId));
        result.andExpect(jsonPath("$.name").value(userInsertDTO.getName()));
        result.andExpect(jsonPath("$.email").value(userInsertDTO.getEmail()));
    }

    @Test
    public void updateShouldReturnUpdatedUserDTOWhenIdExistsAndAdminUser() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(userInsertDTO);

        ResultActions result =
                mockMvc.perform(put("/users/{id}", existingUserId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        result.andExpect(jsonPath("$.id").value(existingUserId));
        result.andExpect(jsonPath("$.name").value(userInsertDTO.getName()));
        result.andExpect(jsonPath("$.email").value(userInsertDTO.getEmail()));
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(userInsertDTO);

        ResultActions result =
                mockMvc.perform(put("/users/{id}", nonExistingUserId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());

    }

    @Test
    public void deleteShouldReturnNoContentAndDeleteUserWhenIdExistsAndAdminUser() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/users/{id}", existingUserId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/users/{id}", nonExistingUserId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnBadRequestWhenDependentId() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/users/{id}", dependentId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());
    }
}
