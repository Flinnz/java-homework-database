package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.HibernateUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class DAO<T> {
    protected List<T> queryAllEntities(Class<T> entityClass) {
        try (Session session = HibernateUtils.getSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
            Root<T> root = criteriaQuery.from(entityClass);
            return session.createQuery(criteriaQuery.select(root)).getResultList();
        }
    }

    protected T querySingleEntity(Class<T> entityClass, int id) {
        try (Session session = HibernateUtils.getSession()) {
            return session.find(entityClass, id);
        }
    }

    protected void makeTransaction(BiConsumer<Session, T> action, T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtils.getSession()) {
            tx = session.beginTransaction();
            action.accept(session, entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    public void save(T entity) {
        makeTransaction(Session::save, entity);
    }

    public void update(T entity) {
        makeTransaction(Session::update, entity);
    }

    public void delete(T entity) {
        makeTransaction(Session::delete, entity);
    }

    public <U> void updateEntityField(int id, U fieldValue, BiConsumer<T, U> updater) {
        T entity = this.findEntityById(id);
        updater.accept(entity, fieldValue);
        this.update(entity);
    }

    public T saveAndGetEntity(T entity) {
        this.save(entity);
        return entity;
    }

    public T findEntityByFieldValue(Predicate<T> predicate, Supplier<T> defaultValue) {
        return this
                .getAllEntities()
                .stream()
                .filter(predicate)
                .findFirst()
                .orElseGet(defaultValue); // вот тут можно было и поменять на sql, но мне лень
    }

    public abstract T findEntityById(int id);
    public abstract List<T> getAllEntities();
}
