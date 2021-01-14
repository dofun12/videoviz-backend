#!/bin/sh
mvn verify clean --fail-never -Dmaven.test.skip=true
mvn package -Dmaven.test.skip=true
touch VERSION.txt
echo $(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout) > VERSION.txt