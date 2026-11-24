package geoactivity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SanityTest {
    @Test
    void idUsesModNamespace() {
        assertEquals("geoactivity:lignite_ore", GeoActivity.id("lignite_ore").toString());
    }
}
