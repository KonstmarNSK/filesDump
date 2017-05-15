package com.kostya.filesDump.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Костя on 14.05.2017.
 */
@Entity
@Table(name = "users")
public class User implements Serializable{
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id" )
    private Set<Authority> authorities;

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

    public void addAuthority(String authority){
        if(authorities == null){
            authorities = new HashSet<>();
        }
        Authority newAuthority = new Authority();
        newAuthority.setAuthority(authority);

        this.authorities.add(newAuthority);
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public long getId() {
        return id;
    }
}
