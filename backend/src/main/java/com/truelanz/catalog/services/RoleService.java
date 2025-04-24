package com.truelanz.catalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.truelanz.catalog.dto.RoleDTO;
import com.truelanz.catalog.entities.Role;
import com.truelanz.catalog.repositories.RoleRepository;
import com.truelanz.catalog.services.exceptions.DataBaseException;
import com.truelanz.catalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Page<RoleDTO> findAllPaged(Pageable pageable) {
        Page<Role> result = roleRepository.findAll(pageable);
        return result.map(x -> new RoleDTO(x));
    }

    @Transactional(readOnly = true)
    public RoleDTO findById(Long id) {
        Optional<Role> obj = roleRepository.findById(id);
        Role entity = obj.orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return new RoleDTO(entity);
    }

    @Transactional
    public RoleDTO insert(RoleDTO dto) {
        Role entity = new Role();
        copyDtoToEntity(dto, entity);
        entity = roleRepository.save(entity);
        return new RoleDTO(entity);
    }

    @Transactional
    public RoleDTO update(Long id, RoleDTO dto) {
        try {
            Role entity = roleRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = roleRepository.save(entity);
            return new RoleDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id" + id + "not found");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            roleRepository.deleteById(id);    		
        }
            catch (DataIntegrityViolationException e) {
                throw new DataBaseException("Falha de integridade referencial");
        }
    }

    //copiando do DTO para a entidade
    private void copyDtoToEntity(RoleDTO dto, Role entity) {
        entity.setAuthority(dto.getAuthority());
    }
}
