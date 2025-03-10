package com.luis.learnplatform.services;

import com.luis.learnplatform.entities.DTO.RoleDTO;
import com.luis.learnplatform.entities.DTO.UserDTO;
import com.luis.learnplatform.entities.DTO.UserInsertDTO;
import com.luis.learnplatform.entities.Role;
import com.luis.learnplatform.entities.User;
import com.luis.learnplatform.entities.projections.UserDetailsProjection;
import com.luis.learnplatform.repositories.RoleRepository;
import com.luis.learnplatform.repositories.UserRepository;

import com.luis.learnplatform.services.exceptions.DatabaseException;
import com.luis.learnplatform.services.exceptions.ResourceNotFoundException;
import com.luis.learnplatform.util.CustomUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserUtil customUserUtil;

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
        update(user,dto);
        user = repository.save(user);
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO update(Long id, UserInsertDTO dto) {
        User user = repository.findById(id).orElseThrow(()->new ResourceNotFoundException());
        user.getRoles().clear();
        update(user, dto);
        user = repository.save(user);
        return new UserDTO(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException();
        }
        try{
            repository.deleteById(id);
        }
        catch(DataIntegrityViolationException e){
            throw new DatabaseException(e.getMessage());
        }
    }

    private void update(User user, UserInsertDTO dto) {
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        for(RoleDTO x : dto.getRoles()){
            Role role = roleRepository.getReferenceById(x.getId());
            user.getRoles().add(role);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
        if (result.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        User user = new User();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection projection : result) {
            user.getRoles().add(new Role(projection.getRoleId(), projection.getAuthority()));
        }
        return user;
    }

    protected User authenticated() {
        try {
            String username = customUserUtil.getLoggedUser();
            return repository.findByEmail(username).get();
        } catch (Exception e) {
            throw new UsernameNotFoundException("Email not found");
        }
    }

    @Transactional(readOnly = true)
    public UserDTO getMe() {
        User user = authenticated();
        return new UserDTO(user);
    }
}
