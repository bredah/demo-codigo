MVN_REPORT := target/site/surefire-report.html
TIMESTAMP := $(shell date +'%F %T')
CERT_PATH := $(HOME)/.openssl/dev

compile: # compile project
	./mvnw clean compile test-compile

package:
	./mvnw package -DskipTests

start-api:
	./mvnw clean spring-boot:run

start-jar: package
	java -jar ./target/demo-codigo-*.jar

start-docker:
	docker run --rm --name demo-aplicacao -p 8080:8080 -it demo/aplicacao

debug-api:
	./mvnw clean spring-boot:run -Dspring-boot.run.profiles=dev -Dspring.jmx.enabled=true

report-maven: # Gerar relatorio HTML utilizando maven
	@./mvnw  surefire-report:report
	@echo $(TIMESTAMP) [INFO] maven report generate in: $(MVN_REPORT)

## Test

unit-test:
	@./mvnw test

integration-test:
	@./mvnw test -Pintegration-test

system-test:
	@./mvnw test -Psystem-test

performance-test:
	@./mvnw gatling:test -Pperformance-test

test: unit-test integration-test

db-h2-dump:
	java -cp ~/.m2/repository/com/h2database/h2/2.1.214/h2*.jar org.h2.tools.Script -url jdbc:h2:~/test -user sa -script db-dump.sql

## Docker

docker-image: package
	docker build -t demo/aplicacao -f ./docker/Dockerfile .

docker-monitoring:
	docker-compose -f ./docker/docker-compose.yml up