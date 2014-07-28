package org.opennms.newts.search;


import org.cassandraunit.AbstractCassandraUnit4CQLTestCase;
import org.cassandraunit.dataset.CQLDataSet;
import org.cassandraunit.dataset.cql.FileCQLDataSet;


public class AbstractCassandraTestCase extends AbstractCassandraUnit4CQLTestCase {

    protected static final String CASSANDRA_CONFIG = "cassandra.yaml";
    protected static final String CASSANDRA_HOST = "localhost";
    protected static final int CASSANDRA_PORT = 9043;
    protected static final String CASSANDRA_KEYSPACE = "lucene";

    private static final String SCHEMA_RESOURCE = "/schema.cql";

    public AbstractCassandraTestCase() {
        super(CASSANDRA_CONFIG, CASSANDRA_HOST, CASSANDRA_PORT);
    }

    @Override
    public CQLDataSet getDataSet() {
        return new FileCQLDataSet(getClass().getResource(SCHEMA_RESOURCE).getFile(), false, true, CASSANDRA_KEYSPACE);
    }

}
