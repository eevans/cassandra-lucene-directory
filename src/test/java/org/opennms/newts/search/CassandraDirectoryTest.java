package org.opennms.newts.search;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexOutput;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Sets;


public class CassandraDirectoryTest extends AbstractCassandraTestCase {

    private CassandraDirectory m_directory;

    @BeforeClass
    public static void setUpClass() {
        CassandraFile.SEGMENT_SIZE = 256;
    }

    @Before
    public void setUp() throws Exception {
        super.before();
        m_directory = new CassandraDirectory(CASSANDRA_KEYSPACE, CASSANDRA_HOST, CASSANDRA_PORT, "CassandraDirectoryTest");
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

        try (IndexOutput output = m_directory.createOutput("createOutputTest", new IOContext())) {

            byte[] bytes = new String("a").getBytes();

            for (int i = 0; i < 513; i++) {
                output.writeBytes(bytes, bytes.length);
            }

            System.out.println("Wrote " + 513 * bytes.length + " bytes");

        }

    }

    @Test
    public void testCreateInput() throws IOException {

//        try (IndexInput i = m_directory.openInput("aFile", new IOContext())) {
//
//        }

    }

}
