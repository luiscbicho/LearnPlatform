package com.luis.learnplatform.services;

import com.luis.learnplatform.entities.DTO.RoleDTO;
import com.luis.learnplatform.entities.DTO.UserDTO;
import com.luis.learnplatform.entities.DTO.UserInsertDTO;
import com.luis.learnplatform.entities.Role;
import com.luis.learnplatform.entities.User;
import com.luis.learnplatform.repositories.RoleRepository;
import com.luis.learnplatform.repositories.UserRepository;

import com.luis.learnplatform.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        List<User> users = repository.findAll();
        List<UserDTO> usersDTO=users.stream().map(user -> new UserDTO(user)).collect(Collectors.toList());
        return usersDTO;
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = repository.findById(id).orElseThrow(()->new ResourceNotFoundException());
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        for(RoleDTO x : dto.getRoles()){
            Role role = roleRepository.findByName(x.getAuthority()).orElseThrow(()->new ResourceNotFoundException());
            user.getRoles().add(role);
        }
        repository.save(user);
        return new UserDTO(user);
    }

}
