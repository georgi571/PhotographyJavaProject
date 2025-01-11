package bg.photographyjava.user.model;

import bg.photographyjava.user.property.enums.CountryEnum;
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
    private Set<UserEntity> users;

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

    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }
}
