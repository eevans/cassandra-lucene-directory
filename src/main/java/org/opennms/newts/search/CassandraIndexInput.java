package org.opennms.newts.search;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.lucene.store.BufferedIndexInput;

public class CassandraIndexInput extends BufferedIndexInput {

    private final CassandraFile m_file;
    private long m_pointer = 0;

    protected CassandraIndexInput(CassandraFile file) {
        super(file.getResourceDescription());
        m_file = checkNotNull(file, "file argument");
    }

    @Override
    protected void readInternal(byte[] b, int offset, int length) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void seekInternal(long pos) throws IOException {
        m_pointer = pos;
    }

    @Override
    public void close() throws IOException {
        
    }

    @Override
    public long length() {
        return m_file.getLength();
    }

}
