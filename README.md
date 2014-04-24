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

    $ lein run ${target} owner1 owner2 ...

Before running this, JRE and Clojure (Leiningen) should be installed.

## Todo

 * Separate configuration
 * Support MySQL, PostgreSQL
