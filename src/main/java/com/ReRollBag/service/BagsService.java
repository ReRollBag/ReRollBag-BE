package com.ReRollBag.service;

import com.ReRollBag.domain.BagsCount;
import com.ReRollBag.domain.dto.Bags.BagsRentOrReturnRequestDto;
import com.ReRollBag.domain.dto.Bags.BagsResponseDto;
import com.ReRollBag.domain.dto.Bags.BagsSaveRequestDto;
import com.ReRollBag.domain.dto.MockResponseDto;
import com.ReRollBag.domain.entity.Bags;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.exceptions.bagsExceptions.AlreadyRentedException;
import com.ReRollBag.exceptions.bagsExceptions.ReturnRequestUserMismatchException;
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

    private static final MockResponseDto successMockResponseDto = new MockResponseDto(true);

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


    @Transactional
    public MockResponseDto renting(BagsRentOrReturnRequestDto requestDto) throws AlreadyRentedException {
        String bagsId = requestDto.getBagsId();
        String usersId = requestDto.getUsersId();

        Users users = usersRepository.findByUsersId(usersId);
        Bags bags = bagsRepository.findById(bagsId).orElseThrow(
                () -> new IllegalArgumentException("IllegalArgumentException")
        );

        if (bags.isRented())
            throw new AlreadyRentedException();

        bags.setRentingUsers(users);
        bags.setRented(true);
        bags.setWhenIsRented(LocalDateTime.now());

        users.getRentingBagsList().add(bags);

        return successMockResponseDto;
    }

    public MockResponseDto requestReturning(BagsRentOrReturnRequestDto requestDto) throws ReturnRequestUserMismatchException {
        String bagsId = requestDto.getBagsId();
        String usersId = requestDto.getUsersId();

        Users users = usersRepository.findByUsersId(usersId);
        Bags bags = bagsRepository.findById(bagsId).orElseThrow(
                () -> new IllegalArgumentException("IllegalArgumentException")
        );

        if (users.getUsersId() != bags.getRentingUsers().getUsersId())
            throw new ReturnRequestUserMismatchException();

        users.getRentingBagsList().remove(bags);
        users.getReturningBagsList().add(bags);

        return successMockResponseDto;
    }

    public MockResponseDto returning(String bagsId) {

        Bags bags = bagsRepository.findById(bagsId).orElseThrow(
                () -> new IllegalArgumentException("IllegalArgumentException")
        );
        Users users = bags.getRentingUsers();

        bags.setWhenIsRented(LocalDateTime.MIN);
        bags.setRentingUsers(null);
        bags.setRented(false);

        users.getReturningBagsList().remove(bags);
        users.getReturnedBagsList().add(bags);

        return successMockResponseDto;
    }

}
