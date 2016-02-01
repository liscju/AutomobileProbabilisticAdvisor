Building jar:
$ mvn clean compile assembly:single

Running project:
$ java -Djava.library.path=src\main\resources \
     -jar target\AutomobileProbabilisticAdvisor-1.0-SNAPSHOT-jar-with-dependencies.jar

Or the easiest way:
$ run.bat