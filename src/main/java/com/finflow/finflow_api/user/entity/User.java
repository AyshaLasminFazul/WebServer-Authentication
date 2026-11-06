package com.finflow.finflow_api.user.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    public User() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name= "id")
    private Long id;

    @Column(name= "name")
    private String name;

    @Column(name= "email")
    private String email;

    @Column(name= "password_hash")
    private String passwordHash;

    @Column(name= "role")
    private String role;

    @Column(name= "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
