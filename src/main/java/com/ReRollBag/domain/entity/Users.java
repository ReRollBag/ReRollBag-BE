package com.ReRollBag.domain.entity;

import com.ReRollBag.enums.UserRole;
import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users implements UserDetails{

    @Id @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usersPk;

    @NotNull
    @Column(name = "usersId", nullable = false)
    private String usersId;

    @NotNull
    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "idToken")
    private String idToken;

    @NotNull
    @Column(name = "userrole", nullable = false)
    private UserRole userRole;

    @Builder.Default
    @OneToMany(mappedBy = "bagsId", fetch = FetchType.EAGER)
    private List<Bags> rentingBagsList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "bagsId", fetch = FetchType.EAGER)
    private List<Bags> returningBagsList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "bagsId", fetch = FetchType.EAGER)
    private List<Bags> returnedBagsList = new ArrayList<>();

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userRole.name()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return idToken;
    }

    @Override
    public String getUsername() {
        return usersId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

}
