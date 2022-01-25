package dao;

import models.Parent;
import org.hibernate.Session;
import utils.HibernateUtils;

import java.util.List;

public class ParentDAO extends DAO<Parent> {
    @Override
    public Parent findEntityById(int id) {
        return querySingleEntity(Parent.class, id);
    }

    @Override
    public List<Parent> getAllEntities() {
        return queryAllEntities(Parent.class);
    }
}
