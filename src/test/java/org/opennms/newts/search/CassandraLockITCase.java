package org.opennms.newts.search;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.junit.Before;
import org.junit.Test;


public class CassandraLockITCase extends AbstractCassandraTestCase {

    private CassandraSession m_session;

    @Before
    public void setUp() {
        m_session = new CassandraSession(CASSANDRA_KEYSPACE, CASSANDRA_HOST, CASSANDRA_PORT, "CassandraLockITCase");
    }

    @Test
    public void test() throws IOException {
        try (Lock lock = new CassandraLock(m_session, "L")) {
            // Not locked
            assertThat(lock.isLocked(), is(false));

            // Locked
            assertThat(lock.obtain(), is(true));
            assertThat(lock.obtain(), is(false));

            // Is locked?
            assertThat(lock.isLocked(), is(true));

            lock.close();

            // Not locked
            assertThat(lock.isLocked(), is(false));
        }
    }

    @Test
    public void testFactory() throws IOException {
        LockFactory factory = new CassandraLockFactory(m_session);

        try (Lock lock = factory.makeLock("L")) {
            assertThat(lock.obtain(), is(true));
            assertThat(lock.isLocked(), is(true));

            factory.clearLock("L");

            // Not locked
            assertThat(lock.isLocked(), is(false));
        }
    }

}
