package com.ReRollBag.service;

import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

import static org.apache.catalina.realm.UserDatabaseRealm.getRoles;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String usersId) throws UsernameNotFoundException {
        return usersRepository.findByUsersId(usersId);
    }
}
