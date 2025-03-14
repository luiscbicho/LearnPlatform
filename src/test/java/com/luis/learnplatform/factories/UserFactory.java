package com.luis.learnplatform.factories;

import com.luis.learnplatform.entities.Role;
import com.luis.learnplatform.entities.User;

public class UserFactory {

    public static User createUser() {
        User user = new User(1L,"Luis", "luis@luis.com", "teste");
        user.getRoles().add(new Role(1L,"ROLE_STUDENT"));
        return user;
    }

    public static User createUserAdmin() {
        User user = new User(2L,"Pedro", "pedro@pedro.com", "teste");
        user.getRoles().add(new Role(3L,"ROLE_ADMIN"));
        return user;
    }

}
