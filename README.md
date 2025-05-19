# OOP Lab Assignment – Java E-Commerce System

## Overview

This project is a Java-based command-line e-commerce system developed as part of an Object-Oriented Programming (OOP) lab assignment. It follows standard OOP principles, utilizing model classes, operation classes, a control class, and a separate I/O interface to simulate a basic e-commerce management system.

> **Contributors**:  
> - Nguyễn Lê Mai Hương  
> - Nguyễn Ngọc Phương Như  

---

## Objectives

- Practice OOP principles such as class hierarchy, abstraction, encapsulation, and Singleton pattern.
- Design a maintainable and modular Java application.
- Manipulate data stored in `.txt` files (simulate a database).
- Provide a clean CLI-based user interface for both Admin and Customer roles.

---

## Features

### For Admins:
- Manage customers and products (add, delete, view)
- View all orders
- Generate and view statistical charts (category, discount, likes, consumption, best-sellers)
- Delete all system data
- Generate test data automatically

### For Customers:
- Register and login
- Update profile
- View available products (with keyword search)
- Purchase products
- View order history
- Generate personal consumption statistics

---

## Requirements

- **Java SE 17** or higher
- No external libraries (only `java.util`, `java.io`, `java.time`, optional: `javafx` for charts)

---

## How to Run

1. Clone this repository
2. Open the project in IntelliJ IDEA or any Java IDE
3. Run `Main.java` to start the application
4. Follow the on-screen menu prompts to navigate

> **Note**: All file input/output and printing are restricted to operation and IO classes respectively, as per assignment specification.

---

## Output Example

- Product listing with pagination
- User profiles printed in JSON-like format
- Chart files saved under `data/figure/` if implemented

---

## License

This project is for educational purposes only and submitted as part of the OOP lab assignment.
