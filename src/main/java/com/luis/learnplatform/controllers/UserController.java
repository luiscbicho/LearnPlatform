package com.luis.learnplatform.controllers;

import com.luis.learnplatform.entities.DTO.EnrollmentDTO;
import com.luis.learnplatform.entities.DTO.UserDTO;
import com.luis.learnplatform.entities.DTO.UserInsertDTO;
import com.luis.learnplatform.services.EnrollmentService;
import com.luis.learnplatform.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private EnrollmentService enrollmentService;


    //GET http://localhost:8080/users?name=alex
    //GET http://localhost:8080/users?name=alex&page=1&size=5
    //GET http://localhost:8080/users?sort=id,desc
    @GetMapping
    public ResponseEntity<Page<UserDTO>>findAll(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
         Page<UserDTO> page = service.findAll(name, pageable);
         return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        UserDTO user = service.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe() {
        UserDTO user = service.getMe();
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserDTO> insert(@RequestBody UserInsertDTO dto) {
        UserDTO userDTO = service.insert(dto);
        URI uri= ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(userDTO);
    }


    @PutMapping(value="/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserInsertDTO dto) {
        UserDTO newDto = service.update(id,dto);
        return ResponseEntity.ok(newDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value="/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value="/{id}/enrollments")
    public ResponseEntity<List<EnrollmentDTO>> findEnrollmentsByUserId(@PathVariable Long id) {
        List<EnrollmentDTO> list = enrollmentService.findEnrollmentsByUserId(id);
        return ResponseEntity.ok(list);
    }

}
