package models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Table(name = "educational_institution")
@Entity
public class EducationalInstitution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public int getId() {
        return id;
    }

    public EducationalInstitution(Address address, int number) {
        this.address = address;
        this.number = number;
    }

    public EducationalInstitution() {
    }

    @ManyToOne
    @JoinColumn(name="address_id", insertable = true, updatable = true)
    private Address address;
    public Address getAddress() {
        return address;
    }

    @Column(name = "number")
    private int number;
    public int getNumber() {
        return number;
    }
    @OneToMany(mappedBy = "educationalInstitution", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Child> children = new HashSet<>();

    public Set<Child> getChildren() {
        return children;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
