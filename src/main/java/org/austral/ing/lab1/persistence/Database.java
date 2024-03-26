package org.austral.ing.lab1.persistence;

import org.hsqldb.persist.HsqlProperties;

import java.sql.Connection;

public class Database {

    final String dbLocation = "/Users/inegassiebayle/projects/lab1/hsqldb-2.7.2/hsqldb/db/";
    org.hsqldb.server.Server server;

    public void startDBServer() {
        HsqlProperties props = new HsqlProperties();
        props.setProperty("server.database.0", "file:" + dbLocation + "energymDB;");
        props.setProperty("server.dbname.0", "energymDB");
        server = new org.hsqldb.Server();
        try {
            server.setProperties(props);
        } catch (Exception e) {
            return;
        }
        server.start();
    }

    public void stopDBServer() {
        server.shutdown();
    }

}

