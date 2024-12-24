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

  # Disclaimer
By downloading this software, the user acknowledges that it is unsupported, not reviewed for security purposes, and that the user assumes all risk for running it.

Users accept all risk whatsoever regarding the security of the code they download.

This software is not an official PTC product and is not officially supported by PTC.

PTC is not responsible for any maintenance for this software.

PTC will not accept technical support cases logged related to this Software.

This source code is offered freely and AS IS without any warranty.

The author of this code cannot be held accountable for the well-functioning of it.

The author shared the code that worked at a specific moment in time using specific versions of PTC products at that time, without the intention to make the code compliant with past, current or future versions of those PTC products.

The author has not committed to maintain this code and he may not be bound to maintain or fix it.


# License
I accept the MIT License (https://opensource.org/licenses/MIT) and agree that any software downloaded/utilized will be in compliance with that Agreement. However, despite anything to the contrary in the License Agreement, I agree as follows:

I acknowledge that I am not entitled to support assistance with respect to the software, and PTC will have no obligation to maintain the software or provide bug fixes or security patches or new releases.

The software is provided “As Is” and with no warranty, indemnitees or guarantees whatsoever, and PTC will have no liability whatsoever with respect to the software, including with respect to any intellectual property infringement claims or security incidents or data loss.
