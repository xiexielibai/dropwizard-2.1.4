package io.dropwizard.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@SuppressWarnings("deprecation")
class SizeTest {
    @Test
    void convertsToTerabytes() {
        assertThat(Size.terabytes(2).toTerabytes())
                .isEqualTo(2);
    }

    @Test
    void convertsToGigabytes() {
        assertThat(Size.terabytes(2).toGigabytes())
                .isEqualTo(2048);
    }

    @Test
    void convertsToMegabytes() {
        assertThat(Size.gigabytes(2).toMegabytes())
                .isEqualTo(2048);
    }

    @Test
    void convertsToKilobytes() {
        assertThat(Size.megabytes(2).toKilobytes())
                .isEqualTo(2048);
    }

    @Test
    void convertsToBytes() {
        assertThat(Size.kilobytes(2).toBytes())
                .isEqualTo(2048L);
    }

    @Test
    void parsesTerabytes() {
        assertThat(Size.parse("2TB"))
                .isEqualTo(Size.terabytes(2));

        assertThat(Size.parse("2TiB"))
                .isEqualTo(Size.terabytes(2));

        assertThat(Size.parse("1 terabyte"))
                .isEqualTo(Size.terabytes(1));

        assertThat(Size.parse("2 terabytes"))
                .isEqualTo(Size.terabytes(2));
    }

    @Test
    void parsesGigabytes() {
        assertThat(Size.parse("2GB"))
                .isEqualTo(Size.gigabytes(2));

        assertThat(Size.parse("2GiB"))
                .isEqualTo(Size.gigabytes(2));

        assertThat(Size.parse("1 gigabyte"))
                .isEqualTo(Size.gigabytes(1));

        assertThat(Size.parse("2 gigabytes"))
                .isEqualTo(Size.gigabytes(2));
    }

    @Test
    void parsesMegabytes() {
        assertThat(Size.parse("2MB"))
                .isEqualTo(Size.megabytes(2));

        assertThat(Size.parse("2MiB"))
                .isEqualTo(Size.megabytes(2));

        assertThat(Size.parse("1 megabyte"))
                .isEqualTo(Size.megabytes(1));

        assertThat(Size.parse("2 megabytes"))
                .isEqualTo(Size.megabytes(2));
    }

    @Test
    void parsesKilobytes() {
        assertThat(Size.parse("2KB"))
                .isEqualTo(Size.kilobytes(2));

        assertThat(Size.parse("2KiB"))
                .isEqualTo(Size.kilobytes(2));

        assertThat(Size.parse("1 kilobyte"))
                .isEqualTo(Size.kilobytes(1));

        assertThat(Size.parse("2 kilobytes"))
                .isEqualTo(Size.kilobytes(2));
    }

    @Test
    void parsesBytes() {
        assertThat(Size.parse("2B"))
                .isEqualTo(Size.bytes(2));

        assertThat(Size.parse("1 byte"))
                .isEqualTo(Size.bytes(1));

        assertThat(Size.parse("2 bytes"))
                .isEqualTo(Size.bytes(2));
    }

    @Test
    void parseSizeWithWhiteSpaces() {
        assertThat(Size.parse("64   kilobytes"))
                .isEqualTo(Size.kilobytes(64));
    }

    @Test
    void parseCaseInsensitive() {
        assertThat(Size.parse("1b")).isEqualTo(Size.parse("1B"));
    }

    @Test
    void parseSingleLetterSuffix() {
        assertThat(Size.parse("1B")).isEqualTo(Size.bytes(1));
        assertThat(Size.parse("1K")).isEqualTo(Size.kilobytes(1));
        assertThat(Size.parse("1M")).isEqualTo(Size.megabytes(1));
        assertThat(Size.parse("1G")).isEqualTo(Size.gigabytes(1));
        assertThat(Size.parse("1T")).isEqualTo(Size.terabytes(1));
    }

    @Test
    void unableParseWrongSizeCount() {
        assertThatIllegalArgumentException().isThrownBy(() -> Size.parse("three bytes"));
    }

    @Test
    void unableParseWrongSizeUnit() {
        assertThatIllegalArgumentException().isThrownBy(() -> Size.parse("1EB"));
    }

    @Test
    void unableParseWrongSizeFormat() {
        assertThatIllegalArgumentException().isThrownBy(() -> Size.parse("1 mega byte"));
    }

    @Test
    void isHumanReadable() {
        assertThat(Size.gigabytes(3))
                .hasToString("3 gigabytes");

        assertThat(Size.kilobytes(1))
                .hasToString("1 kilobyte");
    }

    @Test
    void hasAQuantity() {
        assertThat(Size.gigabytes(3).getQuantity())
                .isEqualTo(3);
    }

    @Test
    void hasAUnit() {
        assertThat(Size.gigabytes(3).getUnit())
                .isEqualTo(SizeUnit.GIGABYTES);
    }

    @Test
    void isComparable() {
        // both zero
        assertThat(Size.bytes(0)).isEqualByComparingTo(Size.bytes(0));
        assertThat(Size.bytes(0)).isEqualByComparingTo(Size.kilobytes(0));
        assertThat(Size.bytes(0)).isEqualByComparingTo(Size.megabytes(0));
        assertThat(Size.bytes(0)).isEqualByComparingTo(Size.gigabytes(0));
        assertThat(Size.bytes(0)).isEqualByComparingTo(Size.terabytes(0));

        assertThat(Size.kilobytes(0)).isEqualByComparingTo(Size.bytes(0));
        assertThat(Size.kilobytes(0)).isEqualByComparingTo(Size.kilobytes(0));
        assertThat(Size.kilobytes(0)).isEqualByComparingTo(Size.megabytes(0));
        assertThat(Size.kilobytes(0)).isEqualByComparingTo(Size.gigabytes(0));
        assertThat(Size.kilobytes(0)).isEqualByComparingTo(Size.terabytes(0));

        assertThat(Size.megabytes(0)).isEqualByComparingTo(Size.bytes(0));
        assertThat(Size.megabytes(0)).isEqualByComparingTo(Size.kilobytes(0));
        assertThat(Size.megabytes(0)).isEqualByComparingTo(Size.megabytes(0));
        assertThat(Size.megabytes(0)).isEqualByComparingTo(Size.gigabytes(0));
        assertThat(Size.megabytes(0)).isEqualByComparingTo(Size.terabytes(0));

        assertThat(Size.gigabytes(0)).isEqualByComparingTo(Size.bytes(0));
        assertThat(Size.gigabytes(0)).isEqualByComparingTo(Size.kilobytes(0));
        assertThat(Size.gigabytes(0)).isEqualByComparingTo(Size.megabytes(0));
        assertThat(Size.gigabytes(0)).isEqualByComparingTo(Size.gigabytes(0));
        assertThat(Size.gigabytes(0)).isEqualByComparingTo(Size.terabytes(0));

        assertThat(Size.terabytes(0)).isEqualByComparingTo(Size.bytes(0));
        assertThat(Size.terabytes(0)).isEqualByComparingTo(Size.kilobytes(0));
        assertThat(Size.terabytes(0)).isEqualByComparingTo(Size.megabytes(0));
        assertThat(Size.terabytes(0)).isEqualByComparingTo(Size.gigabytes(0));
        assertThat(Size.terabytes(0)).isEqualByComparingTo(Size.terabytes(0));

        // one zero, one negative
        assertThat(Size.bytes(0)).isGreaterThan(Size.bytes(-1));
        assertThat(Size.bytes(0)).isGreaterThan(Size.kilobytes(-1));
        assertThat(Size.bytes(0)).isGreaterThan(Size.megabytes(-1));
        assertThat(Size.bytes(0)).isGreaterThan(Size.gigabytes(-1));
        assertThat(Size.bytes(0)).isGreaterThan(Size.terabytes(-1));

        assertThat(Size.kilobytes(0)).isGreaterThan(Size.bytes(-1));
        assertThat(Size.kilobytes(0)).isGreaterThan(Size.kilobytes(-1));
        assertThat(Size.kilobytes(0)).isGreaterThan(Size.megabytes(-1));
        assertThat(Size.kilobytes(0)).isGreaterThan(Size.gigabytes(-1));
        assertThat(Size.kilobytes(0)).isGreaterThan(Size.terabytes(-1));

        assertThat(Size.megabytes(0)).isGreaterThan(Size.bytes(-1));
        assertThat(Size.megabytes(0)).isGreaterThan(Size.kilobytes(-1));
        assertThat(Size.megabytes(0)).isGreaterThan(Size.megabytes(-1));
        assertThat(Size.megabytes(0)).isGreaterThan(Size.gigabytes(-1));
        assertThat(Size.megabytes(0)).isGreaterThan(Size.terabytes(-1));

        assertThat(Size.gigabytes(0)).isGreaterThan(Size.bytes(-1));
        assertThat(Size.gigabytes(0)).isGreaterThan(Size.kilobytes(-1));
        assertThat(Size.gigabytes(0)).isGreaterThan(Size.megabytes(-1));
        assertThat(Size.gigabytes(0)).isGreaterThan(Size.gigabytes(-1));
        assertThat(Size.gigabytes(0)).isGreaterThan(Size.terabytes(-1));

        assertThat(Size.terabytes(0)).isGreaterThan(Size.bytes(-1));
        assertThat(Size.terabytes(0)).isGreaterThan(Size.kilobytes(-1));
        assertThat(Size.terabytes(0)).isGreaterThan(Size.megabytes(-1));
        assertThat(Size.terabytes(0)).isGreaterThan(Size.gigabytes(-1));
        assertThat(Size.terabytes(0)).isGreaterThan(Size.terabytes(-1));

        assertThat(Size.bytes(-1)).isLessThan(Size.bytes(0));
        assertThat(Size.bytes(-1)).isLessThan(Size.kilobytes(0));
        assertThat(Size.bytes(-1)).isLessThan(Size.megabytes(0));
        assertThat(Size.bytes(-1)).isLessThan(Size.gigabytes(0));
        assertThat(Size.bytes(-1)).isLessThan(Size.terabytes(0));

        assertThat(Size.kilobytes(-1)).isLessThan(Size.bytes(0));
        assertThat(Size.kilobytes(-1)).isLessThan(Size.kilobytes(0));
        assertThat(Size.kilobytes(-1)).isLessThan(Size.megabytes(0));
        assertThat(Size.kilobytes(-1)).isLessThan(Size.gigabytes(0));
        assertThat(Size.kilobytes(-1)).isLessThan(Size.terabytes(0));

        assertThat(Size.megabytes(-1)).isLessThan(Size.bytes(0));
        assertThat(Size.megabytes(-1)).isLessThan(Size.kilobytes(0));
        assertThat(Size.megabytes(-1)).isLessThan(Size.megabytes(0));
        assertThat(Size.megabytes(-1)).isLessThan(Size.gigabytes(0));
        assertThat(Size.megabytes(-1)).isLessThan(Size.terabytes(0));

        assertThat(Size.gigabytes(-1)).isLessThan(Size.bytes(0));
        assertThat(Size.gigabytes(-1)).isLessThan(Size.kilobytes(0));
        assertThat(Size.gigabytes(-1)).isLessThan(Size.megabytes(0));
        assertThat(Size.gigabytes(-1)).isLessThan(Size.gigabytes(0));
        assertThat(Size.gigabytes(-1)).isLessThan(Size.terabytes(0));

        assertThat(Size.terabytes(-1)).isLessThan(Size.bytes(0));
        assertThat(Size.terabytes(-1)).isLessThan(Size.kilobytes(0));
        assertThat(Size.terabytes(-1)).isLessThan(Size.megabytes(0));
        assertThat(Size.terabytes(-1)).isLessThan(Size.gigabytes(0));
        assertThat(Size.terabytes(-1)).isLessThan(Size.terabytes(0));

        // one zero, one positive
        assertThat(Size.bytes(0)).isLessThan(Size.bytes(1));
        assertThat(Size.bytes(0)).isLessThan(Size.kilobytes(1));
        assertThat(Size.bytes(0)).isLessThan(Size.megabytes(1));
        assertThat(Size.bytes(0)).isLessThan(Size.gigabytes(1));
        assertThat(Size.bytes(0)).isLessThan(Size.terabytes(1));

        assertThat(Size.kilobytes(0)).isLessThan(Size.bytes(1));
        assertThat(Size.kilobytes(0)).isLessThan(Size.kilobytes(1));
        assertThat(Size.kilobytes(0)).isLessThan(Size.megabytes(1));
        assertThat(Size.kilobytes(0)).isLessThan(Size.gigabytes(1));
        assertThat(Size.kilobytes(0)).isLessThan(Size.terabytes(1));

        assertThat(Size.megabytes(0)).isLessThan(Size.bytes(1));
        assertThat(Size.megabytes(0)).isLessThan(Size.kilobytes(1));
        assertThat(Size.megabytes(0)).isLessThan(Size.megabytes(1));
        assertThat(Size.megabytes(0)).isLessThan(Size.gigabytes(1));
        assertThat(Size.megabytes(0)).isLessThan(Size.terabytes(1));

        assertThat(Size.gigabytes(0)).isLessThan(Size.bytes(1));
        assertThat(Size.gigabytes(0)).isLessThan(Size.kilobytes(1));
        assertThat(Size.gigabytes(0)).isLessThan(Size.megabytes(1));
        assertThat(Size.gigabytes(0)).isLessThan(Size.gigabytes(1));
        assertThat(Size.gigabytes(0)).isLessThan(Size.terabytes(1));

        assertThat(Size.terabytes(0)).isLessThan(Size.bytes(1));
        assertThat(Size.terabytes(0)).isLessThan(Size.kilobytes(1));
        assertThat(Size.terabytes(0)).isLessThan(Size.megabytes(1));
        assertThat(Size.terabytes(0)).isLessThan(Size.gigabytes(1));
        assertThat(Size.terabytes(0)).isLessThan(Size.terabytes(1));

        assertThat(Size.bytes(1)).isGreaterThan(Size.bytes(0));
        assertThat(Size.bytes(1)).isGreaterThan(Size.kilobytes(0));
        assertThat(Size.bytes(1)).isGreaterThan(Size.megabytes(0));
        assertThat(Size.bytes(1)).isGreaterThan(Size.gigabytes(0));
        assertThat(Size.bytes(1)).isGreaterThan(Size.terabytes(0));

        assertThat(Size.kilobytes(1)).isGreaterThan(Size.bytes(0));
        assertThat(Size.kilobytes(1)).isGreaterThan(Size.kilobytes(0));
        assertThat(Size.kilobytes(1)).isGreaterThan(Size.megabytes(0));
        assertThat(Size.kilobytes(1)).isGreaterThan(Size.gigabytes(0));
        assertThat(Size.kilobytes(1)).isGreaterThan(Size.terabytes(0));

        assertThat(Size.megabytes(1)).isGreaterThan(Size.bytes(0));
        assertThat(Size.megabytes(1)).isGreaterThan(Size.kilobytes(0));
        assertThat(Size.megabytes(1)).isGreaterThan(Size.megabytes(0));
        assertThat(Size.megabytes(1)).isGreaterThan(Size.gigabytes(0));
        assertThat(Size.megabytes(1)).isGreaterThan(Size.terabytes(0));

        assertThat(Size.gigabytes(1)).isGreaterThan(Size.bytes(0));
        assertThat(Size.gigabytes(1)).isGreaterThan(Size.kilobytes(0));
        assertThat(Size.gigabytes(1)).isGreaterThan(Size.megabytes(0));
        assertThat(Size.gigabytes(1)).isGreaterThan(Size.gigabytes(0));
        assertThat(Size.gigabytes(1)).isGreaterThan(Size.terabytes(0));

        assertThat(Size.terabytes(1)).isGreaterThan(Size.bytes(0));
        assertThat(Size.terabytes(1)).isGreaterThan(Size.kilobytes(0));
        assertThat(Size.terabytes(1)).isGreaterThan(Size.megabytes(0));
        assertThat(Size.terabytes(1)).isGreaterThan(Size.gigabytes(0));
        assertThat(Size.terabytes(1)).isGreaterThan(Size.terabytes(0));

        // both negative
        assertThat(Size.bytes(-2)).isLessThan(Size.bytes(-1));
        assertThat(Size.bytes(-2)).isGreaterThan(Size.kilobytes(-1));
        assertThat(Size.bytes(-2)).isGreaterThan(Size.megabytes(-1));
        assertThat(Size.bytes(-2)).isGreaterThan(Size.gigabytes(-1));
        assertThat(Size.bytes(-2)).isGreaterThan(Size.terabytes(-1));

        assertThat(Size.kilobytes(-2)).isLessThan(Size.bytes(-1));
        assertThat(Size.kilobytes(-2)).isLessThan(Size.kilobytes(-1));
        assertThat(Size.kilobytes(-2)).isGreaterThan(Size.megabytes(-1));
        assertThat(Size.kilobytes(-2)).isGreaterThan(Size.gigabytes(-1));
        assertThat(Size.kilobytes(-2)).isGreaterThan(Size.terabytes(-1));

        assertThat(Size.megabytes(-2)).isLessThan(Size.bytes(-1));
        assertThat(Size.megabytes(-2)).isLessThan(Size.kilobytes(-1));
        assertThat(Size.megabytes(-2)).isLessThan(Size.megabytes(-1));
        assertThat(Size.megabytes(-2)).isGreaterThan(Size.gigabytes(-1));
        assertThat(Size.megabytes(-2)).isGreaterThan(Size.terabytes(-1));

        assertThat(Size.gigabytes(-2)).isLessThan(Size.bytes(-1));
        assertThat(Size.gigabytes(-2)).isLessThan(Size.kilobytes(-1));
        assertThat(Size.gigabytes(-2)).isLessThan(Size.megabytes(-1));
        assertThat(Size.gigabytes(-2)).isLessThan(Size.gigabytes(-1));
        assertThat(Size.gigabytes(-2)).isGreaterThan(Size.terabytes(-1));

        assertThat(Size.terabytes(-2)).isLessThan(Size.bytes(-1));
        assertThat(Size.terabytes(-2)).isLessThan(Size.kilobytes(-1));
        assertThat(Size.terabytes(-2)).isLessThan(Size.megabytes(-1));
        assertThat(Size.terabytes(-2)).isLessThan(Size.gigabytes(-1));
        assertThat(Size.terabytes(-2)).isLessThan(Size.terabytes(-1));

        assertThat(Size.bytes(-1)).isGreaterThan(Size.bytes(-2));
        assertThat(Size.bytes(-1)).isGreaterThan(Size.kilobytes(-2));
        assertThat(Size.bytes(-1)).isGreaterThan(Size.megabytes(-2));
        assertThat(Size.bytes(-1)).isGreaterThan(Size.gigabytes(-2));
        assertThat(Size.bytes(-1)).isGreaterThan(Size.terabytes(-2));

        assertThat(Size.kilobytes(-1)).isLessThan(Size.bytes(-2));
        assertThat(Size.kilobytes(-1)).isGreaterThan(Size.kilobytes(-2));
        assertThat(Size.kilobytes(-1)).isGreaterThan(Size.megabytes(-2));
        assertThat(Size.kilobytes(-1)).isGreaterThan(Size.gigabytes(-2));
        assertThat(Size.kilobytes(-1)).isGreaterThan(Size.terabytes(-2));

        assertThat(Size.megabytes(-1)).isLessThan(Size.bytes(-2));
        assertThat(Size.megabytes(-1)).isLessThan(Size.kilobytes(-2));
        assertThat(Size.megabytes(-1)).isGreaterThan(Size.megabytes(-2));
        assertThat(Size.megabytes(-1)).isGreaterThan(Size.gigabytes(-2));
        assertThat(Size.megabytes(-1)).isGreaterThan(Size.terabytes(-2));

        assertThat(Size.gigabytes(-1)).isLessThan(Size.bytes(-2));
        assertThat(Size.gigabytes(-1)).isLessThan(Size.kilobytes(-2));
        assertThat(Size.gigabytes(-1)).isLessThan(Size.megabytes(-2));
        assertThat(Size.gigabytes(-1)).isGreaterThan(Size.gigabytes(-2));
        assertThat(Size.gigabytes(-1)).isGreaterThan(Size.terabytes(-2));

        assertThat(Size.terabytes(-1)).isLessThan(Size.bytes(-2));
        assertThat(Size.terabytes(-1)).isLessThan(Size.kilobytes(-2));
        assertThat(Size.terabytes(-1)).isLessThan(Size.megabytes(-2));
        assertThat(Size.terabytes(-1)).isLessThan(Size.gigabytes(-2));
        assertThat(Size.terabytes(-1)).isGreaterThan(Size.terabytes(-2));

        // both positive
        assertThat(Size.bytes(1)).isLessThan(Size.bytes(2));
        assertThat(Size.bytes(1)).isLessThan(Size.kilobytes(2));
        assertThat(Size.bytes(1)).isLessThan(Size.megabytes(2));
        assertThat(Size.bytes(1)).isLessThan(Size.gigabytes(2));
        assertThat(Size.bytes(1)).isLessThan(Size.terabytes(2));

        assertThat(Size.kilobytes(1)).isGreaterThan(Size.bytes(2));
        assertThat(Size.kilobytes(1)).isLessThan(Size.kilobytes(2));
        assertThat(Size.kilobytes(1)).isLessThan(Size.megabytes(2));
        assertThat(Size.kilobytes(1)).isLessThan(Size.gigabytes(2));
        assertThat(Size.kilobytes(1)).isLessThan(Size.terabytes(2));

        assertThat(Size.megabytes(1)).isGreaterThan(Size.bytes(2));
        assertThat(Size.megabytes(1)).isGreaterThan(Size.kilobytes(2));
        assertThat(Size.megabytes(1)).isLessThan(Size.megabytes(2));
        assertThat(Size.megabytes(1)).isLessThan(Size.gigabytes(2));
        assertThat(Size.megabytes(1)).isLessThan(Size.terabytes(2));

        assertThat(Size.gigabytes(1)).isGreaterThan(Size.bytes(2));
        assertThat(Size.gigabytes(1)).isGreaterThan(Size.kilobytes(2));
        assertThat(Size.gigabytes(1)).isGreaterThan(Size.megabytes(2));
        assertThat(Size.gigabytes(1)).isLessThan(Size.gigabytes(2));
        assertThat(Size.gigabytes(1)).isLessThan(Size.terabytes(2));

        assertThat(Size.terabytes(1)).isGreaterThan(Size.bytes(2));
        assertThat(Size.terabytes(1)).isGreaterThan(Size.kilobytes(2));
        assertThat(Size.terabytes(1)).isGreaterThan(Size.megabytes(2));
        assertThat(Size.terabytes(1)).isGreaterThan(Size.gigabytes(2));
        assertThat(Size.terabytes(1)).isLessThan(Size.terabytes(2));

        assertThat(Size.bytes(2)).isGreaterThan(Size.bytes(1));
        assertThat(Size.bytes(2)).isLessThan(Size.kilobytes(1));
        assertThat(Size.bytes(2)).isLessThan(Size.megabytes(1));
        assertThat(Size.bytes(2)).isLessThan(Size.gigabytes(1));
        assertThat(Size.bytes(2)).isLessThan(Size.terabytes(1));

        assertThat(Size.kilobytes(2)).isGreaterThan(Size.bytes(1));
        assertThat(Size.kilobytes(2)).isGreaterThan(Size.kilobytes(1));
        assertThat(Size.kilobytes(2)).isLessThan(Size.megabytes(1));
        assertThat(Size.kilobytes(2)).isLessThan(Size.gigabytes(1));
        assertThat(Size.kilobytes(2)).isLessThan(Size.terabytes(1));

        assertThat(Size.megabytes(2)).isGreaterThan(Size.bytes(1));
        assertThat(Size.megabytes(2)).isGreaterThan(Size.kilobytes(1));
        assertThat(Size.megabytes(2)).isGreaterThan(Size.megabytes(1));
        assertThat(Size.megabytes(2)).isLessThan(Size.gigabytes(1));
        assertThat(Size.megabytes(2)).isLessThan(Size.terabytes(1));

        assertThat(Size.gigabytes(2)).isGreaterThan(Size.bytes(1));
        assertThat(Size.gigabytes(2)).isGreaterThan(Size.kilobytes(1));
        assertThat(Size.gigabytes(2)).isGreaterThan(Size.megabytes(1));
        assertThat(Size.gigabytes(2)).isGreaterThan(Size.gigabytes(1));
        assertThat(Size.gigabytes(2)).isLessThan(Size.terabytes(1));

        assertThat(Size.terabytes(2)).isGreaterThan(Size.bytes(1));
        assertThat(Size.terabytes(2)).isGreaterThan(Size.kilobytes(1));
        assertThat(Size.terabytes(2)).isGreaterThan(Size.megabytes(1));
        assertThat(Size.terabytes(2)).isGreaterThan(Size.gigabytes(1));
        assertThat(Size.terabytes(2)).isGreaterThan(Size.terabytes(1));

        // one negative, one positive
        assertThat(Size.bytes(-1)).isLessThan(Size.bytes(1));
        assertThat(Size.bytes(-1)).isLessThan(Size.kilobytes(1));
        assertThat(Size.bytes(-1)).isLessThan(Size.megabytes(1));
        assertThat(Size.bytes(-1)).isLessThan(Size.gigabytes(1));
        assertThat(Size.bytes(-1)).isLessThan(Size.terabytes(1));

        assertThat(Size.kilobytes(-1)).isLessThan(Size.bytes(1));
        assertThat(Size.kilobytes(-1)).isLessThan(Size.kilobytes(1));
        assertThat(Size.kilobytes(-1)).isLessThan(Size.megabytes(1));
        assertThat(Size.kilobytes(-1)).isLessThan(Size.gigabytes(1));
        assertThat(Size.kilobytes(-1)).isLessThan(Size.terabytes(1));

        assertThat(Size.megabytes(-1)).isLessThan(Size.bytes(1));
        assertThat(Size.megabytes(-1)).isLessThan(Size.kilobytes(1));
        assertThat(Size.megabytes(-1)).isLessThan(Size.megabytes(1));
        assertThat(Size.megabytes(-1)).isLessThan(Size.gigabytes(1));
        assertThat(Size.megabytes(-1)).isLessThan(Size.terabytes(1));

        assertThat(Size.gigabytes(-1)).isLessThan(Size.bytes(1));
        assertThat(Size.gigabytes(-1)).isLessThan(Size.kilobytes(1));
        assertThat(Size.gigabytes(-1)).isLessThan(Size.megabytes(1));
        assertThat(Size.gigabytes(-1)).isLessThan(Size.gigabytes(1));
        assertThat(Size.gigabytes(-1)).isLessThan(Size.terabytes(1));

        assertThat(Size.terabytes(-1)).isLessThan(Size.bytes(1));
        assertThat(Size.terabytes(-1)).isLessThan(Size.kilobytes(1));
        assertThat(Size.terabytes(-1)).isLessThan(Size.megabytes(1));
        assertThat(Size.terabytes(-1)).isLessThan(Size.gigabytes(1));
        assertThat(Size.terabytes(-1)).isLessThan(Size.terabytes(1));

        assertThat(Size.bytes(1)).isGreaterThan(Size.bytes(-1));
        assertThat(Size.bytes(1)).isGreaterThan(Size.kilobytes(-1));
        assertThat(Size.bytes(1)).isGreaterThan(Size.megabytes(-1));
        assertThat(Size.bytes(1)).isGreaterThan(Size.gigabytes(-1));
        assertThat(Size.bytes(1)).isGreaterThan(Size.terabytes(-1));

        assertThat(Size.kilobytes(1)).isGreaterThan(Size.bytes(-1));
        assertThat(Size.kilobytes(1)).isGreaterThan(Size.kilobytes(-1));
        assertThat(Size.kilobytes(1)).isGreaterThan(Size.megabytes(-1));
        assertThat(Size.kilobytes(1)).isGreaterThan(Size.gigabytes(-1));
        assertThat(Size.kilobytes(1)).isGreaterThan(Size.terabytes(-1));

        assertThat(Size.megabytes(1)).isGreaterThan(Size.bytes(-1));
        assertThat(Size.megabytes(1)).isGreaterThan(Size.kilobytes(-1));
        assertThat(Size.megabytes(1)).isGreaterThan(Size.megabytes(-1));
        assertThat(Size.megabytes(1)).isGreaterThan(Size.gigabytes(-1));
        assertThat(Size.megabytes(1)).isGreaterThan(Size.terabytes(-1));

        assertThat(Size.gigabytes(1)).isGreaterThan(Size.bytes(-1));
        assertThat(Size.gigabytes(1)).isGreaterThan(Size.kilobytes(-1));
        assertThat(Size.gigabytes(1)).isGreaterThan(Size.megabytes(-1));
        assertThat(Size.gigabytes(1)).isGreaterThan(Size.gigabytes(-1));
        assertThat(Size.gigabytes(1)).isGreaterThan(Size.terabytes(-1));

        assertThat(Size.terabytes(1)).isGreaterThan(Size.bytes(-1));
        assertThat(Size.terabytes(1)).isGreaterThan(Size.kilobytes(-1));
        assertThat(Size.terabytes(1)).isGreaterThan(Size.megabytes(-1));
        assertThat(Size.terabytes(1)).isGreaterThan(Size.gigabytes(-1));
        assertThat(Size.terabytes(1)).isGreaterThan(Size.terabytes(-1));
    }

    @Test
    void serializesCorrectlyWithJackson() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        assertThat(mapper.writeValueAsString(Size.bytes(0L))).isEqualTo("\"0 bytes\"");
        assertThat(mapper.writeValueAsString(Size.bytes(1L))).isEqualTo("\"1 byte\"");
        assertThat(mapper.writeValueAsString(Size.bytes(2L))).isEqualTo("\"2 bytes\"");
        assertThat(mapper.writeValueAsString(Size.kilobytes(0L))).isEqualTo("\"0 kilobytes\"");
        assertThat(mapper.writeValueAsString(Size.kilobytes(1L))).isEqualTo("\"1 kilobyte\"");
        assertThat(mapper.writeValueAsString(Size.kilobytes(2L))).isEqualTo("\"2 kilobytes\"");
        assertThat(mapper.writeValueAsString(Size.megabytes(0L))).isEqualTo("\"0 megabytes\"");
        assertThat(mapper.writeValueAsString(Size.megabytes(1L))).isEqualTo("\"1 megabyte\"");
        assertThat(mapper.writeValueAsString(Size.megabytes(2L))).isEqualTo("\"2 megabytes\"");
        assertThat(mapper.writeValueAsString(Size.gigabytes(0L))).isEqualTo("\"0 gigabytes\"");
        assertThat(mapper.writeValueAsString(Size.gigabytes(1L))).isEqualTo("\"1 gigabyte\"");
        assertThat(mapper.writeValueAsString(Size.gigabytes(2L))).isEqualTo("\"2 gigabytes\"");
        assertThat(mapper.writeValueAsString(Size.terabytes(0L))).isEqualTo("\"0 terabytes\"");
        assertThat(mapper.writeValueAsString(Size.terabytes(1L))).isEqualTo("\"1 terabyte\"");
        assertThat(mapper.writeValueAsString(Size.terabytes(2L))).isEqualTo("\"2 terabytes\"");
    }

    @Test
    void deserializesCorrectlyWithJackson() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        assertThat(mapper.readValue("\"0 bytes\"", Size.class)).isEqualTo(Size.bytes(0L));
        assertThat(mapper.readValue("\"1 byte\"", Size.class)).isEqualTo(Size.bytes(1L));
        assertThat(mapper.readValue("\"2 bytes\"", Size.class)).isEqualTo(Size.bytes(2L));
        assertThat(mapper.readValue("\"0 kilobytes\"", Size.class)).isEqualTo(Size.kilobytes(0L));
        assertThat(mapper.readValue("\"1 kilobyte\"", Size.class)).isEqualTo(Size.kilobytes(1L));
        assertThat(mapper.readValue("\"2 kilobytes\"", Size.class)).isEqualTo(Size.kilobytes(2L));
        assertThat(mapper.readValue("\"0 megabytes\"", Size.class)).isEqualTo(Size.megabytes(0L));
        assertThat(mapper.readValue("\"1 megabyte\"", Size.class)).isEqualTo(Size.megabytes(1L));
        assertThat(mapper.readValue("\"2 megabytes\"", Size.class)).isEqualTo(Size.megabytes(2L));
        assertThat(mapper.readValue("\"0 gigabytes\"", Size.class)).isEqualTo(Size.gigabytes(0L));
        assertThat(mapper.readValue("\"1 gigabyte\"", Size.class)).isEqualTo(Size.gigabytes(1L));
        assertThat(mapper.readValue("\"2 gigabytes\"", Size.class)).isEqualTo(Size.gigabytes(2L));
        assertThat(mapper.readValue("\"0 terabytes\"", Size.class)).isEqualTo(Size.terabytes(0L));
        assertThat(mapper.readValue("\"1 terabytes\"", Size.class)).isEqualTo(Size.terabytes(1L));
        assertThat(mapper.readValue("\"2 terabytes\"", Size.class)).isEqualTo(Size.terabytes(2L));
    }

    @Test
    void verifyComparableContract() {
        final Size kb = Size.kilobytes(1024L);
        final Size bytes = Size.bytes(kb.toBytes());

        assertThat(bytes).isEqualByComparingTo(kb);
        assertThat(kb).isEqualByComparingTo(bytes);

        // If comparator == 0, then the following must be true
        assertThat(bytes).isEqualTo(kb);
        assertThat(kb).isEqualTo(bytes);
    }

    @Test
    void testFromDataSize() {
        assertThat(Size.fromDataSize(DataSize.bytes(5L))).isEqualTo(Size.bytes(5L));
        assertThat(Size.fromDataSize(DataSize.kibibytes(5L))).isEqualTo(Size.kilobytes(5L));
        assertThat(Size.fromDataSize(DataSize.kilobytes(5L))).isEqualTo(Size.bytes(5L * 1000L));
        assertThat(Size.fromDataSize(DataSize.mebibytes(5L))).isEqualTo(Size.megabytes(5L));
        assertThat(Size.fromDataSize(DataSize.megabytes(5L))).isEqualTo(Size.bytes(5L * 1000L * 1000L));
        assertThat(Size.fromDataSize(DataSize.gibibytes(5L))).isEqualTo(Size.gigabytes(5L));
        assertThat(Size.fromDataSize(DataSize.gigabytes(5L))).isEqualTo(Size.bytes(5L * 1000L * 1000L * 1000L));
        assertThat(Size.fromDataSize(DataSize.tebibytes(5L))).isEqualTo(Size.terabytes(5L));
        assertThat(Size.fromDataSize(DataSize.terabytes(5L))).isEqualTo(Size.bytes(5L * 1000L * 1000L * 1000L * 1000L));
        assertThat(Size.fromDataSize(DataSize.pebibytes(5L))).isEqualTo(Size.terabytes(5L * 1024L * 1024L));
        assertThat(Size.fromDataSize(DataSize.petabytes(5L))).isEqualTo(Size.bytes(5L * 1000L * 1000L * 1000L * 1000L * 1000L));
    }

    @Test
    void testToDataSize() {
        assertThat(Size.bytes(5L).toDataSize()).isEqualTo(DataSize.bytes(5L));
        assertThat(Size.kilobytes(5L).toDataSize()).isEqualTo(DataSize.kibibytes(5L));
        assertThat(Size.megabytes(5L).toDataSize()).isEqualTo(DataSize.mebibytes(5L));
        assertThat(Size.gigabytes(5L).toDataSize()).isEqualTo(DataSize.gibibytes(5L));
        assertThat(Size.terabytes(5L).toDataSize()).isEqualTo(DataSize.tebibytes(5L));
    }

    @Test
    void testSerialization() throws IOException, ClassNotFoundException {
        final Size size = Size.megabytes(42L);
        final byte[] bytes;
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(size);
            bytes = outputStream.toByteArray();
        }

        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             final ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            @SuppressWarnings("BanSerializableRead")
            final Object o = objectInputStream.readObject();
            assertThat(o)
                    .isInstanceOf(Size.class)
                    .isEqualTo(size);
        }
    }
}
