package org.opennms.newts.search;


import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class Query implements Closeable {

    private static final int DEFAULT_NUM_HITS = 10;

    private final IndexReader m_indexReader;
    private final IndexSearcher m_indexSearcher;
    private final int m_numHits;

    public Query(File indexDir) throws IOException {
        this(indexDir, DEFAULT_NUM_HITS);
    }

    public Query(File indexDir, int numHits) throws IOException {
        m_indexReader = DirectoryReader.open(FSDirectory.open(indexDir));
        m_indexSearcher = new IndexSearcher(m_indexReader);
        m_numHits = numHits;
    }

    public ScoreDoc[] search(String queryString) throws IOException, ParseException {
        org.apache.lucene.search.Query query =
                new QueryParser(
                        Version.LUCENE_4_9,
                        "_all",
                        new StandardAnalyzer(Version.LUCENE_4_9)).parse(queryString);
        TopScoreDocCollector collector = getCollector(m_numHits);
        m_indexSearcher.search(query, collector);

        return collector.topDocs().scoreDocs;
    }

    public Document getDoc(int id) throws IOException {
        return m_indexSearcher.doc(id);
    }

    public void close() throws IOException {
        m_indexReader.close();
    }

    private TopScoreDocCollector getCollector(int numHits) {
        return TopScoreDocCollector.create(numHits, true);
    }

    public static void main(String... args) throws IOException, ParseException {

        File indexDir = new File("/tmp/newts");
        indexDir.mkdirs();
        Query query = new Query(indexDir);

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String input;

            System.out.print("Query: ");

            while ((input = br.readLine()) != null) {
                ScoreDoc[] hits = query.search(input);

                System.out.printf("Found %d hits.%n", hits.length);

                for (int i = 0; i < hits.length; i++) {
                    Document doc = query.getDoc(hits[i].doc);
                    System.out.printf("[%2d] %s (%s)%n", i, doc.get("resource"), doc);
                }

                System.out.print("Query: ");
            }
        }
        finally {
            query.close();
        }
    }

}
