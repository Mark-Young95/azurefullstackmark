package com.revature.AzureFullStackMark.beans.models;

import javax.persistence.*;

@Entity
@Table(name="user_table")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private Integer count;

    private User() {}

    public User(Integer userId, String username, String password, Integer count) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.count = count;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        //this.count = 0;
    }

//    public User(String username, String password, Integer count) {
//        this.username = username;
//        this.password = password;
//        this.count = count;
//    }

    public User(Integer userId, String username, Integer count) {
        this.userId = userId;
        this.username = username;
        this.count = count;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
