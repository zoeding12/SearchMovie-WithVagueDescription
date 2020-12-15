package edu.inforetrieval.zoe;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Objects;

public class Movie {
    @JSONField(name = "_id")
    private String id;

    @JSONField(name = "title")
    private String title;

    @JSONField(name = "original_title")
    private String original_title;

    @JSONField(name = "genre")
    private List<String> genres;

    @JSONField(name = "actors")
    private List<String> actors;

    @JSONField(name = "director")
    private List<String> director;

    @JSONField(name = "writer")
    private List<String> writer;

    @JSONField(name = "avg_vote")
    private double avg_vote;

    @JSONField(name = "votes")
    private long votes;

    @JSONField(name = "description")
    private String description;

    public Movie(String id, String title, String original_title, List<String> genres, List<String> actors, List<String> director, List<String> writer, double avg_vote, long votes, String description) {
        this.id = id;
        this.title = title;
        this.original_title = original_title;
        this.genres = genres;
        this.actors = actors;
        this.director = director;
        this.writer = writer;
        this.avg_vote = avg_vote;
        this.votes = votes;
        this.description = description;
    }

    public Movie(){

    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<String> getActors() {
        return actors;
    }

    public List<String> getDirector() {
        return director;
    }

    public List<String> getWriter() {
        return writer;
    }

    public double getAvg_vote() {
        return avg_vote;
    }

    public long getVotes() {
        return votes;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    public void setDirector(List<String> director) {
        this.director = director;
    }

    public void setWriter(List<String> writer) {
        this.writer = writer;
    }

    public void setAvg_vote(double avg_vote) {
        this.avg_vote = avg_vote;
    }

    public void setVotes(long votes) {
        this.votes = votes;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
