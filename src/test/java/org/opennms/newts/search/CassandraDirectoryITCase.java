package org.opennms.newts.search;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.zip.CRC32;

import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Sets;


public class CassandraDirectoryITCase extends AbstractCassandraTestCase {

    private CassandraDirectory m_directory;
    private IOContext m_context = new IOContext();
    private Random m_random = new Random();

    @BeforeClass
    public static void setUpClass() {
        CassandraFile.SEGMENT_SIZE = 5;
    }

    @Before
    public void setUp() throws Exception {
        super.before();
        m_directory = new CassandraDirectory(CASSANDRA_KEYSPACE, CASSANDRA_HOST, CASSANDRA_PORT, "CassandraDirectoryITCase");
    }

    @After
    public void tearDown() throws IOException {
        super.after();
        m_directory.close();
    }

    @Test
    public void testExists() throws IOException {
        try (IndexOutput o = m_directory.createOutput("afile", new IOContext())) {
            o.writeByte((byte) 0);
        }

        assertThat(m_directory.fileExists("afile"), is(true));

    }

    @Test
    public void testLength() throws IOException {
        try (IndexOutput o = m_directory.createOutput("afile", new IOContext())) {
            o.writeByte((byte) 0);
        }

        assertThat(m_directory.fileLength("afile"), equalTo(1L));

    }

    @Test
    public void testDeleteFile() throws IOException {
        try (IndexOutput o = m_directory.createOutput("afile", new IOContext())) {
            o.writeByte((byte) 0);
        }

        assertThat(Arrays.asList(m_directory.listAll()).contains("afile"), is(true));

        m_directory.deleteFile("afile");

        assertThat(Arrays.asList(m_directory.listAll()).contains("afile"), is(false));

    }

    @Test
    public void testListFiles() throws IOException {

        String[] names = new String[] { "once", "twice", "three", "times", "lady" };

        for (String name : names) {
            try (IndexOutput o = m_directory.createOutput(name, new IOContext())) {
                byte[] bites = name.getBytes();
                o.writeBytes(bites, bites.length);
            }
        }

        Set<String> in = Sets.newHashSet(names);
        Set<String> out = Sets.newHashSet(m_directory.listAll());

        assertThat(in, is(equalTo(out)));

    }

    @Test
    public void testCreateOutput() throws Exception {

        // 103 * 5 = 515 bytes (2 256 byte segments + 1 w/ remaining 3 bytes)
        int numWrites = 103;
        int bytesPerWrite = 5;

        try (IndexOutput output = m_directory.createOutput("testCreateOutput", m_context)) {

            CRC32 crcIn = new CRC32();
            byte[] bytes = new byte[bytesPerWrite];

            for (int i = 0; i < numWrites; i++) {
                randomBytes(bytes);
                crcIn.update(bytes);
                output.writeBytes(bytes, bytes.length);
            }

            // Independently verify the calculated checksum
            assertThat(output.getChecksum(), is(equalTo(crcIn.getValue())));

        }

        // After the close...

        // File should appear in a listing
        assertThat(Arrays.asList(m_directory.listAll()).contains("testCreateOutput"), is(true));

        // Reported size should be numWrites * bytesPerWrite in size
        assertThat(m_directory.fileLength("testCreateOutput"), is(equalTo((long) (numWrites * bytesPerWrite))));

    }

    @Test
    public void testCreateInput() throws IOException {

        long checksum;
        int numWrites = 2;
        int bytesPerWrite = 5;
        int size = numWrites * bytesPerWrite;

        // Write a file
        try (IndexOutput out = m_directory.createOutput("testCreateInput", m_context)) {

            byte[] bytes = new byte[bytesPerWrite];

            for (int i = 0; i < numWrites; i++) {
                randomBytes(bytes);
                out.writeBytes(bytes, bytes.length);
            }

            checksum = out.getChecksum();

        }

        
        // File should appear in a listing
        assertThat(Arrays.asList(m_directory.listAll()).contains("testCreateInput"), is(true));

        // Read the file
        try (IndexInput in = m_directory.openInput("testCreateInput", m_context)) {

            CRC32 crcOut = new CRC32();

            // Length is sane
            assertThat(in.length(), is(equalTo((long) size)));

            // Read and calculate an independent CRC
            for (int i = 0; i < in.length(); i++) {
                byte[] bs = new byte[1];
                in.readBytes(bs, 0, 1);
                crcOut.update(bs[0]);
            }

            // Checksum matches
            assertThat(crcOut.getValue(), is(equalTo(checksum)));

        }

    }

    private void randomBytes(byte[] bytes) {
        m_random.nextBytes(bytes);
    }

}
