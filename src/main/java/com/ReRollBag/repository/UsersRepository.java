package com.ReRollBag.repository;

import com.ReRollBag.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByUsersId(String usersId);
    Boolean existsByUsersId(String usersId);
}
