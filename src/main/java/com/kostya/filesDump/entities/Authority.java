package com.kostya.filesDump.entities;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Костя on 14.05.2017.
 */
@Entity
@Table(name = "authorities")
public class Authority implements Serializable, GrantedAuthority{
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(strategy = "increment", name = "increment")
    long id;

    @Column(name = "auth")
    private String authority;

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
