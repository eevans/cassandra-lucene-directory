package org.opennms.newts.search;


import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class Index {

    private static final Version VERSION = Version.LUCENE_4_9;

    private final IndexWriter m_indexWriter;

    public Index(String indexDir) throws IOException {
        this(new File(indexDir));
    }

    public Index(File indexDir) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(VERSION, new StandardAnalyzer(VERSION));
        m_indexWriter = new IndexWriter(FSDirectory.open(indexDir), config);
    }

    public void addDocument(Document doc) throws IOException {
        m_indexWriter.addDocument(doc);
    }

    public void shutdown() throws IOException {
        m_indexWriter.close(true);;
    }

    public static void main(String... args) throws IOException {

        File indexDir = new File("/tmp/newts");
        indexDir.mkdirs();

        Index index = new Index(indexDir);
        Document doc;

        try {
            doc = new Document();
            doc.add(new StringField("resource",   "/dfw/rack0/10.0.0.1",  Field.Store.YES));
            doc.add(new StringField("region",                "americas",  Field.Store.NO));
            doc.add(new StringField("sysObjId",        ".1.1.3.5.156,0",  Field.Store.NO));
            doc.add(new StringField("_all",       "/dfw/rack0/10.0.0.1",  Field.Store.NO));
            doc.add(new StringField("_all",                  "americas",  Field.Store.NO));
            doc.add(new StringField("_all",            ".1.1.3.5.156,0",  Field.Store.NO));
            index.addDocument(doc);

            doc = new Document();
            doc.add(new StringField("resource",   "/dfw/rack0/10.0.0.2",  Field.Store.YES));
            doc.add(new StringField("region",                "americas",  Field.Store.NO));
            doc.add(new StringField("sysObjId",        ".1.1.3.5.156,0",  Field.Store.NO));
            doc.add(new StringField("_all",       "/dfw/rack0/10.0.0.2",  Field.Store.NO));
            doc.add(new StringField("_all",                  "americas",  Field.Store.NO));
            doc.add(new StringField("_all",            ".1.1.3.5.156,0",  Field.Store.NO));
            index.addDocument(doc);

            doc = new Document();
            doc.add(new StringField("resource",   "/dfw/rack1/10.0.1.1",  Field.Store.YES));
            doc.add(new StringField("region",                "americas",  Field.Store.NO));
            doc.add(new StringField("sysObjId",        ".1.1.3.5.156,0",  Field.Store.NO));
            doc.add(new StringField("_all",       "/dfw/rack1/10.0.1.1",  Field.Store.NO));
            doc.add(new StringField("_all",                  "americas",  Field.Store.NO));
            doc.add(new StringField("_all",            ".1.1.3.5.156,0",  Field.Store.NO));
            index.addDocument(doc);
        }
        finally {
            index.shutdown();
        }

    }

}
