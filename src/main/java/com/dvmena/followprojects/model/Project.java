package com.dvmena.followprojects.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Blob;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String link;

    @Lob
    private Blob fileBLob;

    private String fileName;

    public Long getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public Blob getFileBLob() {
        return fileBLob;
    }

    public String getFileName() {
        return fileName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setFileBLob(Blob fileBLob) {
        this.fileBLob = fileBLob;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
