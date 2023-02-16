package com.dyrmig.banking.repository;

import com.dyrmig.banking.model.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {
    @Query("SELECT a FROM AccountHolder a JOIN FETCH ownedAccountList WHERE a.id = :id")
    Optional<AccountHolder> accountHolderWithAccounts(@Param("id") Long id);
    @Query("SELECT a FROM AccountHolder a JOIN FETCH secondaryOwnedAccountList WHERE a.id = :id")
    Optional<AccountHolder> accountHolderWithSecondaryAccounts(@Param("id") Long id);
    Optional<AccountHolder> findByUsername(String username);
}
