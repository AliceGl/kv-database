# Basics of programming course: key-value database

### Usage
Utility to access database is implemented in file main.kt. Program takes 
arguments from command line and prints the result to the standard output stream.

The program is given a command and its parameters separated by spaces.

The operation logs are stored in the database files, to remove outdated
information the command "mergedb" can be called.

### Commands to access database

* newdb \[database name] - create new database with given name
* deletedb \[database name] - delete existing database
* cleardb \[database name] - delete all keys from the database
* insert \[database name] \[key] \[value] - add the key-value pair to the database
* remove \[database name] \[key] - remove key from the database
* get \[database name] \[key] - get value by the key from the database
* mergedb \[database name] - shrink operation log by deleting outdated 
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