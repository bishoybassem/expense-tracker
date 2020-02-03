package com.myprojects.expense.authenticator.dao;

import com.myprojects.expense.authenticator.model.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserDao extends CrudRepository<AppUser, UUID> {

    Optional<AppUser> findByEmail(String email);

}
