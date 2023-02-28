package com.ReRollBag.repository;

import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void teardown() {
        usersRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("[Repository] 회원 가입 테스트")
    void Repository_회원가입_테스트() {
        Users users = Users.builder()
                .usersId("test@gmail.com")
                .nickname("testNickname")
                .idToken("testIdToken")
                .userRole(UserRole.ROLE_USER)
                .build();

        usersRepository.save(users);
        Users target = usersRepository.findByUsersId("test@gmail.com");

        String expectedUsersId = "test@gmail.com";
        String expectedNickname = "testNickname";
        String expectedIdToken = "testIdToken";
        UserRole expectedUserRole = UserRole.ROLE_USER;

        assertThat(target.getUsersId()).isEqualTo(expectedUsersId);
        assertThat(target.getNickname()).isEqualTo(expectedNickname);
        assertThat(target.getIdToken()).isEqualTo(expectedIdToken);
        assertThat(target.getUserRole()).isEqualTo(expectedUserRole);

    }

    @Test
    @DisplayName("[Repository] 관리자 계정 가입 테스트")
    void Repository_관리자회원가입_테스트() {
        Users users = Users.builder()
                .usersId("testAdmin@gmail.com")
                .nickname("testAdmin")
                .idToken("testAdminIdToken")
                .userRole(UserRole.ROLE_ADMIN)
                .build();

        usersRepository.save(users);
        Users target = usersRepository.findByUsersId("testAdmin@gmail.com");

        String expectedUsersId = "testAdmin@gmail.com";
        String expectedNickname = "testAdmin";
        String expectedIdToken = "testAdminIdToken";
        UserRole expectedUserRole = UserRole.ROLE_ADMIN;

        assertThat(target.getUsersId()).isEqualTo(expectedUsersId);
        assertThat(target.getNickname()).isEqualTo(expectedNickname);
        assertThat(target.getPassword()).isEqualTo(expectedIdToken);
        assertThat(target.getUserRole()).isEqualTo(expectedUserRole);

    }

}
