# PublicLibrary Management System

A Java-based library management system that handles book borrowing, returns, and late fee calculations. This project demonstrates object-oriented programming principles, data structures, and real-world system design.

## 📚 Overview

This system allows libraries to manage their book inventory, register users, track borrowing activities, and calculate late fees. It features a unique ID generation system where each library maintains its own catalog numbers for books and users.

## ✨ Features

- **Book Management**: Add books with ISBN, title, author, and publication date
- **User Registration**: Register library patrons with contact information
- **Borrowing System**: Check out books with automatic due date calculation
- **Return Processing**: Handle book returns with late fee calculation
- **ID Generation**: Each library generates its own unique ID patterns (starting at 1000, incrementing by 7)
- **Borrow Tracking**: Complete history of all borrowing transactions
- **Late Fees**: Automatic calculation of late fees based on daily rate

## 🏗️ Class Structure

### `Book.java`
Represents a book in the library system.
- **Properties**: title, author, ISBN, availability status, publication date
- **Methods**: borrow, return, check overdue status, days until due

### `User.java`
Represents a library patron.
- **Properties**: ID, name, email, phone, borrowed books list
- **Methods**: borrow book, return book, check borrowed count

### `BorrowRecord.java`
Tracks each borrowing transaction.
- **Properties**: book ID, user ID, borrow date, due date, return date, late fee
- **Methods**: calculate late fee, check overdue status, process return

### `PublicLibrary.java`
Main system controller.
- **Properties**: book catalog, user registry, active loans, borrow history
- **Methods**: add books, register users, process borrows/returns, generate reports

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Any Java IDE (Eclipse, IntelliJ, VS Code) or command line

### Installation
1. Clone the repository
2. Compile all Java files:
   ```bash
   javac *.java