package com.ReRollBag.domain.entity;

import com.ReRollBag.domain.dto.Bags.BagsRentingHistoryDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Document(collection = "UsersBagsRentingHistory")
public class UsersBagsRentingHistory {
    @Id
    private String UID;

    private List<BagsRentingHistoryDto> usersBagsRentingHistory = new ArrayList<>();

    public UsersBagsRentingHistory(String UID) {
        this.UID = UID;
        usersBagsRentingHistory = new ArrayList<>();
    }
}


