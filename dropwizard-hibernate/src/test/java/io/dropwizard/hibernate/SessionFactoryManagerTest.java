package io.dropwizard.hibernate;

import io.dropwizard.db.ManagedDataSource;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SessionFactoryManagerTest {
    private final SessionFactory factory = mock(SessionFactory.class);
    private final ManagedDataSource dataSource = mock(ManagedDataSource.class);
    private final SessionFactoryManager manager = new SessionFactoryManager(factory, dataSource);

    @Test
    void closesTheFactoryOnStopping() throws Exception {
        manager.stop();

        verify(factory).close();
    }

    @Test
    void stopsTheDataSourceOnStopping() throws Exception {
        manager.stop();

        verify(dataSource).stop();
    }

    @Test
    void startsTheDataSourceOnStarting() throws Exception {
        manager.start();

        verify(dataSource).start();
    }
}
