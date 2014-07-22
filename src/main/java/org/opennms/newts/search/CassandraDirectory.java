package org.opennms.newts.search;


import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;


public class CassandraDirectory extends Directory {

    private CassandraSession m_session;

    CassandraDirectory() {
        this("newts", "localhost", 9042, "index");
    }

    CassandraDirectory(String indexName) {
        this("newts", "localhost", 9042, indexName);
    }

    public CassandraDirectory(String keyspace, String hostname, int port, String indexName) {
        m_session = new CassandraSession(keyspace, hostname, port, indexName);
    }

    @Override
    public String[] listAll() throws IOException {
        return CassandraFile.listAll(m_session);
    }

    @Override
    public boolean fileExists(String name) throws IOException {
        return CassandraFile.exists(m_session, name);
    }

    @Override
    public void deleteFile(String name) throws IOException {
        CassandraFile.deleteFile(m_session, name);
    }

    @Override
    public long fileLength(String name) throws IOException {
        return new CassandraFile(m_session, name, CassandraFile.Mode.READ).getLength();
    }

    @Override
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        return new CassandraIndexOutput(new CassandraFile(m_session, name, CassandraFile.Mode.CREATE));
    }

    @Override
    public void sync(Collection<String> names) throws IOException {
        // Not applicable.
    }

    @Override
    public IndexInput openInput(String name, IOContext context) throws IOException {
        return new CassandraIndexInput(new CassandraFile(m_session, name, CassandraFile.Mode.READ));
    }

    @Override
    public Lock makeLock(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void clearLock(String name) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLockFactory(LockFactory lockFactory) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public LockFactory getLockFactory() {
        // TODO Auto-generated method stub
        return null;
    }

}
