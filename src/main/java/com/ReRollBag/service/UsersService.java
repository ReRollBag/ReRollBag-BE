package com.ReRollBag.service;

import com.ReRollBag.domain.dto.UsersResponseDto;
import com.ReRollBag.domain.dto.UsersSaveRequestDto;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Log4j2
@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsersResponseDto save(UsersSaveRequestDto requestDto)  {
        Users users = requestDto.toEntity();
        //if (passwordEncoder == null) return null;
        String encryptedPassword = passwordEncoder.encode(users.getPassword());
        users.setPassword(encryptedPassword);
        usersRepository.save(users);
        return new UsersResponseDto(users);
    }

    public UsersResponseDto findByUsersId (String usersId) {
        Users users = usersRepository.findByUsersId(usersId);
        if (users == null) throw new IllegalArgumentException("Users is not Exists");
        return UsersResponseDto.builder()
                .users(users)
                .build();
    }
    public void login () {
    }
}
