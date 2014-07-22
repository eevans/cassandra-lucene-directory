package org.opennms.newts.search;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexOutput;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Sets;


public class CassandraDirectoryTest {

    @BeforeClass
    public static void setUpClass() {
        CassandraFile.SEGMENT_SIZE = 256;
    }

    @Test
    public void testDeleteFile() throws IOException {
        try (CassandraDirectory d = new CassandraDirectory("delete-file-test.index")) {
            try (IndexOutput o = d.createOutput("afile", new IOContext())) {
                o.writeByte((byte) 0);
            }

            assertThat(Arrays.asList(d.listAll()).contains("afile"), is(true));

            d.deleteFile("afile");

            assertThat(Arrays.asList(d.listAll()).contains("afile"), is(false));

        }
    }

    @Test
    public void testListFiles() throws IOException {

        String[] names = new String[] { "once", "twice", "three", "times", "lady" };

        try (CassandraDirectory d = new CassandraDirectory("list-files-test.index")) {
            for (String name : names) {
                try (IndexOutput o = d.createOutput(name, new IOContext())) {
                    byte[] bites = name.getBytes();
                    o.writeBytes(bites, bites.length);
                }
            }

            Set<String> in = Sets.newHashSet(names);
            Set<String> out = Sets.newHashSet(d.listAll());

            assertThat(in, is(equalTo(out)));

        }

    }

    @Test
    public void testCreateOutput() throws Exception {

        CassandraDirectory dir = new CassandraDirectory();

        try (IndexOutput output = dir.createOutput("createOutputTest", new IOContext())) {

            byte[] bytes = new String("a").getBytes();

            for (int i = 0; i < 513; i++) {
                output.writeBytes(bytes, bytes.length);
            }

            System.out.println("Wrote " + 513 * bytes.length + " bytes");

        }
        finally {
            dir.close();
        }

    }

}
