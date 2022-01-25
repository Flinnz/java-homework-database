package models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Table(name = "parent")
@Entity
public class Parent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fullname")
    private String fullname;

    public Parent() {

    }

    public Parent(String fullname, Address address) {
        this.fullname = fullname;
        this.address = address;
    }

    public String getFullname() {
        return fullname;
    }

    @ManyToOne
    @JoinColumn(name="address_id", insertable = true, updatable = true)
    private Address address;

    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
    @ManyToMany
    @JoinTable(name = "child_parent", joinColumns = @JoinColumn(name = "parent_id"), inverseJoinColumns = @JoinColumn(name = "child_id"))
    private Set<Child> children = new HashSet<>();

    public Set<Child> getChildren() {
        return children;
    }
}
