package com.ReRollBag.service;

import com.ReRollBag.domain.BagsCount;
import com.ReRollBag.domain.dto.Bags.BagsRentOrReturnRequestDto;
import com.ReRollBag.domain.dto.Bags.BagsResponseDto;
import com.ReRollBag.domain.dto.Bags.BagsSaveRequestDto;
import com.ReRollBag.domain.dto.MockResponseDto;
import com.ReRollBag.domain.entity.Bags;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.repository.BagsRepository;
import com.ReRollBag.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BagsService {

    private final BagsRepository bagsRepository;
    private final UsersRepository usersRepository;
    private final BagsCount bagsCount;

    public BagsResponseDto save(BagsSaveRequestDto bagsSaveRequestDto) {
        Bags saveTarget = bagsSaveRequestDto.toEntity();
        String region = saveTarget.getBagsId();

        if (!bagsCount.isExistWithRegion(region)) {
            bagsCount.saveNewRegion(region);
        }

        bagsCount.increaseLastIndexWithRegion(region);
        Long bagsCountIndex = bagsCount.getLastIndexWithRegion(region);

        saveTarget.setBagsId(region + bagsCountIndex);
        saveTarget.setWhenIsRented(LocalDateTime.MIN);

        bagsRepository.save(saveTarget);
        return new BagsResponseDto(saveTarget);
    }

    public MockResponseDto rentOrReturn(BagsRentOrReturnRequestDto requestDto) {
        String bagsId = requestDto.getBagsId();
        String usersId = requestDto.getUsersId();

        Users users = usersRepository.findByUsersId(usersId);
        Bags bags = bagsRepository.findById(bagsId).orElseThrow(
                () -> new IllegalArgumentException("IllegalArgumentException")
        );

        if (!bags.isRented()) return renting(bags, users);
        return returning(bags, users);
    }

    private MockResponseDto renting(Bags bags, Users users) {

        MockResponseDto responseDto = MockResponseDto.builder()
                .data(true)
                .build();

        bags.setRentingUsers(users);
        bags.setRented(true);
        bags.setWhenIsRented(LocalDateTime.now());

        users.getRentingBagsList().add(bags);

        return responseDto;
    }

    private MockResponseDto returning(Bags bags, Users users) {

        MockResponseDto responseDto = MockResponseDto.builder()
                .data(true)
                .build();

        bags.setWhenIsRented(LocalDateTime.MIN);
        bags.setRentingUsers(null);
        bags.setRented(false);

        users.getReturningBagsList().remove(bags);
        users.getReturnedBagsList().add(bags);

        return responseDto;
    }

}
