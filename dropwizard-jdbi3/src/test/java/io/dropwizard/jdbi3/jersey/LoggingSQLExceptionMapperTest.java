package io.dropwizard.jdbi3.jersey;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LoggingSQLExceptionMapperTest {
    @Test
    void testLogException() throws Exception {
        Logger logger = mock(Logger.class);
        LoggingSQLExceptionMapper sqlExceptionMapper = new LoggingSQLExceptionMapper(logger);

        RuntimeException runtimeException = new RuntimeException("DB is down");
        SQLException sqlException = new SQLException("DB error", runtimeException);
        sqlExceptionMapper.logException(4981, sqlException);

        verify(logger).error("Error handling a request: 0000000000001375", sqlException);
        verify(logger).error("Error handling a request: 0000000000001375", runtimeException);
    }
}
