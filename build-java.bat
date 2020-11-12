mvn clean install package
copy target/*.jar docker/java
docker build -t videoviz docker/java
docker commit 0e9b78b051ae videoviz
docker tag videoviz 192.168.15.102:500/videoviz
