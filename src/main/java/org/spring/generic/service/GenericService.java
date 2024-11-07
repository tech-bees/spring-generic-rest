package org.spring.generic.service;

import org.spring.generic.exception.InvalidIdException;
import org.spring.generic.repo.GenericRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public abstract class GenericService<T> {

    private final GenericRepository<T> repository;

    public GenericService(GenericRepository<T> genericRepository){
        this.repository = genericRepository;
    }

    public Page<T> findAll(Pageable pageable){
        return repository.findAll(pageable);
    }

    public List<T> findAll(){
        return repository.findAll();
    }

    public  T findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new InvalidIdException("Invalid Id!"));
    }

    public T save(T entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        T entity = findById(id);
        repository.delete(entity);
    }
}
