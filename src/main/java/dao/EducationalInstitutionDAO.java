package dao;

import models.EducationalInstitution;

import java.util.List;

public class EducationalInstitutionDAO extends DAO<EducationalInstitution> {
    @Override
    public EducationalInstitution findEntityById(int id) {
        return querySingleEntity(EducationalInstitution.class, id);
    }

    @Override
    public List<EducationalInstitution> getAllEntities() {
        return queryAllEntities(EducationalInstitution.class);
    }
}
