
CREATE KEYSPACE lucene
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE lucene.files (
    index_name text,
    id uuid,
    segment int,
    data blob,
    PRIMARY KEY((index_name,id), segment)
);

CREATE TABLE lucene.files_idx (
    index_name text,
    name text,
    id uuid,
    PRIMARY KEY(index_name, name)
);

CREATE TABLE lucene.locks (
    name text PRIMARY KEY,
    locked boolean
);