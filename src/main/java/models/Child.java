package models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Table(name = "child")
@Entity
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fullname")
    private String fullname;

    public Child(String fullname, int age) {
        this.fullname = fullname;
        this.age = age;
    }

    public Child() {
    }

    public String getFullname() {
        return fullname;
    }

    @Column(name = "age")
    private int age;

    public int getAge() {
        return age;
    }

    @ManyToMany(mappedBy = "children")
    private Set<Parent> parent = new HashSet<>();

    public Set<Parent> getParents() {
        return parent;
    }

    @ManyToOne
    @JoinColumn(name = "educational_institution_id")
    private EducationalInstitution educationalInstitution;

    public EducationalInstitution getEducationalInstitution() {
        return educationalInstitution;
    }
    public void setEducationalInstitution(EducationalInstitution educationalInstitution) {
        this.educationalInstitution = educationalInstitution;
    }
}
