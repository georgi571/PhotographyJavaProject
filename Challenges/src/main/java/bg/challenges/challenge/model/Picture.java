package bg.challenges.challenge.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "pictures")
public class Picture {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "caption")
    private String caption;

    @Column(name = "story")
    private String story;

    @Column(name = "author_id")
    private UUID authorId;

    @Column(name = "number_of_likes")
    private long likes;

    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @ElementCollection
    @CollectionTable(name = "liked_by_users", joinColumns = @JoinColumn(name = "picture_id"))
    @Column(name = "user_id")
    private Set<UUID> likedByUsers;

    @OneToMany(mappedBy = "picture", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public Set<UUID> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(Set<UUID> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
