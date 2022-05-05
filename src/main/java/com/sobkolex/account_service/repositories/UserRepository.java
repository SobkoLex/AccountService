package com.sobkolex.account_service.repositories;

import com.sobkolex.account_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    int deleteByEmail(String email);

}
