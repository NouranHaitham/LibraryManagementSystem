# 📚 Library Management Console App

This is a **Java-based console application** for managing a library system. It allows **Admins** to manage books and users, and **Regular Users** to log in, borrow, and return books. The app uses a **MySQL database** to persist all data and leverages **Docker** and **Docker Compose** for containerized deployment.

## ✨ Features

* Console-based interface for Admins and Regular Users
* Persistent data storage using MySQL
* Data serialization with Java Object Streams
* Adminer UI for easy database inspection and querying
* Environment-based configuration for secure credentials

---

## 📦 Prerequisites

Make sure the following are installed:

* [Docker](https://docs.docker.com/get-docker/)
* [Docker Compose](https://docs.docker.com/compose/install/)

---

## 📂 Project Structure

```
📁 library-management-app/
👤📁 src/                   # Java source files (Main, models, services)
👤📁 db/                    # SQL scripts for schema (optional)
📄 Dockerfile             # Builds the Java app image
📄 docker-compose.yml     # Orchestrates app and database services
📄 .env                   # Environment variables (not checked into Git)
📄 README.md              # Project documentation (this file)
```

---

## 🚀 Getting Started

### 1. Clone the repo

```bash
git clone https://github.com/your-username/library-management-app.git
cd library-management-app
```

### 2. Create a `.env` file

Create a file named `.env` in the **project root** (same location as `docker-compose.yml`) with the following content:

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

> 🔐 These variables are used by the Java app via `System.getenv()` and passed through Docker Compose.

---

### 3. Build and Run the App

```bash
docker-compose up --build
```

* The script will wait until the database is ready before starting the Java app.
* The application will run as a console app inside the Docker container.
* You’ll be prompted to login or register as Admin/Regular User.

---

## 🌐 Access Adminer UI

You can view and manage your MySQL database using [Adminer](https://www.adminer.org/):

* Visit: [http://localhost:8080](http://localhost:8080)
* Use the following credentials:

| Field    | Value                |
| -------- | -------------------- |
| System   | MySQL                |
| Server   | `db`                 |
| Username | `root`               |
| Password | your\_root\_password |
| Database | `library`            |

---

## ⚙️ Environment Variables Summary

| Variable              | Description                         |
| --------------------- | ----------------------------------- |
| `MYSQL_ROOT_PASSWORD` | Root password for MySQL container   |
| `MYSQL_DATABASE`      | Initial DB name (`library`)         |
| `DB_HOST`             | Hostname of DB container (`db`)     |
| `DB_PORT`             | Port used by MySQL (default: 3306)  |
| `DB_NAME`             | Name of the DB (`library`)          |
| `DB_USER`             | Username for DB connection (`root`) |
| `DB_PASSWORD`         | Password for DB user                |

These variables are consumed by your Java application using:

```java
System.getenv("DB_USER");
System.getenv("DB_PASSWORD");
...
```

> ✅ Make sure `.env` is outside the `src/` folder and next to `docker-compose.yml`.

---

## 🧹 Troubleshooting

### ❌ Communications link failure

* **Cause**: App can't connect to DB.
* **Fix**:

  * Make sure `DB_HOST=db` (not `localhost`).
  * Use the correct username/password.
  * Ensure DB container is fully running.

### ⚠️ Environment variables are `null`

* `.env` must be placed **in the project root**.
* If running outside Docker (e.g. from IDE), `.env` won’t load automatically. You must:

  * Manually set `-D` JVM arguments, or
  * Use a library like [dotenv-java](https://github.com/cdimascio/dotenv-java) to load `.env`.

### 🐳 Docker container errors

* Rebuild from scratch:

  ```bash
  docker-compose down -v
  docker-compose up --build
  ```

### ❗ Java version mismatch

If you see `UnsupportedClassVersionError`, make sure:

* Your Docker image uses the same Java version used to compile `.class` files.
* Match versions in Dockerfile and your local compiler (e.g., Java 21 vs Java 17).

---

## ✅ Optional Notes

* **No need to install netcat**: We use a wait-for-it script or Java socket polling to ensure DB readiness.
* **No hardcoded DB settings**: Everything is configured via `.env` for flexibility and security.
* **Data is persistent** across container restarts using Docker volume (configured in `docker-compose.yml`).

---

## 💬 Contact / Contributions

Feel free to submit issues or PRs. For questions, reach out to the project maintainer.

---

## 📝 License

MIT License. Feel free to use and modify this project.

---

Happy coding! 🚀
