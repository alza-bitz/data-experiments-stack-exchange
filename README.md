# data-experiments-stack-exchange

Various data processing experiments with Clojure and Apache Spark, using archived Stack Exchange data.

The included source code is provided to support example #4 in the following Clojure presentation:

[Apache Spark with Clojure](https://goo.gl/koZ90v)

## Credits

Based largely on the following blog post, with modifications to use [Sparkling](https://github.com/gorillalabs/sparkling) instead of [Flambo](https://github.com/yieldbot/flambo):

[wtfleming.github.io/2015/07/07/exploring-stack-exchange-database-dumps-with-spark-and-flambo](wtfleming.github.io/2015/07/07/exploring-stack-exchange-database-dumps-with-spark-and-flambo)

## Dependencies

- Leiningen (for Clojure build and REPL)
- Docker

## Usage

These steps have only been tested on a host running Fedora 23 workstation, but in theory should work for all Linux variants.

### Clone Project

	mkdir ~/Projects

	cd ~/Projects

	git clone https://github.com/alzadude/data-experiments-stack-exchange.git

### Setup Data

1. Download archived Stack Exchange data e.g. Astronomy, from [https://archive.org/details/stackexchange](https://archive.org/details/stackexchange)
1. Extract files from downloaded archive
1. Copy `Users.xml` to `~/Projects/data-experiments-stack-exchange/data/raw/users`
1. Copy `Posts.xml` to `~/Projects/data-experiments-stack-exchange/data/raw/posts`

### Build Driver

	cd ~/Projects/data-experiments-stack-exchange/

	lein uberjar

### Setup Spark Environment

A Docker-based Spark on YARN environment, in this case..

	chcon -Rt svirt_sandbox_file_t ~/Projects/

	docker pull sequenceiq/spark:1.6.0

### Start Spark Environment

	docker run -d -p 8088:8088 -p 8042:8042 -h sandbox --name sandbox -v ~/Projects:/mnt sequenceiq/spark:1.6.0 -d

### Execute Driver on Spark

	docker exec -t -i sandbox bash

	cd /mnt/data-experiments-stack-exchange/

	rm -rf data/cleansed/*

	spark-submit --class alzatech.data_exp_stack_exchange.etl_users target/data-experiments-stack-exchange-0.1.0-SNAPSHOT-standalone.jar file:///mnt/data-experiments-stack-exchange/data/raw/users file:///mnt/data-experiments-stack-exchange/data/cleansed/users

	spark-submit --class alzatech.data_exp_stack_exchange.etl_posts target/data-experiments-stack-exchange-0.1.0-SNAPSHOT-standalone.jar file:///mnt/data-experiments-stack-exchange/data/raw/posts file:///mnt/data-experiments-stack-exchange/data/cleansed/posts

### Execute Spark Queries

Start a Clojure REPL and execute the code in `repl_example_4.clj`

## License

FIXME