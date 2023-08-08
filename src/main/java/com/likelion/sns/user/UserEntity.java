package com.likelion.sns.user;

import com.likelion.sns.article.ArticleEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String profileImage;
    private String email;
    private String phone;

    @OneToMany(mappedBy = "user")
    private List<ArticleEntity> articles;
}
