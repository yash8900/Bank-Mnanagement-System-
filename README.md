# Bank-Mnanagement-System-

A multi-modular enterprise application engineered to automate frontline hospitality workflows, manage guest check-ins, track room inventories, and streamline administrative records in real time.

Built as part of the *Master of Computer Applications (MCA) Major Project* at *Bundelkhand University, Jhansi.

---

## 🛠️ Tech Stack & Dependencies

* *Language:* Core Java (JDK 8+)
* *GUI Framework:* Java Swing, AWT
* *Database Connectivity:* JDBC (Java Database Connectivity)
* *Database Management:* MySQL Server 8.0 & MySQL Workbench
* *Ide/Environment:* Apache NetBeans IDE
* *Version Control:* Git & GitHub

---

## 🚀 Key Architectural Features

1. *Relational Data Integrity:* Designed schema structures using strict Primary Keys (Aadhar/Document IDs) and Foreign Key constraints with cascading behavior to eliminate duplicate entries and transaction collisions.
2. *Real-time State Synchronization:* Implemented atomic autoCommit transactional execution within the JDBC pipeline to instantly reflect room status modifications (Available → Occupied) upon customer check-in.
3. *Defensive Exception Handling:* Structured backend driver connection routines to handle connection timeouts, port mapping errors (Port 3306), and prevent runtime NullPointerExceptions.
4. *Cloud Migration Pipeline:* Actively transitioning localized database instances into high-availability cloud relational storage layers (IBM Cloud).
