package com.coursework.controller;

import com.coursework.model.AbstractEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommonController<E extends AbstractEntity> {
    ResponseEntity<List<E>> getAll();
    ResponseEntity<E> create(E e);
    ResponseEntity<E> update(Long id, E e);
    ResponseEntity<Void> delete(Long id);
}