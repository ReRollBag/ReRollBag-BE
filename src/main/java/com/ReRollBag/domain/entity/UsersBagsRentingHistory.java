package com.ReRollBag.domain.entity;

import com.ReRollBag.domain.dto.Bags.BagsResponseDto;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Document(collation = "UsersBagsRentingHistory")
public class UsersBagsRentingHistory {
    @Id
    public String UID;

    public List<BagsResponseDto> usersBagsRentingHistory = new ArrayList<>();

    public void addRentingHistory(BagsResponseDto responseDto) {
        usersBagsRentingHistory.add(responseDto);
    }
}


