MVN_REPORT := target/site/surefire-report.html
TIMESTAMP := $(shell date +'%F %T')

MVN_ARGS := -Dorg.slf4j.simpleLogger.showDateTime=true \
			-Dorg.slf4j.simpleLogger.dateTimeFormat="yyyy-MM-dd HH:mm:ss.SSS" \
			-Dorg.slf4j.simpleLogger.showLogName=false \
			-Dorg.slf4j.simpleLogger.showShortLogName=false \
			-Dorg.slf4j.simpleLogger.showThreadName=false 

compile: # compile project
	@./mvnw $(MVN_ARGS) clean compile test-compile

package:
	@./mvnw $(MVN_ARGS) package -DskipTests

start-api:
	@./mvnw $(MVN_ARGS) clean spring-boot:run

start-jar: package
	@java -jar ./target/demo-codigo-*.jar

start-docker:
	@docker run --rm --name demo-aplicacao -p 8080:8080 -it demo/aplicacao

debug-api:
	@./mvnw $(MVN_ARGS) clean spring-boot:run -Dspring-boot.run.profiles=dev -Dspring.jmx.enabled=true

## Test

unit-test:
	@./mvnw $(MVN_ARGS) test

integration-test:
# @./mvnw $(MVN_ARGS) failsafe:integration-test
	@./mvnw $(MVN_ARGS) test -P integration-test

system-test:
	@./mvnw $(MVN_ARGS) test -Psystem-test
	@echo $(TIMESTAMP) [INFO] cucumber HTML report generate in: target/cucumber-reports/cucumber.html

performance-test:
	@./mvnw $(MVN_ARGS) gatling:test -Pperformance-test

test: unit-test integration-test

db-h2-dump:
	java -cp ~/.m2/repository/com/h2database/h2/2.1.214/h2*.jar org.h2.tools.Script -url jdbc:h2:~/test -user sa -script db-dump.sql

report-maven: # Gerar relatorio HTML utilizando maven
	@./mvnw $(MVN_ARGS) surefire-report:report
	@echo $(TIMESTAMP) [INFO] maven report generate in: $(MVN_REPORT)

report-allure:
	allure serve target


## Docker

docker-image: package
	docker build -t demo/aplicacao -f ./docker/Dockerfile .

docker-monitoring:
	docker-compose -f ./docker/docker-compose.yml up