package bg.photographyjava.user.model;

import bg.photographyjava.user.property.enums.GenderEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    @Size(min = 2, max = 50)
    private String username;

    @Column(name = "real_name", nullable = false)
    @Size(min = 2, max = 50)
    private String realName;

    @Column(name = "email", nullable = false, unique = true)
    @Email
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_id", referencedColumnName = "id")
    private Country country;

    @Column(name = "city", nullable = false)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private GenderEnum gender;

    @Column(name = "age", nullable = false)
    private int age;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rank_id", referencedColumnName = "id")
    private Rank rank;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "is_approved")
    private boolean isApproved;

    @Column(name = "profile_picture")
    private String profilePicturePath;

    @Column(name = "is_banned")
    private boolean isBanned;

    @Column(name = "reason_for_ban")
    private String reasonForBan;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Rank getRank() {
        return rank;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public String getReasonForBan() {
        return reasonForBan;
    }

    public void setReasonForBan(String reasonForBan) {
        this.reasonForBan = reasonForBan;
    }
}
