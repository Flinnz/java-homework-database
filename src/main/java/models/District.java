package models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Table(name="district")
@Entity
public class District {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, name="name")
    private String name;

    public String getName() {
        return name;
    }

    public District() {

    }

    public District(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Address> addresses = new HashSet<>();

    public Set<Address> getAddresses() {
        return addresses;
    }
}
