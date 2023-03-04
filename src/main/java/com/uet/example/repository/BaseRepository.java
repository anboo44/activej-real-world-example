package com.uet.example.repository;

import com.uet.example.util.model.Identifier;
import io.activej.inject.annotation.Inject;
import io.activej.inject.annotation.Named;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * VD: var entities = List.of(session.find(clazz, new UserId(1))); ~> findById
 *
 * Read more: https://docs.jboss.org/hibernate/orm/6.2/userguide/html_single/Hibernate_User_Guide.html#pc-find
 *            https://docs.jboss.org/hibernate/orm/6.2/userguide/html_single/Hibernate_User_Guide.html#pc-filtering
 * ~> Batch insert: https://docs.jboss.org/hibernate/orm/6.2/userguide/html_single/Hibernate_User_Guide.html#batch-jdbcbatch
 * https://docs.jboss.org/hibernate/orm/6.2/userguide/html_single/Hibernate_User_Guide.html#annotations-jpa-prepersist
 */
abstract class BaseRepository {
    @Inject
    protected SessionFactory sessionFactory;

    @Inject
    @Named("database")
    protected Executor executor;

    protected <T> void save(T data) {
        var session = sessionFactory.openSession();
        var txn = session.getTransaction();

        try {
            txn.begin();
            session.merge(data); // ~> save or update
            session.flush(); // ~> Should in here before txn commit
            txn.commit();
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

    protected <T, R> R selectById(Identifier<T> id, Class<R> clazz) {
        var session = sessionFactory.openSession();
        var entity  = session.byId(clazz).load(id);
        session.close();

        return entity;
    }
}
