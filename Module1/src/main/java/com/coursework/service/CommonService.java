package com.coursework.service;

import com.coursework.model.AbstractEntity;

import java.util.List;

public interface CommonService <E extends AbstractEntity>{
    List<E> readAll();
    boolean creat(E e);
    void update(Long id, E e);
    void delete(E e);
}