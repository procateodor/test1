package services.user.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity(name = "submissions")
public class Submission {
    public Submission() {}

    public Submission(UUID id, List<String> urls, UUID userId) {
        this.id = id;
        this.urls = String.join(",", urls);
        this.userId = userId;
        this.status = "pending";
    }

    public Submission(List<String> urls, UUID userId) {
        this.id = UUID.randomUUID();
        this.urls = String.join(",", urls);
        this.userId = userId;
        this.status = "pending";
    }

    @Id
    @NotNull
    private UUID id;

    private String status;

    private String urls;

    @NotNull
    @Column(name = "user_id")
    private UUID userId;

    private String results;

    public Map<String, String> toJSON() {
        Map<String, String> submission = new HashMap<>();
        submission.put("id", id.toString());
        submission.put("userId", userId.toString());
        submission.put("urls", urls);
        submission.put("results", results);

        return submission;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }
}
