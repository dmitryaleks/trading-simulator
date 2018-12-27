# Trading Simulator

Simulates continuous trading, enabling placing orders and getting trades generated based on order's price-time priority.

Comes with programmatic REST API and a simple web front-end:

## Small View

![Front-end view (mobile)](docs/img/TSD_small.gif)

## Medium View

![Front-end view (mobile)](docs/img/TSD_medium.gif)

## Big View

![Front-end view (mobile)](docs/img/TSD_big.gif)

 
## Overall architecture

Jersey based RESTFull API Server backed by Hibernate/PostgreSQL data persistence layer.

React.js based front-end that comminicates with the backend via RESTFull API and gets push notifications via WebSocket.

### Technology stack

  * Database Engine:    PostgreSQL (9.5.7)
  * ORM:                Hibernate (4.2.15)
  * REST Server:        Jersey (2.23.2)
  * JSON processor:     Jackson (Java) (2.9.2)
  * Back-end config:    Apache Commons Configuration (1.6)
  * Web Server:         Tomcat (v8.0.44)
  * Back-end tests:     JUnit (4.12)
  * REST API tests:     REST Assured (Java) (3.2.0)
  * Build management:   Maven (3.3.3)
  * Front-end:          React.js (15) + Axios (to fetch data from the REST server)
  * Push notifications: WebSocket (Java-WebSocket v1.3.0) + PostgreSQL NOTIFY table trigger and PG-JDBC-NG (0.7.1) Driver based listener in Java.
  * Charts:             Recharts (v1.4.1) (charts for React.js)
  * Front-end tests:    Selenium (Java) (3.4.0)
  * Front-end build:    npm

### Cloud setup

  * RDBMS:              AWS RDS instance of PostgreSQL;
  * Web-server:         Tomcat running a RESTFull API server in AWS EC2 Instance of Amazon Linux;
  * Front-end:          optimized React.js build hosted from AWS S3 bucket;
  * integration tests:  Maven driven REST Assured test running from a dedicated AWS EC2 instance;
  * routing:            AWS Route 53 based routing for S3 bucket serving a React.js site and for the API endpoints residing in EC2 instance.

### Deployment

  * frontend:           "npm run deploy" - automatically uploads an optimized build to AWS S3 bucket using AWS Command Line Interface;
  * backend:            "mvn package" followed by WAR upload via Tomcat Management interface.

### Related code repositories

  * front-end: "Trading Dashboard":

  <https://github.com/dmi3aleks/React/tree/master/dashboard>

  * integration tests for the front-end and REST API:

  <https://github.com/dmi3aleks/WebAppTester>

### Live instances hosted in a public AWS cloud

App front-end:

<http://trade.dmitryaleks.com>

API endpoints:

<http://simulator.dmitryaleks.com:8080/api/order?limit=10>

<http://simulator.dmitryaleks.com:8080/api/trade?limit=10>

Tomcat Management Console:

<http://simulator.dmitryaleks.com:8080/manager/html>

### Project Wiki

<https://github.com/dmi3aleks/TradingSimulator/wiki>

#### Shortcuts:

How to build this project:

<https://github.com/dmi3aleks/TradingSimulator/wiki/Build>

Technical notes:

<https://github.com/dmi3aleks/TradingSimulator/wiki/Technical-Notes>

REST API documentation:

<https://github.com/dmi3aleks/TradingSimulator/wiki/API>
