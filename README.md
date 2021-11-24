# Basics of programming course: key-value database

### Usage
Utility to access database is implemented in file main.kt. Program accepts
arguments from command line and prints result to standard output stream.

The program is given a command and its parameters separated by spaces.

The operation logs are stored in the database files and to remove outdated
information you can call the command "mergedb".

### Commands to access database

* newdb \[database name] - create new database with given name
* deletedb \[database name] - delete existing database
* cleardb \[database name] - delete all keys from database
* insert \[database name] \[key] \[value] - add pair key-value to database
* remove \[database name] \[key] - remove key from database
* get \[database name] \[key] - get value by key from database
* mergedb \[database name] - reduce operation log by deleting outdated
information about added and removed keys

### Examples

> newdb Database1

database Database1 created

> insert Database1 key1 value1

> insert Database1 key2 value2

> remove Database1 key1

> insert Database1 key2 value3

> get Database1 key2

Output: **value3**

Logs in database files:

<pre>
+ key1 value1
+ key2 value2
- key1
+ key2 value3
</pre>

> mergedb Database1

Logs have been reduced to:
<pre>
+ key2 value3
</pre>

> cleardb Database1

All the logs are removed

> deletedb Database1

database Database1 deleted