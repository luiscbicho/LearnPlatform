package com.luis.learnplatform.entities.DTO;

import com.luis.learnplatform.entities.User;

public class UserInsertDTO extends UserDTO {

    private String password;

    public UserInsertDTO() {
        super();
    }

    public UserInsertDTO(Long id, String name, String email, String password) {
        super(id, name, email);
        this.password = password;
    }

    public UserInsertDTO(User user) {
        super(user);
        this.password = user.getPassword();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
