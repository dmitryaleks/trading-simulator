# Trading Simulator

Enables placing orders and getting trades generated based on order's price-time priority.

Comes with programmatic REST API and a simple web front-end.

## Overall architecture

Jersey based REST Server with Hibernate+PostgreSQL persistence.

  * Database Engine:    PostgreSQL (9.5.7)
  * ORM:                Hibernate (4.2.15)
  * REST Server:        Jersey (2.23.2)
  * JSON processor:     Jackson (Java) (2.9.2)
  * Web Server:         Tomcat (v8.0.44)
  * Back-end tests:     JUnit (4.12)
  * REST API tests:     REST Assured (Java) (3.2.0)
  * Build management:   Maven (3.3.3)
  * Front-end:          React.js (15) + Axios (to fetch data from the REST server)
  * Push notifications: WebSocket (Java-WebSocket v1.3.0)
  * Charts:             Recharts (v1.4.1) (charts for React.js)
  * Front-end tests:    Selenium (Java) (3.4.0)

Relevant projects:
  * front-end: "Trading Dashboard":

  <https://github.com/dmi3aleks/React/tree/master/dashboard>

  * front-end and REST API tests:

  <https://github.com/dmi3aleks/WebAppTester>

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

## To test via Internet

Start a tonnel as follows:
```
ssh -R dmi3aleks:80:localhost:5000 serveo.net
```

Access the site via:
```
https://dmi3aleks.serveo.net
```

## Enabling CORS (Cross-Origin Request Sharing) on Jersey side

CORS needs to be enabled on the server side. Server should respond with proper headers.

Key headers to allow CORS on preflight requests made by browsers are as follows:
  * Access-Control-Allow-Origin: specifies what origins can make requests (* stands for any);
  * Access-Control-Allow-Headers: should contain "Crossdomain";
  * Access-Control-Allow-Methods: should contain "OPTIONS".

Define a filter:
```
package com.rest.cors;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request,
                       ContainerResponseContext response) throws IOException {
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHeaders().add("Access-Control-Allow-Headers",
                "Origin, Content-Type, Accept, Authorization, Crossdomain");
        response.getHeaders().add("Access-Control-Allow-Credentials", "true");
        response.getHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }
}
```

Apply filter:
```
package com.rest;

import com.rest.cors.CORSFilter;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class Server extends ResourceConfig {

    public Server() {
        register(CORSFilter.class);
    }
}
```

## Test server

Place an order using a POST request:
```
curl -H "Content-Type: application/json" --request POST --data '{"price":"7808", "quantity":"12000", "notes":"order", "instCode":"6753.T"}' http://localhost:8080/order/add
```

Fetch the list of orders using a GET request:
```
curl -H "Content-Type: application/json" --request GET http://localhost:8080/order
```

## Notes on PostgreSQL

### Installation

Install packages:
```
sudo dnf install postgresql-server
sudo postgresql-setup --initdb
```

Start the service:
```
sudo systemctl start postgresql
systemctl status postgresql
```

Set password for the new special user "postgres":
```
su - root
passwd postgress
# type a new password twice
```

Switch to the special user "postgres":
```
su - postgres
```

Enter the PostgreSQL interactive management tool:
```
psql
```

Create a new user (named "app" in this case):
```
createuser app -P
```

Create a new database (name "db" in this case):
```
createdb --owner=app db
```

Edit PostgreSQL configuration to allow user authentication:
```
sudo vim /var/lib/pgsql/data/pg_hba.conf
# change "ident" method to "md5" or "trust" (for testing purposes):

# TYPE  DATABASE        USER            ADDRESS                 METHOD

# "local" is for Unix domain socket connections only
local   all             all                                     trust
# IPv4 local connections:
host    all             all             127.0.0.1/32            trust
# IPv6 local connections:
host    all             all             ::1/128                 trust
```

Restart the service and verify that it runs successfully:
```
sudo systemctl restart postgresql
systemctl status postgresql
```

### Connecting to the database

From the regular (non-"postgres") user, connect to the database as follows:

```
psql -h localhost -U app db
```

To list all the available databases run:
```
\l
# alternatively:
\list
```

To list all the available tables run:
```
\d
# alternatively
\dt
```

To exit:
```
\q
```

### Creating tables

To create a table, connect to the database:
```
psql -h localhost -U app db
```

And then define a table using Data Definition Language, E.g. as follows:
```sql
CREATE TABLE instrument (
        timestamp date,
        instrument_id serial PRIMARY KEY,
        name varchar (128) NOT NULL,
        description varchar (256) NOT NULL
);
```

The resulting table should be visible now:
```
\d

# Outputs:

st of relations
 Schema |             Name             |   Type   | Owner
--------+------------------------------+----------+-------
 public | instrument                   | table    | app
 public | instrument_instrument_id_seq | sequence | app
(2 rows)
```

Table schema can be change later using the following syntax:
```
ALTER TABLE instrument ADD COLUMN is_active bool;
```

To delete an existing table:
```
DROP TABLE instrument;
```

### Insert data into tables

Insert data using psql command line:

```sql
INSERT INTO instrument (timestamp, name, description) VALUES (CURRENT_TIMESTAMP, "6758.T", "Sony Co.Ltd.");
INSERT INTO instrument (timestamp, name, description) VALUES (CURRENT_TIMESTAMP, '6753.T', 'Sharp Co.Ltd.');
INSERT INTO instrument (timestamp, name, description) VALUES (CURRENT_TIMESTAMP, '6702.T', 'Fujitsu Co.Ltd.');
```

Check the data:
```
SELECT * FROM instrument;

# Outputs:

 timestamp  | instrument_id |  name  |   description
------------+---------------+--------+-----------------
 2018-12-02 |             1 | 6758.T | Sony Co.Ltd.
 2018-12-02 |             2 | 6753.T | Sharp Co.Ltd.
 2018-12-02 |             3 | 6702.T | Fujitsu Co.Ltd.
(3 rows)
```

Add a relation (note that foreign key is defined using the "REFERENCES" keyword):

```sql
CREATE TABLE orders (
        timestamp(3) date,
        order_id serial PRIMARY KEY,
        version bigint,
        inst_id serial REFERENCES instrument(instrument_id),
        price real,
        quantity real,
        notes varchar (256) NOT NULL
);

INSERT INTO orders (timestamp, version, inst_id, price, quantity, notes) VALUES (CURRENT_TIMESTAMP, 1, 2, 7800, 10000, 'Direct order');
```

We can now join to the secondary table in a query:

```sql
SELECT * FROM orders INNER JOIN instrument ON instrument.instrument_id=orders.inst_id;

# Outputs:

 timestamp  | order_id | version | inst_id | price | quantity |    notes     | timestamp  | instrument_id |  name  |  description
------------+----------+---------+---------+-------+----------+--------------+------------+---------------+--------+---------------
 2018-12-02 |        1 |       1 |       2 |  7800 |    10000 | Direct order | 2018-12-02 |             2 | 6753.T | Sharp Co.Ltd.
(1 row)
```

To change table schema:

```
ALTER TABLE orders ADD COLUMN side char;
```

Update existing data:

```
UPDATE orders SET quantity_filled=0;
```

Grant permissions for table access to a given user:
```
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO app;
```

Grant permissions for serial key access to a given user:
```
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO app;
```

Reset sequence for a serial key:
```
ALTER SEQUENCE trade_trade_id_seq RESTART WITH 1;
```

Check the latest value in the sequence used for a serial key:
```
SELECT last_value FROM orders_order_id_seq;
```

## Notes on Hibernate

### Setup with Maven

Add Maven dependencies:
```
<properties>
	<hibernate.version>4.2.15.Final</hibernate.version>
</properties>

<dependency>
	<groupId>org.hibernate</groupId>
	<artifactId>hibernate-entitymanager</artifactId>
	<version>${hibernate.version}</version>
</dependency>

<dependency>
	<groupId>org.hibernate</groupId>
	<artifactId>hibernate-core</artifactId>
	<version>4.2.15.Final</version>
</dependency>
```

Define Hibernate properties for a PostgreSQL connection:
  * make sure to point Hibernate at the right DB connection URL: "jdbc:postgresql://localhost:5432/db"
  * make sure to include mappings for annotated classes (see <mapping /> entries at the bottom).

main/resources/hibernate.cfg.xml:
```
<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">

<hibernate-configuration>
    <session-factory>

        <!-- Connection settings -->
        <property name="hibernate.connection.driver_class">org.postgresql.Driver</property>
        <property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/db</property>
        <property name="hibernate.connection.username">app</property>
        <property name="hibernate.connection.password">app</property>

        <!-- SQL dialect -->
        <property name="hibernate.dialect">org.hibernate.dialect.PostgreSQLDialect</property>

        <!-- Print executed SQL to stdout -->
        <property name="show_sql">true</property>

        <!-- Drop and re-create all database on startup -->
        <!--property name="hibernate.hbm2ddl.auto">create-drop</property-->

        <!-- Annotated entity classes -->
        <mapping class="com.rest.model.Orders"/>
        <mapping class="com.rest.model.Instrument"/>

    </session-factory>
</hibernate-configuration>
```

Add annotated classes to represent DB records (note how ID, Timestamp and Side fields are being handled):
```java
package com.rest.model;
import com.rest.model.common.Side;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;
import javax.persistence.*;

@Entity
public class Orders implements Serializable {

    @Id
    @Column(name = "order_id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long order_id;

    @Column(name="timestamp", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date timestamp;

    private int version;
    private int inst_id;
    private double price;
    private double quantity;
    private String notes;

    @Enumerated(EnumType.STRING)
    private Side side;

    private double quantity_filled;
    private double fill_price;
    private String status;

    public Orders() {
    }

    public Orders(Long orderID) {
        this.order_id = orderID;
    }

    public Orders(int version, int inst_id, Side side, double price, double quantity, String notes) {
        this.version = version;
        this.inst_id = inst_id;
        this.price = price;
        this.quantity = quantity;
        this.notes = notes;
        this.side = side;
        this.status = "A";
        this.quantity_filled = 0;
        this.fill_price = 0;
        this.timestamp = new Date(new java.util.Date().getTime());
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getOrderID() {
        return order_id;
    }

    public void setOrderID(Long orderID) {
        this.order_id = orderID;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getInst_id() {
        return inst_id;
    }

    public void setInst_id(int inst_id) {
        this.inst_id = inst_id;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public double getQuantity_filled() {
        return quantity_filled;
    }

    public void setQuantity_filled(double quantity_filled) {
        this.quantity_filled = quantity_filled;
    }

    public double getFill_price() {
        return fill_price;
    }

    public void setFill_price(double fill_price) {
        this.fill_price = fill_price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getTimestampString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return sdf.format(getTimestamp());
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @PrePersist
    protected void onCreate() {
        timestamp = new Date(new java.util.Date().getTime());
    }

    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        try {
            res.put("OrderID", getOrderID());
            res.put("Quantity",getQuantity());
            res.put("Price",   getPrice());
            res.put("InstrID", getInst_id());
            res.put("Notes",   getNotes());
            res.put("Side",    getSide().getCode());
            res.put("QuantityFilled", getQuantity_filled());
            res.put("FillPrice", getFill_price());
            res.put("Status",  getStatus());
            res.put("Timestamp", getTimestampString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getMatchingKey() {
        return String.format("%d-%s", getInst_id(), getSide() == Side.B?"S":"B");
    }

    public String getSelfKey() {
        return String.format("%d-%s", getInst_id(), getSide().name());
    }

    public String toString() {
        return String.format("#%d: [%d] %s %.2f@%.2f", getOrderID(), getInst_id(), getSide().name(), getQuantity(), getPrice());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Orders orders = (Orders) o;
        return order_id.equals(orders.order_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order_id);
    }

    public Orders addTrade(double quantity, double price) {
        double currentFilledQty = getQuantity_filled();
        double currentFillPrice = getFill_price();

        double newFillPrice = ((currentFillPrice * currentFilledQty) + (price * quantity))/(currentFilledQty + quantity);

        setQuantity_filled(currentFilledQty + quantity);
        setFill_price(newFillPrice);
        return this;
    }
}
```

Do lookups using HQL (Hibernate Querying Language):
```java
public static List<JSONObject> getAllOrders() throws OrderLookupException {

	Session session = SessionManager.getSessionFactory().openSession();
	final String getInstrHQL =
        String.format("SELECT O, I FROM Orders O, Instrument I WHERE I.instrument_id = O.inst_id");
	Query query = session.createQuery(getInstrHQL);
	List<Object[]> res = query.list();
	session.close();
	if(res.size() == 0) {
		throw new OrderLookupException(String.format("No orders found"));
	}

	List<JSONObject> orders = new LinkedList<>();
	for(Object[] elem: res) {
	   Orders ord = (Orders) elem[0];
	   Instrument instr = (Instrument) elem[1];
		try {
			orders.add(ord.getJSON().put("InstrCode", instr.getName()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	return orders;
}
```

Insert records using a Hibernate session:

```java
SessionFactory sessionFactory = new Configuration().configure()
		.buildSessionFactory();
Session session = sessionFactory.openSession();

session.beginTransaction();

Orders ord = new Orders(1, instID, price, quantity, notes);
session.save(ord);
session.getTransaction().commit();
session.close();
```

Select records using HQL:

```java
public static List<JSONObject> getAllTrades() throws TradeLookupException {

	Session session = SessionManager.getSessionFactory().openSession();
	final String getInstrHQL = String.format("SELECT T FROM Trade T ORDER BY T.trade_id");
	Query query = session.createQuery(getInstrHQL);
	List<Trade> trades = query.list();
	session.close();
	if(trades.size() == 0) {
		throw new TradeLookupException(String.format("No trades found"));
	}

	return trades.stream().map(t -> t.getJSON()).collect(Collectors.toList());
}
```

Select records using Session.get():

```java
public static Orders getOrder(final String orderID) throws OrderLookupException {

	Session session = SessionManager.getSessionFactory().openSession();
	Orders order = (Orders)session.get(Orders.class, Long.valueOf(orderID));
	session.close();

	if(order == null) {
		throw new OrderLookupException(String.format("Order %s not found", orderID));
	}

	return order;
}
```

### Entity state machine

Any entity instance in your application appears in one of the three main states in relation to the Session persistence context:
  * transient — this instance is not, and never was, attached to a Session; this instance has no corresponding rows in the database; it’s usually just a new object that you have created to save to the database;
  * persistent — this instance is associated with a unique Session object; upon flushing the Session to the database, this entity is guaranteed to have a corresponding consistent record in the database;
  * detached — this instance was once attached to a Session (in a persistent state), but now it’s not; an instance enters this state if you evict it from the context, clear or close the Session, or put the instance through serialization/deserialization process.

### Action types

#### When persisting entities

  * persist - transitions entity into a persistent state without generating an ID;
  * save    - transitions entity into a persistent state and generates an ID immediately (issues an ID generation command to DBMS);
  * merge   - updates a persistent entity instance with new field values from a detached entity instance;
  * update  - transitions the passed object from detached to persistent state;

#### When retrieving entities

  * get     - selects record from DBMS and creates an object fo rit;
  * load    - creates a proxy object that can be used to establish a relationship with other records without selecting data from DBMS (E.g. when commiting a relation b/w records).
  * evict   - transitions the passed object from persistent to detached state.

## Notes on WebSockets

WebSocket is a communications protocol, providing full-duplex communication channels over a single TCP connection.

### Maven dependencies

```
<dependency>
	<groupId>org.java-websocket</groupId>
	<artifactId>Java-WebSocket</artifactId>
	<version>1.3.0</version>
</dependency>
```

### A simple server that allows pushing notifications to all connected and subscribed clients via WebSocket

Key points:
  * subclass WebSocketServer;
  * implement abstract parent methods: onOpen(), onMessage(), onClose(), onError();
  * pass port to listen on to the parent constructor;
  * start the server by calling start() method.

```java
package com.rest.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.socket.message.Message;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server extends WebSocketServer {

    private static Server instance;

    public static Server getInstance() {
        if(instance == null) {
            instance = new Server(7888);
        }
        return instance;
    }

    private Set<WebSocket> connections;
    // maps subjects to subscribers
    private Map<String, Set<WebSocket>> subscribers;
    private Map<String, String> subjectData;

    public Server(int port) {
        super(new InetSocketAddress(port));
        connections = new HashSet<>();
        subscribers = new HashMap<>();
        subjectData = new HashMap<>();
    }

    @Override
    public void start() {
        log(String.format("Starting a server at port: %d", super.getPort()));
        super.start();
    }

    public void updateSubject(final String subject, final String data) {
        subjectData.put(subject, data);
        pushUpdate(subject);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        connections.add(webSocket);
        log(String.format("New connection from %s:%d",
                webSocket.getRemoteSocketAddress().getHostName(),
                webSocket.getRemoteSocketAddress().getPort()));

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        connections.remove(webSocket);

    }

    @Override
    public void onMessage(WebSocket webSocket, String msg) {
        log("New message: " + msg);

        ObjectMapper mapper = new ObjectMapper();

        try {
            Message message = mapper.readValue(msg, Message.class);

            switch(message.getType()) {
                case SUBSCRIBE:
                    log("A new user has subscribed");
                    subscribe(message.getSubject(), webSocket);
                break;
                case UNSUBSCRIBE:
                    log("A user has unsubscribed");
                    unsubscribe(message.getSubject(), webSocket);
                break;
            }
        } catch (IOException e) {
        }

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        log("[ERROR] " + e.getMessage());
    }


    private void pushUpdate(final String subject) {
        if(!subscribers.containsKey(subject)) {
            return;
        }

        List<WebSocket> subscribersToBeDiscontinued = new LinkedList<>();

        subscribers.get(subject).forEach(subscriber -> {
            try {
                subscriber.send(subjectData.get(subject));
            } catch (Throwable e) {
                log("Could not send an update to the client");
                // unsubscribe implicitly
                subscribersToBeDiscontinued.add(subscriber);
            }
        });
        subscribersToBeDiscontinued.forEach(s -> unsubscribe(subject, s));
    }

    private void unsubscribe(final String subject, final WebSocket subscriber) {
        if(!subscribers.containsKey(subject) ||
           !subscribers.get(subject).contains(subscriber)) {
            return;
        }
        subscribers.get(subject).remove(subscriber);
    }

    private void subscribe(final String subject, final WebSocket subscriber) {
        if(subscribers.containsKey(subject)) {
            subscribers.get(subject).add(subscriber);
        } else {
            subscribers.put(subject, new HashSet<>(Arrays.asList(subscriber)));
        }
    }

    static void log(final String msg) {
        final String title = "[SERVER] ";
        System.out.println(title + msg);
    }

    static String getCurrentTimestamp() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return sdf.format(new Date());
    }
}
```

### Simple JavaScript client that subscribes to a subject and updates React.js state whenever server pushes an update

#### Socket wrapper

```javascript
const Config = {
  PROTOCOL: "ws:",
  HOST: "//localhost",
  PORT: ":7888"
}

const Socket = (function() {

  let instance;

  function createInstance() {
    const address = Config.PROTOCOL + Config.HOST + Config.PORT
    const socket = new WebSocket(address)
    console.log("Created a Socket instance at: " + address)
    return socket;
  }

  return {
    getInstance: function() {
      if(!instance) {
        instance = createInstance()
      }
      return instance
    }
  }


})();

export default Socket;
```

#### Subscription and push notification processing

```javascript
import Socket from './socket/Socket'

class App extends React.Component {

  constructor() {
    super()

    this.subscribeForPushNotifications()
  }

  subscribeForPushNotifications() {

    const socket = Socket.getInstance()
    socket.onopen = () => {
      const msg = JSON.stringify({type:"SUBSCRIBE", subject:"ORDERS"})
      socket.send(msg)
    }
    socket.onmessage = (msg) => {
      console.log("SOCKET: " + msg.data)
      this.refreshData()
    }
  }

  //...skipping other logic...

}
```


## Notes on Jackson (JSON Parser for Java)

Jackson is a high-performance JSON parser for Java.

### Maven dependencies

```
<dependency>
	<groupId>com.fasterxml.jackson.core</groupId>
	<artifactId>jackson-databind</artifactId>
	<version>2.9.2</version>
</dependency>
```

### A sample POJO class used for data binding:

```java
package com.rest.socket.message;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {

    private MessageType type;
    private String subject;
    private String data;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
```

### Sample of JSON message parsing

```java
@Override
public void onMessage(WebSocket webSocket, String msg) {
	log("New message: " + msg);

	ObjectMapper mapper = new ObjectMapper();

	try {
		Message message = mapper.readValue(msg, Message.class);

		switch(message.getType()) {
			case SUBSCRIBE:
				log("A new user has subscribed");
				subscribe(message.getSubject(), webSocket);
			break;
			case UNSUBSCRIBE:
				log("A user has unsubscribed");
				unsubscribe(message.getSubject(), webSocket);
			break;
		}
	} catch (IOException e) {
	}
}
```
