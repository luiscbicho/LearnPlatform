package com.luis.learnplatform.controllers;

import com.luis.learnplatform.entities.DTO.UserDTO;
import com.luis.learnplatform.entities.DTO.UserInsertDTO;
import com.luis.learnplatform.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> users = service.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        UserDTO user = service.findById(id);
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


    @DeleteMapping(value="/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }



}
