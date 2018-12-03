# RESTServer

Jersey based REST Server with Hibernate+PostgreSQL persistence.


## Deployment of a Jersey based web app to a Tomcat web server

  * export an archived WAR from IntelliJ: configure artifact and build it;
  * copy resulting WAR file to: /var/lib/tomcat/webapp;
  * start Tomcat:
  ```
  tomcat start
  ```
  * verify that Tomcat is listening on port 8080:
  ```
  sudo lsof -i :8080

  # Outputs:

  COMMAND   PID   USER   FD   TYPE  DEVICE SIZE/OFF NODE NAME
  java    17040 tomcat   48u  IPv6 3598735      0t0  TCP *:webcache (LISTEN)
  ```

  * make a request at the app address:
  <http://localhost:8080/RESTServer/orders?stockCode=6753.T>

## Test server

Place an order using a POST request:
```
curl -H "Content-Type: application/json" --request POST --data '{"price":"7808", "quantity":"12000", "notes":"order", "instCode":"6753.T"}' http://localhost:8080/order/add
```

Fetch the list of orders using a GET request:
```
curl -H "Content-Type: application/json" --request GET http://localhost:8080/order
```
