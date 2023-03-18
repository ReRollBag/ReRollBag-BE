package com.ReRollBag.repository;

import com.ReRollBag.domain.entity.UsersBagsRentingHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersBagsRentingHistoryRepository extends MongoRepository<UsersBagsRentingHistory, String> {
}
