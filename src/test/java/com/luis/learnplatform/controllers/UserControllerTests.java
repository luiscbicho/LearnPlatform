package com.luis.learnplatform.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luis.learnplatform.entities.DTO.UserDTO;
import com.luis.learnplatform.entities.DTO.UserInsertDTO;
import com.luis.learnplatform.entities.User;
import com.luis.learnplatform.factories.UserFactory;
import com.luis.learnplatform.services.UserService;
import com.luis.learnplatform.services.exceptions.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;


import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import java.util.List;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class UserControllerTests {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingUserId, nonExistingUserId;
    private User user,userAdmin;
    private UserDTO userDTO, userDTOAdmin, userInsertDTO;
    private PageImpl<UserDTO> dtos;

    @BeforeEach
    void setUp() {
        user = UserFactory.createUser();
        userAdmin = UserFactory.createUserAdmin();
        userInsertDTO = new UserInsertDTO(user);
        userDTOAdmin = new UserDTO(userAdmin);
        userDTO = new UserDTO(user);
        dtos = new PageImpl<>(List.of(userDTO, userDTOAdmin));
        existingUserId = user.getId();
        nonExistingUserId = 10L;


        Mockito.when(userService.findAll(any(),any(Pageable.class))).thenReturn(dtos);
        Mockito.when(userService.findById(existingUserId)).thenReturn(userDTO);
        Mockito.when(userService.findById(nonExistingUserId)).thenThrow(ResourceNotFoundException.class);
        Mockito.doNothing().when(userService).delete(existingUserId);
        Mockito.doThrow(ResourceNotFoundException.class).when(userService).delete(nonExistingUserId);
        Mockito.when(userService.getMe()).thenReturn(userDTO);
        Mockito.when(userService.insert(any())).thenReturn(userDTO);
        Mockito.when(userService.update(eq(existingUserId),any())).thenReturn(userDTO);
        Mockito.when(userService.update(eq(nonExistingUserId),any())).thenThrow(ResourceNotFoundException.class);

    }

    @Test
    public void findAllShouldReturnPageOfUserDTOs() throws Exception {
        ResultActions result=
                mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.totalElements").value(2));
        result.andExpect(jsonPath("$.content[0].id").exists());
        result.andExpect(jsonPath("$.content[1].id").exists());

    }

    @Test
    public void findAllShouldReturnPageWithSpecificUsersByName() throws Exception {

        Mockito.when(userService.findAll(eq(userDTO.getName()),any(Pageable.class)))
                .thenAnswer(invocation -> {
                    String name = invocation.getArgument(0);
                    Pageable pageable = invocation.getArgument(1);

                    List<UserDTO> filteredList = dtos.getContent().stream().filter(u -> u.getName().equalsIgnoreCase(name)).toList();

                    return new PageImpl<>(filteredList,pageable,filteredList.size());
                });
        ResultActions result=
                mockMvc.perform(get("/users?name={name}", userDTO.getName())
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.totalElements").value(1));
        result.andExpect(jsonPath("$.content[0].name").value(userDTO.getName()));
    }

    @Test
    public void findAllShouldReturnSortedPageOfUserDTOsByName() throws Exception {
        Mockito.when(userService.findAll(any(), any(Pageable.class)))
                .thenAnswer(invocation -> {
                    String name = invocation.getArgument(0);
                    Pageable pageable = invocation.getArgument(1);

                    // Filtrando pelo nome (se for passado)
                    List<UserDTO> filteredList = dtos.getContent().stream()
                            .filter(user -> name == null || user.getName().equalsIgnoreCase(name))
                            .toList();

                    // Ordenação dinâmica conforme o Pageable
                    List<UserDTO> sortedList = filteredList.stream()
                            .sorted((u1, u2) -> {
                                if (pageable.getSort().getOrderFor("name") != null) {
                                    return pageable.getSort().getOrderFor("name").isAscending() ?
                                            u1.getName().compareToIgnoreCase(u2.getName()) :
                                            u2.getName().compareToIgnoreCase(u1.getName());
                                }
                                return 0;
                            })
                            .toList();

                    return new PageImpl<>(sortedList, pageable, sortedList.size());
                });

        ResultActions result=
                mockMvc.perform(get("/users?sort=name,asc")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].name").value("Luis"));
        result.andExpect(jsonPath("$.content[1].name").value("Pedro"));
    }

    @Test
    public void findAllShouldReturnSortedPageOfUserDTOsById() throws Exception {
        Mockito.when(userService.findAll(any(), any(Pageable.class)))
                .thenAnswer(invocation -> {
                    String name = invocation.getArgument(0);
                    Pageable pageable = invocation.getArgument(1);

                    // Filtrando pelo nome (se for passado)
                    List<UserDTO> filteredList = dtos.getContent().stream()
                            .filter(user -> name == null || user.getName().equalsIgnoreCase(name))
                            .toList();

                    // Ordenação dinâmica conforme o Pageable
                    List<UserDTO> sortedList = filteredList.stream()
                            .sorted((u1, u2) -> {
                                if (pageable.getSort().getOrderFor("id") != null) {
                                    return pageable.getSort().getOrderFor("id").isAscending() ?
                                            Long.compare(u1.getId(), u2.getId()) :
                                            Long.compare(u2.getId(), u1.getId());
                                }
                                return 0;
                            })
                            .toList();

                    return new PageImpl<>(sortedList, pageable, sortedList.size());
                });

        ResultActions result=
                mockMvc.perform(get("/users?sort=id,asc")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].id").value(1L));
        result.andExpect(jsonPath("$.content[1].id").value(2L));
    }

    @Test
    public void findByIdShouldReturnUserDTOWhenIdExists() throws Exception {

        ResultActions result=
                mockMvc.perform(get("/users/{id}", existingUserId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.email").exists());
        result.andExpect(jsonPath("$.id").value(user.getId()));
        result.andExpect(jsonPath("$.name").value(user.getName()));
        result.andExpect(jsonPath("$.email").value(user.getEmail()));

    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {
        ResultActions result=
                mockMvc.perform(get("/users/{id}", nonExistingUserId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void getMeShouldReturnUserDTO() throws Exception {
        ResultActions result=
                mockMvc.perform(get("/users/me").accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.email").exists());
    }

    @Test
    public void insertShouldReturnCreatedUserDTOWhenUserIsValid() throws Exception {

        String json = objectMapper.writeValueAsString(userInsertDTO);
        ResultActions result =
            mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.email").exists());
        result.andExpect(jsonPath("$.name").value(userInsertDTO.getName()));
        result.andExpect(jsonPath("$.email").value(userInsertDTO.getEmail()));
    }

    @Test
    public void updateShouldReturnUpdatedUserDTOWhenUserValidAndUserIdExists() throws Exception {
        String json = objectMapper.writeValueAsString(userInsertDTO);
        ResultActions result =
            mockMvc.perform(put("/users/{id}", existingUserId).content(json).contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.id").value(existingUserId));
        result.andExpect(jsonPath("$.name").value(userInsertDTO.getName()));
        result.andExpect(jsonPath("$.email").value(userInsertDTO.getEmail()));
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenUserValidAndUserIdDoesNotExist() throws Exception {
        String json = objectMapper.writeValueAsString(userInsertDTO);
        ResultActions result =
                mockMvc.perform(put("/users/{id}", nonExistingUserId).content(json).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldThrowNotFoundWhenIdDoesNotExist() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException()).when(userService).delete(nonExistingUserId);

        ResultActions result =
                mockMvc.perform(delete("/users/{id}", nonExistingUserId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }



    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        ResultActions result=
                mockMvc.perform(delete("/users/{id}", existingUserId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
        result.andExpect(jsonPath("$.id").doesNotExist());
    }


}
