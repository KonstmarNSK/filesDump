package com.kostya.filesDump.repositories.implementations;

import com.kostya.filesDump.entities.User;
import com.kostya.filesDump.repositories.interfaces.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by Костя on 14.05.2017.
 */
@Repository
public class UserRepositoryImplementation implements UserRepository{

    @Autowired
    SessionFactory sessionFactory;

    @Transactional
    public User getUserById(Long userId){
        Session session = sessionFactory.openSession();
        return session.get(User.class, userId);
    }

    @Transactional
    public User getUserByEmail(String email){
        Session session = sessionFactory.openSession();

        String hql = "from User U where U.email = :user_email";

        Query query = session.createQuery(hql);
        query.setParameter("user_email",email);

        return (User)query.getSingleResult();
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = getUserByEmail(s);

        if(user == null){
            throw new NoSuchElementException("No user with email '"+s+"' found");
        }

        return new UserDetails() {

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return user.getAuthorities();
            }

            @Override
            public String getPassword() {
                return user.getPassword();
            }

            @Override
            public String getUsername() {
                return user.getEmail();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }

    @Transactional
    public void putUser(User user){
        Session session = sessionFactory.openSession();
        session.save(user);
    }


}