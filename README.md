# Order Management System

## Project Overview
A menu-driven Order Management System developed using Java JDBC and MySQL to manage customer orders efficiently.

## Technologies Used
- Java
- JDBC
- MySQL
- Maven
- IntelliJ IDEA

## Database Details
**Database:** `ecommerce_db`

**Table:** `Orders`

| Column | Data Type |
|---|---|
| order_id | INT (Primary Key) |
| customer_name | VARCHAR(100) |
| product_name | VARCHAR(100) |
| quantity | INT |
| unit_price | DOUBLE |
| status | VARCHAR(30) |

## Features
1. Insert three order records using PreparedStatement.
2. Search orders by status and quantity greater than 2.
3. Increase the quantity of an existing order.
4. Update order status with validation.
5. Calculate total order value.
6. Delete orders only when their status is Cancelled.

## How to Run
1. Clone or download this repository.
2. Open the project in IntelliJ IDEA.
3. Create the `ecommerce_db` database and `Orders` table in MySQL.
4. Configure your MySQL username and password in the Java connection code.
5. Run the `Main.java` file.

## Author
Shreyansh
