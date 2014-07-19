package org.opennms.newts.search;


import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexOutput;
import org.junit.BeforeClass;
import org.junit.Test;


public class CassandraDirectoryTest {

    @BeforeClass
    public static void setUpClass() {
        CassandraFile.SEGMENT_SIZE = 256;
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
