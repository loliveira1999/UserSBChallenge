package com.beginsecure.usersbchallenge.Persistence.Repository;

import com.beginsecure.usersbchallenge.Persistence.Entity.UsersEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, Integer> {

    // @Override
    @Query("SELECT u FROM UsersEntity u ORDER BY u.id ASC")
    List<UsersEntity> getAllUsers();

    @Query("SELECT u FROM UsersEntity u WHERE u.ID = :ID ORDER BY u.ID ASC")
    UsersEntity getUserByID(@Param("ID") Integer ID);

    @Query("SELECT u FROM UsersEntity u WHERE (:ID IS NULL OR u.ID != :ID) AND UPPER(u.email) = UPPER(:email) AND u.isActive = true")
    List<UsersEntity> getUserWEmail(@Param("email") String email, @Param("ID") Integer ID, Pageable pageable);

    @Query("SELECT u FROM UsersEntity u WHERE u.ID = :ID AND u.isActive = true ORDER BY u.ID ASC")
    UsersEntity getActiveUserByID(@Param("ID") Integer ID);
}
