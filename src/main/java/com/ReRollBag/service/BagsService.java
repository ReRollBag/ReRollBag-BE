package com.ReRollBag.service;

import com.ReRollBag.domain.BagsCount;
import com.ReRollBag.domain.dto.Bags.BagsResponseDto;
import com.ReRollBag.domain.dto.Bags.BagsSaveRequestDto;
import com.ReRollBag.domain.entity.Bags;
import com.ReRollBag.repository.BagsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BagsService {

    private final BagsRepository bagsRepository;
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

        bagsRepository.save(saveTarget);
        return new BagsResponseDto(saveTarget);
    }
}
