package io.dropwizard.jersey.jsr310;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class ZoneIdParamTest {
    @Test
    void parsesDateTimes() throws Exception {
        final ZoneIdParam param = new ZoneIdParam("Europe/Berlin");

        assertThat(param.get())
                .isEqualTo(ZoneId.of("Europe/Berlin"));
    }
}
