package com.luis.learnplatform.entities.DTO;

import com.luis.learnplatform.entities.Role;

public class RoleDTO {

    private String authority;

    public RoleDTO() {}
    public RoleDTO(String authority) {
        this.authority = authority;
    }
    public RoleDTO(Role role) {
        authority = role.getAuthority();
    }


    public String getAuthority() {
        return authority;
    }
}
