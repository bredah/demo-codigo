MVN_REPORT := target/site/surefire-report.html
TIMESTAMP := $(shell date +'%F %T')
CERT_PATH := $(HOME)/.openssl/dev

## compile project
compile:
	mvn compile

## Run Unit Tests
unit-test:
	mvn test

## Run Integration Tests
integration-test:
	mvn test -Pintegration-test

## Run All Tests
test: unit-test integration-test

## Package application
package:
	mvn package

## Start application
start-app:
	mvn clean spring-boot:run

## Start application via JAR
start-jar: package
	java -jar target/demo-codigo-1.0-SNAPSHOT.jar

## Generate allure report
allure-generate-report:
	mvn allure:report

## Open allure report
allure-report: allure-generate-report
	mvn allure:serve