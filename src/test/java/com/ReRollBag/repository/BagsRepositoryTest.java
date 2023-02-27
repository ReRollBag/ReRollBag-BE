package com.ReRollBag.repository;

import com.ReRollBag.domain.entity.Bags;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.enums.UserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BagsRepositoryTest {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    BagsRepository bagsRepository;

    @BeforeAll
    @Test
    @DisplayName("Users 저장 이후 Bags 저장 테스트")
    void 테스트전_users저장_그리고_Bags저장_테스트() {
        Users users = Users.builder()
                .usersId("test@gmail.com")
                .nickname("testNickname")
                .password("testPassword")
                .userRole(UserRole.ROLE_USER)
                .build();

        usersRepository.save(users);

        //given
        String expectedId = "KOR_SUWON_1";
        LocalDateTime expectedTime = LocalDateTime.now();

        Bags target = Bags.builder()
                .bagsId(expectedId)
                .isRented(false)
                .whenIsRented(expectedTime)
                .build();

        //when
        bagsRepository.save(target);

        //then
        assertThat(target.getBagsId()).isEqualTo(expectedId);
        assertThat(target.isRented()).isEqualTo(false);
        assertThat(target.getWhenIsRented()).isEqualTo(expectedTime);
    }

}