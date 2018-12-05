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
        timestamp date,
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

Add annotated classes to represent DB records:
```java
package com.rest.model;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.Serializable;
import javax.persistence.*;

@Entity
public class Orders implements Serializable {

    @Id
    @Column(name = "order_id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int order_id;

    private int version;
    private int inst_id;
    private double price;
    private double quantity;
    private String notes;

    public Orders() {

    }

    public Orders(int version, int inst_id, double price, double quantity, String notes) {
        this.version = version;
        this.inst_id = inst_id;
        this.price = price;
        this.quantity = quantity;
        this.notes = notes;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getOrderID() {
        return order_id;
    }

    public void setOrderID(int orderID) {
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

    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        try {
            res.put("OrderID", getOrderID());
            res.put("Quantity",getQuantity());
            res.put("Price",   getPrice());
            res.put("InstrID", getInst_id());
            res.put("Notes",   getNotes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
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

Insert records using HQL:

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
