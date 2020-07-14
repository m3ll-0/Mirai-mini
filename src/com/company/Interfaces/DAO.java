package com.company.Interfaces;

import java.util.List;
import java.util.Optional;

public interface DAO<T> {

    List<T> getAll();

    void save(T t);
}