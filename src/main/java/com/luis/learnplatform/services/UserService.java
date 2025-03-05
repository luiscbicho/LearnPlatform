package com.luis.learnplatform.services;

import com.luis.learnplatform.entities.DTO.UserDTO;
import com.luis.learnplatform.entities.User;
import com.luis.learnplatform.repositories.UserRepository;

import com.luis.learnplatform.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        List<User> users = repository.findAll();
        List<UserDTO> usersDTO=users.stream().map(user -> new UserDTO(user)).collect(Collectors.toList());
        return usersDTO;
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = repository.findById(id).orElseThrow(()->new ResourceNotFoundException(id));
        return new UserDTO(user);
    }
}
