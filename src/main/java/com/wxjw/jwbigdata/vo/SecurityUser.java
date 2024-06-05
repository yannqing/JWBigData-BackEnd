package com.yannqing.dockerdesktop.vo;


import com.yannqing.dockerdesktop.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails {

    private List<SimpleGrantedAuthority> simpleGrantedAuthorities;

    private final User user;

    private final int role;

    public SecurityUser(User user, int role) {
        this.user = user;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public int getRole() {
        return role;
    }

    public void setSimpleGrantedAuthorities(List<SimpleGrantedAuthority> simpleGrantedAuthorities) {
        this.simpleGrantedAuthorities = simpleGrantedAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return simpleGrantedAuthorities;
    }

    @Override
    public String getPassword() {
        String password = user.getPassword();
        user.setPassword(null);
        return password;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getAccount_no_expired()==1;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getAccount_no_locked()==1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.getCredentials_no_expired()==1;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled()==1;
    }
}
