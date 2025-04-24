package com.truelanz.catalog.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.truelanz.catalog.dto.RoleDTO;
import com.truelanz.catalog.services.RoleService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping(value = "/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;
    
    @GetMapping()
    public ResponseEntity<Page<RoleDTO>> findAll(Pageable pageable) {
        Page <RoleDTO> list = roleService.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }
    
    @GetMapping(value = "/{id}")
    public ResponseEntity<RoleDTO> findById(@PathVariable Long id) {
        RoleDTO dto = roleService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping()
    public ResponseEntity<RoleDTO> insert(@RequestBody RoleDTO dto) {
        dto = roleService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<RoleDTO> update(@PathVariable Long id, @RequestBody RoleDTO dto) {
        dto = roleService.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}