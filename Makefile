MVN_REPORT := target/site/surefire-report.html
TIMESTAMP := $(shell date +'%F %T')
CERT_PATH := $(HOME)/.openssl/dev


build-docker:
	docker build -t demo/aplicacao .

compile: # compile project
	./mvnw clean compile test-compile

start-api:
	./mvnw clean spring-boot:run

start-jar:
	./mvnw package -DskipTests
	java -jar ./target/demo-codigo-*.jar

start-docker:
	docker run --rm --name demo-aplicacao -p 8080:8080 -it demo/aplicacao

debug-api:
	./mvnw clean spring-boot:run -Dspring-boot.run.profiles=dev -Dspring.jmx.enabled=true

report-maven: # Gerar relatorio HTML utilizando maven
	@./mvnw  surefire-report:report
	@echo $(TIMESTAMP) [INFO] maven report generate in: $(MVN_REPORT)

unit-test:
	@./mvnw test

integration-test:
	@./mvnw test -Pintegration-test

system-test:
	@./mvnw test -Psystem-test

test: unit-test integration-test

db-h2-dump:
	java -cp ~/.m2/repository/com/h2database/h2/2.1.214/h2*.jar org.h2.tools.Script -url jdbc:h2:~/test -user sa -script db-dump.sql
