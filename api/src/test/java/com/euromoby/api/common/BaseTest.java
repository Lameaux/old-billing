package com.euromoby.api.common;

import com.euromoby.api.TestDataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class BaseTest {
    @Autowired
    TestDataLoader testDataLoader;

    @BeforeEach
    public void setUp() {
        testDataLoader.cleanupDatabase();
    }
}
