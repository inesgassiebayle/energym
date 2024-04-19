#!/usr/bin/env bash

java -cp /Users/luzlaura/IdeaProjects/hsqldb/lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:../db/energymdb --dbname.0 energymdb
