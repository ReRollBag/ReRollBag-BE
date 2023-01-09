package com.ReRollBag.service;

import com.ReRollBag.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UsersRepository usersRepository;


    @Override
    public UserDetails loadUserByUsername(String usersPK) throws UsernameNotFoundException {
        return usersRepository.findById(usersPK)
                .orElseThrow(()->new IllegalArgumentException("Cannot find users with usersPK"));
    }
}
