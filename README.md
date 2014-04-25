TableDoc
========

Generates documentation about tables, columns, indexes, etc. of Specified database in JavaDoc style.

## Setup
Clone this project and edit `config.clj`.

    $ git clone https://github.com/ntalbs/tabledoc.git
    $ cd tabledoc
    $ vim config.clj
    ...

## Usage

    $ lein run -d ${target} -s "owner1,owner2,..."

Before running this, JRE and Clojure (Leiningen) should be installed.

### Oracle memo
If you want to connect to Oracle DBMS, you should prepare the Oracle JDBC Driver. [Download it from Oracle](http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html) and install the driver file to local Maven repository.

    $ mvn install:install-file -X -DgroupId=local -DartifactId=ojdbc6 -Dversion=11.2.0.3 -Dpackaging=jar -Dfile=/opt/oracle/instantclient/ojdbc6.jar -DgeneratePom=true

And when you specify the owners, they should be upper case.

## Todo

 * Separate configuration
 * Support MySQL, PostgreSQL
