# 📚 Library Management Console App

This is a **Java-based console application** for managing a library system. It allows:

- **Admins** to manage books and users.
- **Regular Users** to log in, borrow, and return books.

All data is persisted using a **MySQL database**, and the project is containerized with **Docker** and **Docker Compose**. An **Adminer UI** is provided for inspecting and managing the database easily.

---

## Features

- Console-based interface for Admins and Regular Users  
- Persistent data storage with MySQL  
- Data serialization via Java Object Streams  
- Adminer UI for visualizing and querying the DB  
- Environment-based configuration for flexibility and security  

---

## Prerequisites

Ensure you have the following installed:

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

---

## Project Structure

```
📦 library-management-app/
├── 📁 src/                  # Java source files (Main, models, services)
├── 📄 Dockerfile            # Builds the Java app container
├── 📄 docker-compose.yml    # Defines app, db, and adminer services
├── 📄 .env                  # Environment variables for DB/app settings
└── 📄 README.md             # Project documentation
```

---

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/library-management-app.git
cd library-management-app
```

### 2. Create a `.env` File

Create a `.env` file in the project root:

```env
# MySQL Configuration
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_DATABASE=library

# Java App DB Connection Settings
DB_HOST=db
DB_PORT=3306
DB_NAME=library
DB_USER=root
DB_PASSWORD=your_root_password
```

### 3. Build and Run the Application

```bash
docker-compose up --build
```

This will:

- Build the Java app image using the Dockerfile  
- Start **MySQL (db)**, your **Java app (app)**, and **Adminer (adminer_ui)**  
- Automatically wait until the database is ready before launching the Java app  

---

## Application Usage

Once the app starts, you'll see a console interface prompting you to:

- Log in or register as Admin or Regular User
- Borrow, return, or manage books depending on your role

If the interactive prompt doesn’t appear automatically, attach to the container:

```bash
docker attach library_app
```

To detach without stopping it:  
→ Press `Ctrl + P`, then `Ctrl + Q`.

---

## Adminer UI Access

Inspect the MySQL database using **Adminer**:

- Open: [http://localhost:8080](http://localhost:8080)

**Use the following credentials:**

| Field    | Value                |
| -------- | -------------------- |
| System   | MySQL                |
| Server   | `db`                 |
| Username | `root`               |
| Password | `your_root_password` |
| Database | `library`            |

---

## Environment Variable Descriptions

| Variable              | Description                          |
| --------------------- | ------------------------------------ |
| `MYSQL_ROOT_PASSWORD` | Root password for MySQL              |
| `MYSQL_DATABASE`      | Default database name (`library`)    |
| `DB_HOST`             | Hostname for app to connect to DB    |
| `DB_PORT`             | DB port (default: `3306`)            |
| `DB_NAME`             | Database name                        |
| `DB_USER`             | Database user (`root`)               |
| `DB_PASSWORD`         | Password for database user           |

In Java, these are accessed using:

```java
System.getenv("DB_HOST");
System.getenv("DB_PORT");
System.getenv("DB_NAME");
System.getenv("DB_USER");
System.getenv("DB_PASSWORD");
```

---

## Troubleshooting

### App can’t connect to DB (Communications link failure)

- Make sure `DB_HOST=db` in `.env`
- Verify the DB container is running
- Run `docker-compose logs db` to check status

### Environment variables return `null`

- Ensure `.env` is in the **same directory** as `docker-compose.yml`
- If running outside Docker (e.g., IntelliJ), `.env` isn't auto-loaded:
  - Use JVM args like `-DDB_USER=...`
  - Or use a library like [dotenv-java](https://github.com/cdimascio/dotenv-java)

### Java version mismatch

- Match the Java version in Dockerfile and IDE (e.g., Java 21)

### Container crash or stale data

To clean up and rebuild everything:

```bash
docker-compose down -v
docker-compose up --build
```
---

## Contact & Contributions

Contributions are welcome! If you find bugs or have suggestions:

- Open an issue
- Submit a pull request
- Contact the maintainer for help or collaboration
