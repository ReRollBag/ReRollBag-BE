package com.ReRollBag.service;

import com.ReRollBag.domain.BagsCount;
import com.ReRollBag.domain.dto.Bags.BagsRentOrReturnRequestDto;
import com.ReRollBag.domain.dto.Bags.BagsRentingHistoryDto;
import com.ReRollBag.domain.dto.Bags.BagsResponseDto;
import com.ReRollBag.domain.dto.Bags.BagsSaveRequestDto;
import com.ReRollBag.domain.dto.MockResponseDto;
import com.ReRollBag.domain.entity.Bags;
import com.ReRollBag.domain.entity.Users;
import com.ReRollBag.domain.entity.UsersBagsRentingHistory;
import com.ReRollBag.exceptions.bagsExceptions.AlreadyRentedException;
import com.ReRollBag.exceptions.bagsExceptions.ReturnRequestUserMismatchException;
import com.ReRollBag.repository.BagsRepository;
import com.ReRollBag.repository.UsersBagsRentingHistoryRepository;
import com.ReRollBag.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BagsService {

    private final BagsRepository bagsRepository;
    private final UsersRepository usersRepository;
    private final UsersBagsRentingHistoryRepository usersBagsRentingHistoryRepository;
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


    public MockResponseDto renting(BagsRentOrReturnRequestDto requestDto) throws AlreadyRentedException {
        String bagsId = requestDto.getBagsId();
        String usersId = requestDto.getUsersId();

        Users users = usersRepository.findByUsersId(usersId);
        Bags bags = bagsRepository.findById(bagsId).orElseThrow(
                () -> new IllegalArgumentException("IllegalArgumentException")
        );

        if (users.getRentingBagsList().contains(bags))
            System.out.println("User already have bags!");

        if (bags.isRented())
            throw new AlreadyRentedException();

        bags.setRentingUsers(users);
        bags.setRented(true);
        bags.setWhenIsRented(LocalDateTime.now());

        users.getRentingBagsList().add(bags);

        usersRepository.save(users);
        bagsRepository.save(bags);

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

        bags.setRentingUsers(null);
        bags.setReturningUsers(users);

        usersRepository.save(users);
        bagsRepository.save(bags);

        System.out.println("users.getReturningBagsList() size : " + users.getReturningBagsList().size());

        return successMockResponseDto;
    }

    public MockResponseDto returning(String bagsId) {
        //Find Bags and Returning Users
        Bags bags = bagsRepository.findById(bagsId).orElseThrow(
                () -> new IllegalArgumentException("IllegalArgumentException")
        );
        Users returningUsers = bags.getReturningUsers();

        //Get UID from Users
        String UID = returningUsers.getUID();

        //Check if there is UsersBagsRentingHistory.
        if (!usersBagsRentingHistoryRepository.existsById(UID))
            saveUsersBagsRentingHistory(UID);

        //Find UsersBagsRentingHistory
        UsersBagsRentingHistory usersBagsRentingHistory = usersBagsRentingHistoryRepository.findById(UID).orElseThrow(
                () -> new IllegalArgumentException("IllegalArgumentException")
        );

        //Add RentingHistory
        BagsResponseDto responseDto = new BagsResponseDto(bags);
        responseDto.setRentingUsersId(usersRepository.findById(UID).get().getUsersId());
        usersBagsRentingHistory.getUsersBagsRentingHistory().add(new BagsRentingHistoryDto(bags));

        //Disconnect Bags and Users, Change Bags Info
        bags.setReturningUsers(null);
        returningUsers.getReturningBagsList().remove(bags);
        bags.setRented(false);
        bags.setWhenIsRented(LocalDateTime.MIN);

        //Save Entity for Update
        usersRepository.save(returningUsers);
        bagsRepository.save(bags);
        usersBagsRentingHistoryRepository.save(usersBagsRentingHistory);

        return successMockResponseDto;
    }

    private void saveUsersBagsRentingHistory(String UID) {
        UsersBagsRentingHistory usersBagsRentingHistory = new UsersBagsRentingHistory(UID);
        usersBagsRentingHistoryRepository.save(usersBagsRentingHistory);
    }

    public BagsResponseDto findById(String bagsId) {
        Bags bags = bagsRepository.findById(bagsId).orElseThrow(
                () -> new IllegalArgumentException("IllegalArgumentException")
        );
        return new BagsResponseDto(bags);
    }

}
