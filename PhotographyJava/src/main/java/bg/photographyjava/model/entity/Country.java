package bg.photographyjava.model.entity;

import bg.photographyjava.model.enums.CountryEnum;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "countries")
public class Country {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private CountryEnum name;

    @OneToMany(mappedBy = "country")
    private Set<User> users;

    public Country() {
        this.users = new HashSet<>();
    }

    public Country(CountryEnum name) {
        this.name = name;
        this.users = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CountryEnum getName() {
        return name;
    }

    public void setName(CountryEnum name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
