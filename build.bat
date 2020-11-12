mvn clean install package
mkdir target\dependency
cd target\dependency
jar -xf ../*.jar
