package dev.ervin.online_quiz.services;

import java.util.List;

public interface BaseService<T, ID> {
    T create(T entity);
    T update(ID id, T entityDetails);
    T getById(ID id);
    List<T> getAll();
    void delete(ID id);
}
