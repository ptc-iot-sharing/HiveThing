/*
 * Copyright (c) 2019.  PTC Inc. and/or Its Subsidiary Companies. All Rights Reserved.
 * Copyright for PTC software products is with PTC Inc. and its subsidiary companies (collectively “PTC”), and their respective licensors. This software is provided under written license agreement, contains valuable trade secrets and proprietary information, and is protected by the copyright laws of the United States and other countries. It may not be copied or distributed in any form or medium, disclosed to third parties, or used in any manner not provided for in the software license agreement except with written prior approval from PTC.
 */

package com.thingworx.things.hive;

import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.metadata.annotations.*;
import com.thingworx.things.Thing;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.StringPrimitive;

import java.sql.*;
import java.util.HashMap;
import java.util.Vector;

import static com.thingworx.things.database.SQLToInfoTableConversion.createInfoTableFromResultset;

/**
 * An implementation of the Apache Hive-JDBC library for Thingworx
 */

@ThingworxConfigurationTableDefinitions(
        tables = {@ThingworxConfigurationTableDefinition(
                name = "ConnectionInfo",
                description = "Connection Settings",
                isMultiRow = false,
                dataShape = @ThingworxDataShapeDefinition(
                        fields = {@ThingworxFieldDefinition(
                                name = "serverName",
                                description = "Hive Server name",
                                baseType = "STRING"
                        ), @ThingworxFieldDefinition(
                                name = "serverPort",
                                description = "Hive Server port",
                                baseType = "NUMBER",
                                aspects = {"defaultValue:80"}
                        ), @ThingworxFieldDefinition(
                                name = "userName",
                                description = "User name",
                                baseType = "STRING"
                        ), @ThingworxFieldDefinition(
                                name = "dbName",
                                description = "database name",
                                baseType = "STRING"
                        ), @ThingworxFieldDefinition(
                                name = "password",
                                description = "Password",
                                baseType = "PASSWORD"
                        ), @ThingworxFieldDefinition(
                                name = "nosassl",
                                description = "Toggle NOSASSL authentication",
                                baseType = "BOOLEAN",
                                aspects = {"defaultValue:true"}
                        ), @ThingworxFieldDefinition(
                                name = "timeout",
                                description = "Timeout (milliseconds) to execute a request",
                                baseType = "NUMBER",
                                aspects = {"defaultValue:60000"}

                        )}
                )
        )}
)


public class HiveThing extends Thing {
    public static final String HIVE_WEBAPP = "hive";
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    private String _serverName = "localhost";
    private int _serverPort = 10000;
    private Boolean _nosasl = true;
    private String _dbName = "default";
    private String _ds = "DatabaseTable";
    private String _username = "hive";
    private String _password = "password";
    private int _timeout = 60000;

    public HiveThing() {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            _logger.error("Couldn't find a registered Hive Driver");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            _logger.error("Couldn't find a registered Hive Driver");
            e.printStackTrace();
            System.exit(1);
        }

    }

    protected void initializeThing() {
        this._serverName = (String) this.getConfigurationSetting("ConnectionInfo", "serverName");
        this._serverPort = ((Number) this.getConfigurationSetting("ConnectionInfo", "serverPort")).intValue();
        this._dbName = (String) this.getConfigurationSetting("ConnectionInfo", "dbName");
        this._username = (String) this.getConfigurationSetting("ConnectionInfo", "userName");
        this._password = (String) this.getConfigurationSetting("ConnectionInfo", "password");
        this._timeout = ((Number) this.getConfigurationSetting("ConnectionInfo", "timeout")).intValue();
        this._nosasl = (Boolean) this.getConfigurationSetting("ConnectionInfo", "nosassl");
    }

    @ThingworxServiceDefinition(
            name = "getTables",
            description = "Return the tables in the db"
    )
    @ThingworxServiceResult(
            name = "result",
            description = "Result",
            baseType = "INFOTABLE"
    )

    public InfoTable getTables(@ThingworxServiceParameter(name = "maxItems", description = "Max items to return", baseType = "NUMBER") Double maxItems) throws Exception {
        if (maxItems == null) {
            _logger.info("No maximum item parameter specified using default value");
        }

        InfoTable it = InfoTableInstanceFactory.createInfoTableFromDataShape("DatabaseTable");
        Connection con = null;
        ResultSet res;
        double timeout = 60000.0D;

        if (this._timeout > 0) {
            timeout = (double) this._timeout;

            StringBuilder stringServer = new StringBuilder();
            if (_dbName.equals("") || _dbName == null) {
                stringServer.append(this.buildBaseURL("default"));
                _logger.info("No database specified, using default ");
            } else {
                stringServer.append(this.buildBaseURL(_dbName));
                _logger.info("Connecting to: " + stringServer);
            }

            try {
                if (this._nosasl == true) {
                    _logger.info("Connecting with no auth to: " + stringServer);
                    con = DriverManager.getConnection(stringServer.toString());
                } else if (this._nosasl == false) {
                    _logger.info("Connecting with username: " + this._username + " and password " + this._password);
                    con = DriverManager.getConnection(stringServer.toString(), this._username, this._password);
                }
            } catch (SQLException e) {
                _logger.error("Something went wrong creating the connection to Hive");
                e.printStackTrace();
            }

            long timeStart = 0;

            try {
                if (con == null) {
                    _logger.error("Could not establish a connection to Hive");
                }

                timeStart = System.currentTimeMillis();
                String[] tableTypes = {"TABLE", "VIEW"};

                res = con.getMetaData().getTables(null, null, null, tableTypes);
                while (res.next()) {
                    ValueCollection values = new ValueCollection();
                    values.put("name", new StringPrimitive(res.getString(3)));
                    values.put("type", new StringPrimitive(res.getString(4)));

                    it.addRow(values);
                }

            } catch (Exception e) {
                _logger.error("Could not complete the query");
                e.printStackTrace();
            }

            _logger.info("query took " + (System.currentTimeMillis() - timeStart) + " ms");

            try {
                con.close();
            } catch (SQLException e) {
                _logger.error("Error closing the connection");
                e.printStackTrace();
            }
        }

        return it;
    }

    @ThingworxServiceDefinition(
            name = "getColumns",
            description = "Return the columns in a table"
    )
    @ThingworxServiceResult(
            name = "result",
            description = "Result",
            baseType = "INFOTABLE"
    )

    public InfoTable getColumns(@ThingworxServiceParameter(name = "tableName", description = "table  name", baseType = "STRING") String tableName,
                                @ThingworxServiceParameter(name = "maxItems", description = "Max items to return", baseType = "NUMBER") Double maxItems) throws Exception {

        InfoTable it = InfoTableInstanceFactory.createInfoTableFromDataShape("DatabaseColumn");
        double timeout = 60000.0D;
        Connection con = null; // initialise an empty hive-jdbc connection

        if (this._timeout > 0) {
            timeout = (double) this._timeout;

            StringBuilder stringServer = new StringBuilder();
            if (_dbName.equals("") || _dbName == null) {
                stringServer.append(this.buildBaseURL("default"));
                _logger.info("No database specified, using default ");
            } else {
                stringServer.append(this.buildBaseURL(_dbName));
                _logger.info("Connecting to: " + stringServer);
            }

            try {
                if (this._nosasl == true) {
                    _logger.info("Connecting with no auth to: " + stringServer);
                    con = DriverManager.getConnection(stringServer.toString());
                } else if (this._nosasl == false) {
                    _logger.info("Connecting with username: " + this._username + " and password " + this._password);
                    con = DriverManager.getConnection(stringServer.toString(), this._username, this._password);
                }
            } catch (SQLException e) {
                _logger.error("Something went wrong creating the connection to Hive");
                e.printStackTrace();
            }

            long timeStart = 0;

            if ((tableName != null) && (tableName.length() > 0)) {
                try {
                    ResultSet rs = con.getMetaData().getColumns(null, null, tableName, null);
                    while (rs.next()) {
                        ValueCollection values = new ValueCollection();
                        values.put("name", new StringPrimitive(rs.getString(4)));
                        BaseTypes baseType = BaseTypes.JDBCTypeToBaseType(rs.getInt(5));
                        values.put("baseType", new StringPrimitive(baseType.name()));

                        it.addRow(values);
                    }
                } catch (SQLException e) {
                    _logger.error("Error Getting Columns : " + e.getMessage());
                } finally {
                    con = null;
                }
            }
        }
        return it;
    }

    @ThingworxServiceDefinition(
            name = "executeUpdate",
            description = "Write to a table already defined in Hive"
    )
    @ThingworxServiceResult(
            name = "result",
            description = "Result",
            baseType = "NUMBER"
    )


    public Integer executeUpdate(@ThingworxServiceParameter(name = "tableName", description = "Table name", baseType = "STRING") String tableName,
                                 @ThingworxServiceParameter(name = "statement", description = "Hive SQL statement", baseType = "STRING") String statement) throws Exception {

        int result = 1;
        Connection conn = null;

        StringBuilder stringServer = new StringBuilder();
        if (_dbName.equals("") || _dbName == null) {
            stringServer.append(this.buildBaseURL("default"));
            _logger.info("No database specified, using default ");
        } else {
            stringServer.append(this.buildBaseURL(_dbName));
            _logger.info("Connecting to: " + stringServer);
        }

        conn = DriverManager.getConnection(stringServer.toString());
        conn.setCatalog(tableName);

        PreparedStatement queryStatement = conn.prepareStatement(statement);

        result = queryStatement.executeUpdate();

        return result;

    }



    @ThingworxServiceDefinition(
            name = "getTable",
            description = "Return a table from the db"
    )
    @ThingworxServiceResult(
            name = "result",
            description = "Result",
            baseType = "INFOTABLE"
    )


    public InfoTable getTable(@ThingworxServiceParameter(name = "tableName", description = "Table name", baseType = "STRING") String tableName,
                              @ThingworxServiceParameter(name = "maxItems", description = "Max items to return", baseType = "NUMBER") Double maxItems) throws Exception {

        InfoTable result = null;
        Connection conn = null;

        StringBuilder stringServer = new StringBuilder();
        if (_dbName.equals("") || _dbName == null) {
            stringServer.append(this.buildBaseURL("default"));
            _logger.info("No database specified, using default ");
        } else {
            stringServer.append(this.buildBaseURL(_dbName));
            _logger.info("Connecting to: " + stringServer);
        }

        conn = DriverManager.getConnection(stringServer.toString());
        PreparedStatement queryStatement = conn.prepareStatement("SELECT * from " + tableName);
        ResultSet res = queryStatement.executeQuery();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        ResultSetMetaData rsmd = res.getMetaData();
        int columnCount = rsmd.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(rsmd.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (res.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(res.getObject(columnIndex));
            }
            data.add(vector);
        }

        conn.setCatalog(tableName);
        /*
        HashMap<String, String> columnsMap = new HashMap<String, String>();
        InfoTable mtdtable = new InfoTable();

        ResultSet mtd = conn.getMetaData().getColumns(tableName, null, tableName,"%");
        ValueCollection values = new ValueCollection();
        while (mtd.next()) {
            values.put("name", new StringPrimitive(mtd.getString(4)));
            BaseTypes baseType = BaseTypes.JDBCTypeToBaseType(mtd.getInt(5));
            values.put("baseType", new StringPrimitive(baseType.name()));
            mtdtable.addRow(values);

            columnsMap.put((mtd.getString(4)), mtd.getString(5));
    }

        result = createInfoTableFromResultset(res, columnsMap);
        */

        ResultSetMetaData metadata = res.getMetaData();
        columnCount = metadata.getColumnCount();
        HashMap<String, String> map = new HashMap<String, String>();
        //List<String> rowBuilder = new LinkedList<>();

        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {

            String actualColumnName = metadata.getColumnName(columnIndex);
            String columnType = metadata.getColumnLabel(columnIndex);
            //String stringCell = res.getString(columnIndex);
            //rowBuilder.add(stringCell);
            map.put(actualColumnName, columnType);

        }
        result = createInfoTableFromResultset(res, map);
        return result;

    }


    protected String buildBaseURL(String dbName) {
        StringBuffer sbURL = new StringBuffer();

        sbURL.append("jdbc:hive2://");
        sbURL.append(this._serverName);
        sbURL.append(':');
        sbURL.append(this._serverPort);
        sbURL.append("/");
        if (dbName != null && dbName.length() > 0) {
            sbURL.append(dbName);
            sbURL.append(";");
        }

        if (_nosasl == false) {
            _logger.error("Not Implemented");
        } else if (_nosasl == true) {
            sbURL.append("auth=noSasl");
        }

        _logger.error("connection string " + sbURL + "");
        return sbURL.toString();
    }

    protected static class ConfigConstants {
        public static final String ConnectionInfo = "ConnectionInfo";
        public static final String ServerName = "serverName";
        public static final String ServerPort = "serverPort";
        public static final String UserName = "userName";
        public static final String Password = "password";
        public static final String Timeout = "timeout";
        public static final Boolean Nosassl = true;
        public static final String Dbname = "dbName";

        protected ConfigConstants() {
        }
    }
}

