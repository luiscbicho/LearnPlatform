package com.luis.learnplatform.entities.DTO;

import com.luis.learnplatform.entities.Role;
import com.luis.learnplatform.entities.User;

import java.util.HashSet;
import java.util.Set;

public class UserDTO {

    private Long id;
    private String name;
    private String email;

    private Set<RoleDTO> roles=new HashSet<>();

    public UserDTO() {
    }

    public UserDTO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public UserDTO(User user) {
        id = user.getId();
        name=user.getName();
        email=user.getEmail();
        for(Role role : user.getRoles()) {
            roles.add(new RoleDTO(role));
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoleDTO> getRoles() {
        return roles;
    }

}
