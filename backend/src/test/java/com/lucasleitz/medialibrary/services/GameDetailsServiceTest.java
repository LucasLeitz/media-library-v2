package com.lucasleitz.medialibrary.services;

import com.lucasleitz.medialibrary.support.IntegrationTestBase;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class GameDetailsServiceTest extends IntegrationTestBase {

    @Autowired
    private EntityManager em;


}
