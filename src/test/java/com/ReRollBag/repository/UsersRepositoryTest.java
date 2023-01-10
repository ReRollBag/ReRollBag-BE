package com.ReRollBag.repository;

import com.ReRollBag.domain.entity.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
public class UsersRepositoryTest {
    @Autowired
    UsersRepository usersRepository;

    @Test
    @DisplayName("[Repository] 사용자 회원 가입")
    void Repository_회원가입_테스트 () {
        Users users = Users.builder()
                .usersId("test@gmail.com")
                .nickname("testNickname")
                .password("testPassword")
                .build();

        usersRepository.save(users);
        Users target = usersRepository.findByUsersId("test@gmail.com");

        String expectedUsersId = "test@gmail.com";
        String expectedNickname = "testNickname";
        String expectedPassword = "testPassword";

        assertThat(target.getUsersId()).isEqualTo(expectedUsersId);
        assertThat(target.getNickname()).isEqualTo(expectedNickname);
        assertThat(target.getPassword()).isEqualTo(expectedPassword);

    }

}
