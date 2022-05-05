package com.sobkolex.account_service.repositories;

import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Data
@Repository
public class BreachedPasswordsRepositories {

    private List<String> breachedPasswords;

    {
        breachedPasswords = new ArrayList<>(List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"));
    }
}
