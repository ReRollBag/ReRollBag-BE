package com.ReRollBag.domain.entity;

import com.ReRollBag.enums.UserRole;
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
public class Users extends BaseTimeEntity implements UserDetails {

    // Firebase UID Value for Entity PK
    @Id
    @Column(unique = true)
    private String UID;

    // Email Value for Entity
    @Column(name = "usersId")
    private String usersId;

    // Name Value for Entity
    @Column(name = "name")
    private String name;

    // Users' role; Admin, Users, Blocked
    @Column(name = "userRole")
    private UserRole userRole;

    @Builder.Default
    @OneToMany(mappedBy = "rentingUsers", fetch = FetchType.EAGER)
    private List<Bags> rentingBagsList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "returningUsers", fetch = FetchType.EAGER)
    private List<Bags> returningBagsList = new ArrayList<>();

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
        return "";
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
