package com.lucasleitz.medialibrary;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaLibraryApplicationTest {

    @Test
    void constructorLoads() {
        MediaLibraryApplication app = new MediaLibraryApplication();
        assertNotNull(app);
    }

}