package com.fizzlesmp.fizzle_enchanting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ModBootTest {
    @Test
    void modId_matchesExpected() {
        assertEquals("fizzle_enchanting", FizzleEnchanting.MOD_ID);
    }

    @Test
    void logger_isInitialized() {
        assertNotNull(FizzleEnchanting.LOGGER);
    }

    @Test
    void id_producesNamespacedResourceLocation() {
        assertEquals("fizzle_enchanting", FizzleEnchanting.id("test").getNamespace());
        assertEquals("test", FizzleEnchanting.id("test").getPath());
    }
}
