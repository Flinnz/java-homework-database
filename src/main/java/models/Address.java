package models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Table(name="address")
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    public District getDistrict() {
        return district;
    }

    public Address(District district, String address) {
        this.district = district;
        this.address = address;
    }

    public Address() {
    }

    @Column(unique=true, name="address")
    private String address;
    public String getAddress() {
        return address;
    }
}
