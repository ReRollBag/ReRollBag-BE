package com.ReRollBag.repository;

import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.as;
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
                .UID("testUID")
                .usersId("test@gmail.com")
                .name("testName")
                .userRole(UserRole.ROLE_USER)
                .build();

        usersRepository.save(users);
        Users target = usersRepository.findByUsersId("test@gmail.com");

        String expectedUID = "testUID";
        String expectedUsersId = "test@gmail.com";
        String expectedName = "testName";
        UserRole expectedUserRole = UserRole.ROLE_USER;

        assertThat(target.getUID()).isEqualTo(expectedUID);
        assertThat(target.getUsersId()).isEqualTo(expectedUsersId);
        assertThat(target.getName()).isEqualTo(expectedName);
        assertThat(target.getUserRole()).isEqualTo(expectedUserRole);

    }

    @Test
    @DisplayName("[Repository] 관리자 계정 가입 테스트")
    void Repository_관리자회원가입_테스트() {
        Users users = Users.builder()
                .UID("testAdminUID")
                .usersId("testAdmin@gmail.com")
                .name("testAdminName")
                .userRole(UserRole.ROLE_ADMIN)
                .build();

        usersRepository.save(users);
        Users target = usersRepository.findByUsersId("testAdmin@gmail.com");

        String expectedUID = "testAdminUID";
        String expectedUsersId = "testAdmin@gmail.com";
        String expectedName = "testAdminName";
        UserRole expectedUserRole = UserRole.ROLE_ADMIN;

        assertThat(target.getUID()).isEqualTo(expectedUID);
        assertThat(target.getUsersId()).isEqualTo(expectedUsersId);
        assertThat(target.getName()).isEqualTo(expectedName);
        assertThat(target.getUserRole()).isEqualTo(expectedUserRole);

    }

}
