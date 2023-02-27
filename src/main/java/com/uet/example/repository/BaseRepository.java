package com.uet.example.repository;

import com.uet.example.util.model.Identifier;
import io.activej.inject.annotation.Inject;
import io.activej.inject.annotation.Named;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.concurrent.Executor;

abstract class BaseRepository {
    @Inject
    protected SessionFactory sessionFactory;

    @Inject
    @Named("database")
    protected Executor executor;

    protected <T> void save(T data) {
        var session = sessionFactory.openSession();
        try {
            session.getTransaction().begin();
            session.persist(data);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    protected <T> List<T> selectAll(Class<T> clazz) {
        var query = String.format("SELECT u FROM %s u", clazz.getName());

        var session  = sessionFactory.openSession();
        var entities = session.createQuery(query, clazz).getResultList();
        session.close();

        return entities;
    }

    protected <T> T selectById(Identifier<T> id, Class<T> clazz) {
        var session = sessionFactory.openSession();
        var entity  = session.byId(clazz).load(id);
        session.close();

        return entity;
    }
}
