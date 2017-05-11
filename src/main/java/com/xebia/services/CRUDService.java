package com.xebia.services;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */

import java.util.List;


public interface CRUDService<T> {


    List<?> listAll();

    T getById(Integer id);

    T saveOrUpdate(T domainObject);

    void delete(Integer id);
}
