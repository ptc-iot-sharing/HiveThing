# HiveThing

**Disclaimer**

This repository is provided "AS-IS" with **no warranty or support** given. This is not an official or supported product/use case. 

Download the project build files from 

https://github.com/ptc-iot-sharing/HiveThing/archive/refs/tags/v1.4.74.zip

Download the extension package from

https://github.com/ptc-iot-sharing/HiveThing/releases/download/v1.4.74/HiveJDBC.zip



**Description**

The Apache Hive (TM) data warehouse software facilitates reading,
writing, and managing large datasets residing in distributed storage
using SQL. Built on top of Apache Hadoop (TM), it provides:

* Tools to enable easy access to data via SQL, thus enabling data
  warehousing tasks such as extract/transform/load (ETL), reporting,
  and data analysis

* A mechanism to impose structure on a variety of data formats

* Access to files stored either directly in Apache HDFS (TM) or in other
  data storage systems such as Apache HBase (TM)

* Query execution using Apache Hadoop MapReduce, Apache Tez
  or Apache Spark frameworks.

This extension allows the user to interact with a Hive server, and perform the tasks listed below. 

**Services**

`getColumns` - For a specified table returns all the columns, useful for creating a DataShape

`getTables`-  Lists all the available tables on Hive server, useful for debugging/verifying connectivity

`getTable`- Returns an entire table from the server (WIP)

`executeUpdate` - Calls the executeUpdate method that allows data insertion into an existing table. Used for storing user search analytics and saved queries.



**Configuration**

*password* - the password if it is configured

*serverName* - the IP address or hostname of the Hive server

*serverPort* - the port that is configured to receive queries on the Hive server

*userName* - the username that will be used together with the password to authenticate, can be blank

*timeout* - after this interval if a response is not received the connection will time out, in miliseconds

*nosassl* - a boolean that indicates that the authentication type is nosassl




General Info
============

For the latest information about Hive, please visit out website at:

  http://hive.apache.org/


Getting Started
===============

- Installation Instructions and a quick tutorial:
  https://cwiki.apache.org/confluence/display/Hive/GettingStarted

- A longer tutorial that covers more features of HiveQL:
  https://cwiki.apache.org/confluence/display/Hive/Tutorial

- The HiveQL Language Manual:
  https://cwiki.apache.org/confluence/display/Hive/LanguageManual


Requirements
============

- Java 1.7 or 1.8

- Hadoop 1.x, 2.x, 3.x (3.x required for Hive 3.x)
  repository. Send an empty email to commits-subscribe@hive.apache.org
  in order to subscribe to this mailing list.
