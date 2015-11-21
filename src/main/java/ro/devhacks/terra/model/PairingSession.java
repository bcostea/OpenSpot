package ro.devhacks.terra.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "sessions")
@SequenceGenerator(name = "seq_gen", sequenceName = "session_seq")
public class PairingSession {

    @Id
    @GeneratedValue(generator = "seq_gen", strategy = GenerationType.AUTO)
    private Long id;

    @Size(min = 1, max = 255)
    @NotNull
    private String sessionName;

    @NotNull
    private String language;

    @NotNull
    private String practice;

    @NotNull
    private String duration;

    @NotNull
    @ManyToOne
    private User creator;

    @ManyToOne
    private User participant;

    @NotNull
    private String location;

    private String dateAsString;

    @NotNull
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date atTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPractice() {
        return practice;
    }

    public void setPractice(String practice) {
        this.practice = practice;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getAtTime() {
        return atTime;
    }

    public void setAtTime(Date atTime) {
        this.atTime = atTime;
    }

    @Transient
    public String getDateAsString() {
        return dateAsString;
    }

    public void setDateAsString(String dateAsString) {
        this.dateAsString = dateAsString;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getParticipant() {
        return participant;
    }

    public void setParticipant(User participant) {
        this.participant = participant;
    }
}
