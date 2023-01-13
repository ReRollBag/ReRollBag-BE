package com.ReRollBag.integration;

import com.ReRollBag.domain.dto.UsersLoginRequestDto;
import com.ReRollBag.domain.dto.UsersLoginResponseDto;
import com.ReRollBag.domain.dto.UsersResponseDto;
import com.ReRollBag.domain.dto.UsersSaveRequestDto;
import com.ReRollBag.exceptions.usersExceptions.UsersIdAlreadyExistException;
import com.ReRollBag.repository.UsersRepository;
import com.ReRollBag.service.RedisService;
import com.ReRollBag.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersIntegrationTest {

    @Autowired
    private UsersService usersService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    @DisplayName("[Integration] 회원 가입")
    @Rollback(value = false)
    void Integration_회원가입_테스트() throws Exception {
        //given
        UsersSaveRequestDto requestDto = new UsersSaveRequestDto(
                "test@gmail.com",
                "testNickname",
                "testPassword"
        );

        UsersResponseDto responseDto = new UsersResponseDto("test@gmail.com", "testNickname");

        //when
        mockMvc.perform(post("/api/v2/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto))
        )

        //then
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDto)))
                .andDo(print());

    }

    @Test
    @DisplayName("[Integration] 로그인 및 Redis 토큰 검증")
    void Integration_로그인_테스트() throws Exception {
        //given
        UsersLoginRequestDto requestDto = new UsersLoginRequestDto(
                "test@gmail.com",
                "testPassword"
        );

        //when
        MvcResult loginResult = mockMvc.perform(post("/api/v2/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto))
                )
                //then
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        //when
        String content = loginResult.getResponse().getContentAsString();
        UsersLoginResponseDto loginResponseDto = new ObjectMapper().readValue(content, UsersLoginResponseDto.class);
        String expectedAccessToken = loginResponseDto.getAccessToken();
        String expectedRefreshToken = loginResponseDto.getRefreshToken();
        String actualAccessToken = redisService.findAccessToken("test@gmail.com");
        String actualRefreshToken = redisService.findRefreshToken("test@gmail.com");

        //then
        assertThat(expectedAccessToken).isEqualTo(actualAccessToken);
        assertThat(expectedRefreshToken).isEqualTo(actualRefreshToken);
    }

    @Test
    @DisplayName("[Integration] 아이디 중복 검사 성공 case")
    void Integration_아이디_중복검사_성공() throws UsersIdAlreadyExistException {
        //given
        String usersId = "notDuplicatedUsersId@gmail.com";

        //when
        try {
            mockMvc.perform(get("/api/v2/users/checkUserExist/" + usersId))
        //then
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("[Integration] 아이디 중복 검사 실패 case")
    void Integration_아이디_중복검사_실패() throws Exception {
        //given
        String usersId = "test@gmail.com";

        //when
        try {
            mockMvc.perform(get("/api/v2/users/checkUserExist/" + usersId))
        //then
                    .andExpect(status().isAccepted());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("[Integration] 올바른 로그인 로직 이후 dummyMethod 호출 성공 case")
    void Integration_로그인이후_발급된토큰으로_dummyMethod_성공() throws Exception {
        //given
        UsersLoginRequestDto requestDto = new UsersLoginRequestDto(
                "test@gmail.com",
                "testPassword"
        );

        //when
        MvcResult loginResult = mockMvc.perform(post("/api/v2/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
        //then
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn();

        //when
        String content = loginResult.getResponse().getContentAsString();
        UsersLoginResponseDto loginResponseDto = new ObjectMapper().readValue(content, UsersLoginResponseDto.class);
        String accessToken = loginResponseDto.getAccessToken();
        String refreshToken = loginResponseDto.getRefreshToken();
        mockMvc.perform(get("/api/v1/users/dummyMethod")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Token", accessToken))
        //then
                .andExpect(status().isOk())
                .andReturn();

    }

}
