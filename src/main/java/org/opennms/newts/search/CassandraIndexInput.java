package org.opennms.newts.search;


import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.nio.ByteBuffer;

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

        ByteBuffer buf = getCurrentSegment();

        if (length <= buf.remaining()) {
            seekInternal(getFilePointer() + length);
            buf.get(b, offset, length);
        }
        else { // length > remaining
            int remaining = buf.remaining();
            seekInternal(getFilePointer() + remaining);
            buf.get(b, offset, remaining);
            readInternal(b, offset + remaining, length-remaining);
        }

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

    private ByteBuffer getCurrentSegment() throws IOException {
        return m_file.getSegment(getCurrentSegementNumber());
    }

    private long getCurrentSegementNumber() {
        return (getPointer() / CassandraFile.SEGMENT_SIZE) + 1;
    }

    private long getPointer() {
        return m_pointer;
    }

}
