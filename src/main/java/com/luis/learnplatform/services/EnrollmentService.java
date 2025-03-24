package com.luis.learnplatform.services;

import com.luis.learnplatform.entities.DTO.EnrollmentDTO;
import com.luis.learnplatform.repositories.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository repository;

    @Autowired
    private UserService userService;


    @Transactional(readOnly = true)
    public List<EnrollmentDTO> findEnrollmentsByUserId(Long userId) {
        userService.validateSelfOrAdmin(userId);
        return repository.findEnrollmentByUserId(userId).stream().map(EnrollmentDTO::new).collect(Collectors.toList());
    }


}
