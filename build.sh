#!/bin/sh
mvn verify clean --fail-never
mvn package -DskipTests
touch VERSION.txt
echo $(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout) > VERSION.txt