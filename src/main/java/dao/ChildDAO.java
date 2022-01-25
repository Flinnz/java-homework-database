package dao;

import models.Child;

import java.util.List;

public class ChildDAO extends DAO<Child>{
    @Override
    public Child findEntityById(int id) {
        return querySingleEntity(Child.class, id);
    }

    @Override
    public List<Child> getAllEntities() {
        return queryAllEntities(Child.class);
    }
}
