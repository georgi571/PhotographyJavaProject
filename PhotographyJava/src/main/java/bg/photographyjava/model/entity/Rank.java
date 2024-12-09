package bg.photographyjava.model.entity;

import bg.photographyjava.model.enums.UserRank;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "ranks")
public class Rank {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "`rank`")
    private UserRank rank;

    @OneToMany(mappedBy = "rank")
    private Set<User> users;

    public Rank() {
        this.users = new HashSet<>();
    }

    public Rank(UserRank rank) {
        this.rank = rank;
        this.users = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserRank getRank() {
        return rank;
    }

    public void setRank(UserRank rank) {
        this.rank = rank;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
