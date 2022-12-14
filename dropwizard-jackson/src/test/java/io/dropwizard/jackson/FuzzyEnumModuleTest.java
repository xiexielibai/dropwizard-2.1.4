package io.dropwizard.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ClientInfoStatus;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class FuzzyEnumModuleTest {
    private final ObjectMapper mapper = new ObjectMapper();

    private enum EnumWithLowercase {
        lower_case_enum,
        mixedCaseEnum
    }

    private enum EnumWithCreator {
        TEST;

        @JsonCreator
        public static EnumWithCreator fromString(String value) {
            return EnumWithCreator.TEST;
        }
    }

    private enum CurrencyCode {
        USD("United States dollar"),
        AUD("a_u_d"),
        CAD("c-a-d"),
        BLA("b.l.a"),
        EUR("Euro"),
        GBP("Pound sterling");

        private final String description;

        CurrencyCode(String name) {
            this.description = name;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    enum EnumWithPropertyAnno {
        @JsonProperty("a a")
        A,

        // For this value, force use of anonymous sub-class, to ensure things still work
        @JsonProperty("b b")
        B {
            @Override
            public String toString() {
                return "bb";
            }
        },

        @JsonProperty("forgot password")
        FORGOT_PASSWORD,

        @JsonEnumDefaultValue
        DEFAULT
    }

    @BeforeEach
    void setUp() throws Exception {
        mapper.registerModule(new FuzzyEnumModule());
    }

    @Test
    void mapsUpperCaseEnums() throws Exception {
        assertThat(mapper.readValue("\"SECONDS\"", TimeUnit.class))
                .isEqualTo(TimeUnit.SECONDS);
    }

    @Test
    void mapsLowerCaseEnums() throws Exception {
        assertThat(mapper.readValue("\"milliseconds\"", TimeUnit.class))
                .isEqualTo(TimeUnit.MILLISECONDS);
    }

    @Test
    void mapsPaddedEnums() throws Exception {
        assertThat(mapper.readValue("\"   MINUTES \"", TimeUnit.class))
                .isEqualTo(TimeUnit.MINUTES);
    }

    @Test
    void mapsSpacedEnums() throws Exception {
        assertThat(mapper.readValue("\"   MILLI SECONDS \"", TimeUnit.class))
                .isEqualTo(TimeUnit.MILLISECONDS);
    }

    @Test
    void mapsDashedEnums() throws Exception {
        assertThat(mapper.readValue("\"REASON-UNKNOWN\"", ClientInfoStatus.class))
                .isEqualTo(ClientInfoStatus.REASON_UNKNOWN);
    }

    @Test
    void mapsDottedEnums() throws Exception {
        assertThat(mapper.readValue("\"REASON.UNKNOWN\"", ClientInfoStatus.class))
                .isEqualTo(ClientInfoStatus.REASON_UNKNOWN);
    }

    @Test
    void mapsWhenEnumHasCreator() throws Exception {
        assertThat(mapper.readValue("\"BLA\"", EnumWithCreator.class))
                .isEqualTo(EnumWithCreator.TEST);
    }

    @Test
    void failsOnIncorrectValue() {
        assertThatExceptionOfType(JsonMappingException.class)
            .isThrownBy(() -> mapper.readValue("\"wrong\"", TimeUnit.class))
            .satisfies(e -> assertThat(e.getOriginalMessage())
                .isEqualTo("Cannot deserialize value of type `java.util.concurrent.TimeUnit` from String \"wrong\": " +
                    "wrong was not one of [NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS]"));
    }

    @Test
    void mapsToLowerCaseEnums() throws Exception {
        assertThat(mapper.readValue("\"lower_case_enum\"", EnumWithLowercase.class))
                .isEqualTo(EnumWithLowercase.lower_case_enum);
    }

    @Test
    void mapsMixedCaseEnums() throws Exception {
        assertThat(mapper.readValue("\"mixedCaseEnum\"", EnumWithLowercase.class))
                .isEqualTo(EnumWithLowercase.mixedCaseEnum);
    }

    @Test
    void readsEnumsUsingToString() throws Exception {
        final ObjectMapper toStringEnumsMapper = mapper.copy()
                .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        assertThat(toStringEnumsMapper.readValue("\"Pound sterling\"", CurrencyCode.class)).isEqualTo(CurrencyCode.GBP);
    }

    @Test
    void readsUnknownEnumValuesAsNull() throws Exception {
        final ObjectMapper toStringEnumsMapper = mapper.copy()
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        assertThat(toStringEnumsMapper.readValue("\"Pound sterling\"", CurrencyCode.class)).isNull();
    }

    @Test
    void readsUnknownEnumValuesUsingDefaultValue() throws Exception {
        final ObjectMapper toStringEnumsMapper = mapper.copy()
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
        assertThat(toStringEnumsMapper.readValue("\"Pound sterling\"", EnumWithPropertyAnno.class)).isEqualTo(EnumWithPropertyAnno.DEFAULT);
    }

    @Test
    void readsEnumsUsingToStringWithDeserializationFeatureOff() throws Exception {
        assertThat(mapper.readValue("\"Pound sterling\"", CurrencyCode.class)).isEqualTo(CurrencyCode.GBP);
        assertThat(mapper.readValue("\"a_u_d\"", CurrencyCode.class)).isEqualTo(CurrencyCode.AUD);
        assertThat(mapper.readValue("\"c-a-d\"", CurrencyCode.class)).isEqualTo(CurrencyCode.CAD);
        assertThat(mapper.readValue("\"b.l.a\"", CurrencyCode.class)).isEqualTo(CurrencyCode.BLA);
    }

    @Test
    void testEnumWithJsonPropertyRename() throws Exception {
        final String json = mapper.writeValueAsString(new EnumWithPropertyAnno[] {
            EnumWithPropertyAnno.B, EnumWithPropertyAnno.A, EnumWithPropertyAnno.FORGOT_PASSWORD
        });
        assertThat(json).isEqualTo("[\"b b\",\"a a\",\"forgot password\"]");

        final EnumWithPropertyAnno[] result = mapper.readValue(json, EnumWithPropertyAnno[].class);

        assertThat(result).isNotNull().hasSize(3);
        assertThat(result[0]).isEqualTo(EnumWithPropertyAnno.B);
        assertThat(result[1]).isEqualTo(EnumWithPropertyAnno.A);
        assertThat(result[2]).isEqualTo(EnumWithPropertyAnno.FORGOT_PASSWORD);
    }
}
