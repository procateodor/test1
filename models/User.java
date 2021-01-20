package services.user.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity(name = "users")
public class User {
    public User() {}

    public User(UUID id, String name, String email, String picture, String token) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.token = token;
    }

    public User(String name, String email, String picture, String token) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.token = token;
    }

    @Id
    @NotNull
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String picture;

    @NotNull
    private String token;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
