# In-Memory Order Book - BTCZAR

## Overview
### *** NOTE: I have no prior experience with Kotlin, this is my first attempt at writing a Kotlin project. ***
This project implements a simple **in-memory order book** for the BTC/ZAR trading pair.  
It supports submitting **limit orders**, **order matching**, viewing all **open orders**, and retrieving **recent trades**.

The project is implemented in **Kotlin** using **Spring Boot**, with a focus on clean, clear, and simple code.

---

## Features
- **Submit Limit Orders:** Place BUY or SELL orders with a specific price and quantity.
- **Order Matching:** Incoming orders match against opposite side orders if prices overlap.
- **View Order Book:** Get current open bids and asks.
- **Recent Trades:** Retrieve the last 5 executed trades.
- **Validation:** Ensures orders have valid price, quantity, and currency pair.

---

## Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/BTCZAR/orderbook` | Returns the current order book |
| POST | `/api/orders/limit` | Submit a limit order (BUY or SELL) |
| GET | `/api/BTCZAR/tradehistory` | Returns the last 5 trades |

---

## How to Run

### Prerequisites
- Java 17+
- Maven 3.8+
- GitHub Codespaces or local IDE

### Running the API Server
#### The API server will start on http://localhost:8080.
#### Port Forwarding in Codespaces

Forward port 8080 from Codespaces to your local machine to access the API.
```bash
# Build the project
mvn clean install

# Run the Spring Boot application
mvn spring-boot:run
```
### Running Tests
#### Test reports are generated in target/surefire-reports.
#### 
```bash
# Run unit and integration tests
mvn test
```

#### Viewing Test Reports in Browser
```bash
#You can generate the HTML report and open it directly in your browser:
mvn surefire-report:report

#After this, the report is located at: target/reports/surefire.html
#Just right click and open in browser
```

### Summary
#### This project demonstrates:
- Core trading logic (limit orders, matching engine)
- Simple in-memory data structures for an order book
- Clean and maintainable Kotlin code
- Unit and E2E tests verifying core functionality
- API endpoints similar to VALR’s BTCZAR endpoints

