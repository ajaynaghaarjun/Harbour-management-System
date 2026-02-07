# Harbor Management System

A comprehensive Java desktop application for managing harbor operations. Built with Java Swing and MySQL.

## Features

- Complete management of ships, docks, cargo, and employees
- Harbor dashboard with key statistics and visualizations
- Ship tracking with cargo management and weight monitoring
- Dock management with status tracking and inspections
- Employee management with role assignments
- Full CRUD operations for all database entities
- Modern UI with professional design

## Requirements

- Java 11 or higher
- MySQL 8.0 or higher

## Installation

1. Create a MySQL database and import the provided SQL dump file:
   ```
   mysql -u root -p < Dump20250505.sql
   ```

2. Compile and run the application:
   ```
   mvn clean install
   java -jar target/harbor-management-system-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## Database Structure

The application uses a MySQL database with the following main tables:

- harbor - Harbor information
- ships - Ship details
- cargo - Cargo information
- dock - Dock details
- employees - Employee information
- roles - Employee roles

## Technology Stack

- Java 11
- Swing for GUI
- MySQL with JDBC
- JFreeChart for data visualization
- FlatLaf for modern UI look and feel
- Maven for dependency management

## Architecture

The application follows the MVC (Model-View-Controller) pattern:

- Model: Java classes representing database entities
- View: Swing UI components
- Controller: DAO classes and business logic

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License.