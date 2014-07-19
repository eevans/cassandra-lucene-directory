package org.opennms.newts.search;


class CassandraConstants {

    // Tables
    static String T_FILES = "files";
    static String T_FILES_INDEX = "files_idx";

    // Attributes (T_FILES)
    static String C_FILES_INDEX = "index_name";
    static String C_FILES_ID = "id";
    static String C_FILES_SEGMENT = "segment";
    static String C_FILES_DATA = "data";
    
    // Attributes (T_FILES_INDEX)
    static String C_FILES_INDEX_INDEX = "index_name";
    static String C_FILES_INDEX_NAME = "name";
    static String C_FILES_INDEX_ID = "id";

}
