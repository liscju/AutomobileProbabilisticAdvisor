call mvn clean compile assembly:single
call java -Djava.library.path=src\main\resources -jar target\AutomobileProbabilisticAdvisor-1.0-SNAPSHOT-jar-with-dependencies.jar