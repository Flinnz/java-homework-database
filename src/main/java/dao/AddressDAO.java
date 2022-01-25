package dao;

import models.Address;

import java.util.List;

public class AddressDAO extends DAO<Address> {
    @Override
    public Address findEntityById(int id) {
        return querySingleEntity(Address.class, id);
    }

    @Override
    public List<Address> getAllEntities() {
        return queryAllEntities(Address.class);
    }
}
