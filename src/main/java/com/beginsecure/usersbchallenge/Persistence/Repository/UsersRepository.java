package com.beginsecure.usersbchallenge.Persistence.Repository;

import com.beginsecure.usersbchallenge.Persistence.Entity.UsersEntity;
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

    @Query("SELECT u FROM UsersEntity u WHERE u.ID != :ID AND u.email = :email AND u.isActive = true")
    UsersEntity getUserWSameEmail(@Param("ID") Integer ID, @Param("email") String email);

    @Query("SELECT u FROM UsersEntity u WHERE u.ID = :ID AND u.isActive = true ORDER BY u.ID ASC")
    UsersEntity getActiveUserByID(@Param("ID") Integer ID);
}
