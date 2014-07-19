package org.opennms.newts.search;

import java.io.IOException;

import org.apache.lucene.store.BufferedIndexInput;

public class CassandraIndexInput extends BufferedIndexInput {

    protected CassandraIndexInput(CassandraFile file) {
        super(file.getResourceDescription());
    }

    @Override
    protected void readInternal(byte[] b, int offset, int length) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void seekInternal(long pos) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public long length() {
        // TODO Auto-generated method stub
        return 0;
    }

}
