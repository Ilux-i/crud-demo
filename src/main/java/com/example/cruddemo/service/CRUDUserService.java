package com.example.cruddemo.service;

import com.example.cruddemo.dto.*;
import com.example.cruddemo.entity.UserEntity;
import com.example.cruddemo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CRUDUserService {

    @Autowired
    private UserRepository repository;

    @Transactional
    public UserInstanceDto create(UserCreateUpdateDto dto) {
        var entityForCreate = new UserEntity(null, dto.name());
        var createdEntity = repository.save(entityForCreate);
        return UserInstanceDto.ofEntity(createdEntity);
    }

    @Transactional
    public UserInstanceDto update(UserInstanceDto dto){
        var entityForUpdate = repository.findUserEntityById(dto.id());
        entityForUpdate.setName(dto.name());
        var updatedEntity = repository.save(entityForUpdate);
        return UserInstanceDto.ofEntity(updatedEntity);
    }

    public void delete(long id) {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format(e.getMessage())
            );
        }

    }

    @Transactional
    public UserInstanceDto get(Long id) {
        var entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                String.format("User entity is not found by id [%s]", id)
        ));
        return UserInstanceDto.ofEntity(entity);
    }

    @Transactional
    public PagedListDto<UserInstanceDto> list(Integer page, Integer size) {
        var entities = repository.findAllPaged(page*size, size);
        var totalCount = repository.count();

        return new PagedListDto<>(
                entities.stream().map(UserInstanceDto::ofEntity).toList(),
                page,
                size,
                totalCount
        );
    }
}
