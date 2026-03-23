# Library Management System

## Project Overview

This is a comprehensive library management system built in Java that handles multiple library branches, user roles, book borrowing, late fees, and disciplinary tracking. The system features a complete graphical user interface with role-specific dashboards for managers, employees, patrons, and guests.

## What Has Been Implemented

### Core Functionality
- **Multi-Branch Support**: The system can manage multiple independent libraries, each with its own ID patterns, borrowing rules, and user base. Libraries can have different starting IDs and increments (e.g., one library uses 1000+7, another uses 2000+5).

- **Role-Based Access Control**: Six distinct user roles with different permissions:
   - Manager: Full system access, can generate IDs, view all users, add books
   - Employee: Can borrow/return books and view patron information
   - Junior Employee: Entry-level staff with same permissions as Employee
   - Volunteer: Helper role with standard permissions
   - Patron: Can borrow/return books, view personal history, pay fees
   - Guest: Browse-only access, cannot borrow

- **Automated ID Generation**: IDs are generated automatically based on user role and library pattern. Patrons get IDs starting at the library's base number, employees get IDs 4000 higher, and managers get IDs 8000 higher.

- **Borrowing System**: Each role has configurable borrowing limits, loan durations, and late fees. When a book is borrowed, the due date is calculated automatically. When returned, late fees are calculated based on how many days past due and the user's role.

- **Disciplinary System**: Infraction points are tracked for all users. Points are added for late returns, damaged books, or policy violations. Different thresholds trigger warnings, suspension, or termination. The system handles different rules for patrons vs employees.

- **Registration Flow**: Users register by first generating their ID (system automatically creates it), then completing their personal information and setting a password. No manager intervention is needed for standard registration.

- **Graphical User Interface**: A complete Swing-based GUI with:
   - Welcome screen with login, registration, and guest options
   - Role-specific dashboards that show different information based on user permissions
   - Tables for viewing users, books, and borrow history
   - Dialog boxes for adding books, borrowing, returning, and paying fees
   - Real-time statistics showing books, users, active loans, and overdue items

### Data Models
- **Book**: Tracks title, author, ISBN, availability, publication date, and borrowing status
- **User**: Tracks personal information, role, infraction points, status, fees, and suspension details
- **BorrowRecord**: Tracks every borrowing transaction including dates and fees
- **LibraryRegistry**: Manages multiple libraries and ensures unique ID patterns

### Key Features Working
- Users can register and log in
- Managers can generate IDs for new users
- Books can be added to the catalog
- Users can borrow and return books
- Late fees are calculated automatically
- Infraction points are tracked and trigger status changes
- Users can view their borrowing history
- Users can pay outstanding fees
- Different dashboards show different information based on user role
- Pending registrations are tracked separately from active users

## What Is Currently Being Worked On

### Database Integration
The next major feature is adding persistent storage using SQLite. Currently all data exists only in memory and is lost when the program closes. The database will store:
- Libraries and their configuration
- All users with their roles, status, and fees
- All books with their availability status
- Complete borrowing history including returns and late fees

This will make the system usable for real-world applications where data needs to persist between sessions.

## Future Plans

### Short Term
- **Complete Database Integration**: Implement full SQLite storage with all CRUD operations
- **Data Loading**: Load existing data from database on startup
- **Backup System**: Automatic database backups

### Medium Term
- **Email Notifications**: Send due date reminders and overdue alerts to users
- **Advanced Search**: Search by author, ISBN, publication year, and combine filters
- **Bulk Operations**: Add multiple books at once, batch returns
- **Export Reports**: Generate CSV reports for users, books, and transactions

### Long Term
- **REST API**: Create web service endpoints for mobile app integration
- **Mobile Application**: Android/iOS app for patrons to check availability and manage accounts
- **Barcode Scanning**: Use camera to scan ISBNs for quick book lookup and checkout
- **Reservation System**: Allow patrons to reserve books that are currently checked out
- **Inter-Library Loans**: Request books from other branches in the network
- **Online Payment Integration**: Pay late fees through payment processors
- **Statistical Dashboard**: Visual analytics showing borrowing trends, popular books, and user activity
- **Multi-Language Support**: Internationalize the interface for different languages

## Technical Highlights

- **Object-Oriented Design**: Clean separation of concerns with model, service, and UI layers
- **Role-Based Permissions**: Granular access control implemented through permission methods
- **Enum-Based Configuration**: UserRole and UserStatus enums for type-safe state management
- **Map-Based Rule Storage**: Role-based rules stored in HashMaps for O(1) lookup
- **Polymorphism**: Library interface allows different library implementations
- **Event-Driven GUI**: Swing components with action listeners for interactive user experience

## Project Status

The core system is feature-complete and functional. All planned business logic has been implemented. The current focus is on adding database persistence to make the system production-ready.

---

*This project demonstrates proficiency in Java, object-oriented design, GUI development, role-based access control, and multi-branch system architecture.*