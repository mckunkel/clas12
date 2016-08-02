mvn install
cd coat-distrib
mvn package
cp target/coat-libs-2.4-SNAPSHOT.jar $COATJAVA/lib/clas/
cd ..
