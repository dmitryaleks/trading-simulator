**Order entry**
----
  Enters a single order.

* **URL**

  /order/add

* **Method:**

  `POST`

*  **URL Params**

  None

* **Data Params**

   **Required:**

   `Side=[string] ('S'ell or 'B'uy)'

   `InstrCode=[string]`

   `Quantity=[float]`

   `Price=[float]`

   **Optional:**

   `Notes=[string]`

* **Success Response:**

  * **Code:** 200

    **Content:** `{ OrderId : value }`

* **Error Response:**

  * **Code:** 400 BAD REQUEST <br />
    **Content:** `{ error : "[attribute] is missing" }`

* **Sample Call:**

  ```javascript
    axios.post(serverHostName + '/order/add', {
      "Side": "S",
      "InstrCode": "6758.T",
      "Price": "5500",
      "Quantity": "1000",
      "Notes": "Order #42",
    })
    .then(
        (response) => {},
        (error) => { console.log(error) }
    );
  ```

**Order cancellation**
----
  Cancels a single order by ID.

* **URL**

  /order/delete

* **Method:**

  `POST`

*  **URL Params**

  None

* **Data Params**

   **Required:**

   `OrderId=[string]`

* **Success Response:**

  * **Code:** 200

    **Content:** `{ OrderID : value }`

* **Error Response:**

  * **Code:** 400 BAD REQUEST <br />
    **Content:** `{ error : "Unknown order ID" }`

* **Sample Call:**

  ```javascript
    axios.post(serverHostName + '/order/cancel', {
      "OrderId": "42"
    })
    .then(
        (response) => {},
        (error) => { console.log(error) }
    );
  ```

**Order retrieval**
----
  Retrieves order information.

* **URL**

  /order

* **Method:**

  `GET`

*  **URL Params**

   **Optional:**

   `limit=[integer]`

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200
    **Content:** `[{"OrderID":53609,"Quantity":1000,"Price":800,"InstrID":1,"Notes":"A","Side":"Buy","QuantityFilled":1000,"FillPrice":800,"Status":"C","Timestamp":"2018-12-29T11:06:31.309","InstrCode":"6758.T"}]`

* **Error Response:**

	None

* **Sample Call:**

  ```javascript
    axios.get(serverHostName + `/order?limit=` + this.orderDisplayLimit)
      .then(res => {
        const order_list = res.data;
        if (typeof(order_list) != 'string') {
          this.setState({ orders: order_list });
        } else {
          this.setState({ orders: [] });
        }
      });
  ```

**Trade retrieval**
----
  Retrieves trade information.

* **URL**

  /trade

* **Method:**

  `GET`

*  **URL Params**

   **Optional:**

   `limit=[integer]`

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200
    **Content:** `[{"TradeID":26989,"RestingOrderID":53606,"IncomingOrderID":53609,"Quantity":1000,"Price":800,"Timestamp":"2018-12-29T11:06:31.309"}]`

* **Error Response:**

	None

* **Sample Call:**

  ```javascript
    axios.get(serverHostName + `/trade?limit=` + this.tradeDisplayLimit)
      .then(res => {
        const trade_list = res.data;
        if (typeof(trade_list) != 'string') {
          this.setState({ trades: trade_list });
        } else {
          this.setState({ trades: [] });
        }
      });
  ```

**Instrument retrieval**
----
  Retrieves list of available instruments.

* **URL**

  /instruments

* **Method:**

  `GET`

*  **URL Params**

  None

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200
    **Content:** `[{"InstrID":1,"InstrCode":"6758.T","Desciption":"Sony Co.Ltd."},{"InstrID":2,"InstrCode":"6753.T","Desciption":"Sharp Co.Ltd."},{"InstrID":3,"InstrCode":"6702.T","Desciption":"Fujitsu Co.Ltd."}]`

* **Error Response:**

	None

* **Sample Call:**

  ```javascript
      axios.get(serverHostName + `/instruments`)
        .then(res => {
          const inst_list = res.data;
          this.setState({ instruments: inst_list });
        });
  ```
