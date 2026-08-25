## FinProject — Payroll utility

Short description (for GitHub): A small Java payroll utility that calculates base pay and deductions and integrates with a MySQL database for storage and configuration.

About
-
This repository contains a simple payroll calculation program (`com.payyoupayroll.Main`) that reads database configuration from `src/main/resources/db.properties`, connects to a MySQL database, and performs payroll-related calculations and persistence.

Prerequisites
- Java 17 or newer installed (project `pom.xml` is set to Java 17; Java 17+ is recommended)
- Apache Maven 3.x
- A running MySQL instance and credentials with a database (default referenced: `payroll_system`)

Configuration
- Edit `src/main/resources/db.properties` and set your MySQL connection values. Example:

```
db.url=jdbc:mysql://localhost:3306/payroll_system
db.user=root
db.password=pwd
```

Build & Run

Method A — Run via Maven (recommended):

```
mvn clean compile exec:java -Dexec.mainClass="com.payyoupayroll.Main"
```

Method B — Build and run with copied dependencies (Windows):

```
mvn clean package dependency:copy-dependencies -DoutputDirectory=target/dependency
java -cp "target/classes;target/dependency/*" com.payyoupayroll.Main
```

Notes
- If you see a `ClassNotFoundException` for the MySQL driver, ensure `mysql-connector-j` is present in `pom.xml` (it is included by default in this project).
- If the program fails to connect, verify `db.url`, `db.user`, and `db.password`, and ensure your MySQL server accepts connections from this host.

Debugging
- Quick compile check:

```
mvn clean compile
```

- Run tests (if any):

```
mvn test
```

Contact / Contributing
- Open an issue or submit a PR with improvements. Add tests for any behavioral changes.

License
- Add a license file if you plan to publish this repository.

