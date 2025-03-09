package com.luis.learnplatform.services;

import com.luis.learnplatform.entities.User;
import com.luis.learnplatform.services.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    public void validateSelfOrAdmin(Long userId) {
        User me = userService.authenticated();
        if (me.hasRole("ROLE_ADMIN")) {
            return;
        }
        if (!me.getId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to access this resource");
        }
    }

}