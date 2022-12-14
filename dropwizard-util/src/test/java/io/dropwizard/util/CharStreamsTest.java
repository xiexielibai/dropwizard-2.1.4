package io.dropwizard.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

class CharStreamsTest {

    @Test
    void testToString() throws IOException {
        StringBuilder builder = new StringBuilder(4100);
        for (int i = 0; i < 4100; i++) {
             builder.append("a");
        }
        String s = builder.toString();
        assertThat(CharStreams.toString(new StringReader(s))).isEqualTo(s);
    }
}
