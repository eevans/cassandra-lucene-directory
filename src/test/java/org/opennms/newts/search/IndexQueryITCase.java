package org.opennms.newts.search;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.junit.Test;


public class IndexQueryITCase extends AbstractCassandraTestCase {

    private static final Version VERSION = Version.LUCENE_4_9;

    private void index() throws IOException {

        IndexWriterConfig config = new IndexWriterConfig(VERSION, new StandardAnalyzer(VERSION));
        Directory directory = new CassandraDirectory(CASSANDRA_KEYSPACE, CASSANDRA_HOST, CASSANDRA_PORT, "IndexQueryITCase");
        IndexWriter indexWriter = new IndexWriter(directory, config);

        Document doc;

        doc = new Document();
        doc.add(new StringField("resource",  "/dfw/rack0/10.0.0.1", Field.Store.YES));
        doc.add(new StringField("region",               "americas", Field.Store.NO));
        doc.add(new StringField("sysObjId",       ".1.1.3.5.156,0", Field.Store.NO));
        doc.add(new StringField("_all",      "/dfw/rack0/10.0.0.1", Field.Store.NO));
        doc.add(new StringField("_all",                 "americas", Field.Store.NO));
        doc.add(new StringField("_all",           ".1.1.3.5.156,0", Field.Store.NO));
        indexWriter.addDocument(doc);

        doc = new Document();
        doc.add(new StringField("resource",  "/dfw/rack0/10.0.0.2", Field.Store.YES));
        doc.add(new StringField("region",               "americas", Field.Store.NO));
        doc.add(new StringField("sysObjId",       ".1.1.3.5.156,0", Field.Store.NO));
        doc.add(new StringField("_all",      "/dfw/rack0/10.0.0.2", Field.Store.NO));
        doc.add(new StringField("_all",                 "americas", Field.Store.NO));
        doc.add(new StringField("_all",           ".1.1.3.5.156,0", Field.Store.NO));
        indexWriter.addDocument(doc);

        doc = new Document();
        doc.add(new StringField("resource",  "/dfw/rack1/10.0.1.1", Field.Store.YES));
        doc.add(new StringField("region",               "americas", Field.Store.NO));
        doc.add(new StringField("sysObjId",       ".1.1.3.5.156,0", Field.Store.NO));
        doc.add(new StringField("_all",      "/dfw/rack1/10.0.1.1", Field.Store.NO));
        doc.add(new StringField("_all",                 "americas", Field.Store.NO));
        doc.add(new StringField("_all",           ".1.1.3.5.156,0", Field.Store.NO));
        indexWriter.addDocument(doc);

        indexWriter.close(true);
        directory.close();

    }

    private Document[] query(String queryString) throws IOException, ParseException {

        Directory directory = new CassandraDirectory(CASSANDRA_KEYSPACE, CASSANDRA_HOST, CASSANDRA_PORT, "IndexQueryITCase");
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        org.apache.lucene.search.Query query = new QueryParser(VERSION, "_all", new StandardAnalyzer(VERSION)).parse(queryString);
        TopScoreDocCollector collector = TopScoreDocCollector.create(10, true);

        searcher.search(query, collector);

        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        Document[] docs = new Document[hits.length];

        for (int i = 0; i < hits.length; i++) {
            docs[i] = searcher.doc(hits[i].doc);
        }

        return docs;
    }

    @Test
    public void test() throws IOException, ParseException {

        index();

        Document[] docs = query("americas");

        assertThat(docs.length, is(equalTo(3)));
        assertThat(docs[0].get("resource"), equalTo("/dfw/rack0/10.0.0.1"));
        assertThat(docs[1].get("resource"), equalTo("/dfw/rack0/10.0.0.2"));
        assertThat(docs[2].get("resource"), equalTo("/dfw/rack1/10.0.1.1"));

        docs = query("\\/dfw\\/rack1\\/*");

        assertThat(docs.length, is(equalTo(1)));
        assertThat(docs[0].get("resource"), equalTo("/dfw/rack1/10.0.1.1"));

    }

}
