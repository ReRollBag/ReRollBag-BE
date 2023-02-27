package com.ReRollBag.service;

import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.catalina.realm.UserDatabaseRealm.getRoles;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String usersId) throws UsernameNotFoundException {
        Users users = usersRepository.findByUsersId(usersId);

        return new org.springframework.security.core.userdetails.User(
                users.getUsername(),
                users.getPassword(),
                users.getAuthorities()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(users.getUserRole().toString()))
                        .collect(Collectors.toList()));
    }
}
