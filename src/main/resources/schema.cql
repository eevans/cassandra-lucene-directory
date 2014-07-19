
CREATE TABLE files (
    index_name text,
    id uuid,
    segment int,
    data blob,
    PRIMARY KEY((index_name,id), segment)
);                                                                                
 
CREATE TABLE files_idx (
    index_name text,
    name text,
    id uuid,
    PRIMARY KEY(index_name, name)
);                                                                                
 
CREATE TABLE locks (
    name text PRIMARY KEY,
    locked boolean
);