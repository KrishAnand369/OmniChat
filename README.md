# 🚀 OmniChat (SaaS Chat Engine)

**OmniChat** is a production-ready, multi-tenant chat infrastructure designed to be integrated into any application via configuration. It acts as a "Black Box" messaging backend, handling real-time delivery, persistence, and scaling so host applications don't have to.



## 🌟 Key Features

* **🏢 Multi-Tenancy:** Strict data isolation using `tenant_id` discriminators. Tenant A's data is invisible to Tenant B.
* **⚡ Real-Time Messaging:** High-performance WebSocket implementation using **STOMP** over **RabbitMQ** (External Broker Relay) for clustering support.
* **🛡️ Robust Security:** HMAC-SHA256 signature verification for stateless authentication.
* **💾 Polyglot Persistence (Dual-Write):**
    * **PostgreSQL:** Source of Truth for metadata, users, and tenancy rules.
    * **MongoDB:** High-throughput storage for chat logs (JSON payload).
    * **Self-Healing:** Implements a "Janitor" process to reconcile consistency between SQL and NoSQL stores.
* **📈 Scalable:** Stateless architecture using **Redis** for session management and presence tracking.

## 🛠️ Tech Stack

| Component | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Java | 25 (LTS) | Core Logic |
| **Framework** | Spring Boot | 4.x | Web, Security, Data |
| **Messaging** | RabbitMQ | 3.12+ | Message Broker & STOMP Relay |
| **Primary DB** | PostgreSQL | 15+ | Relational Data (ACID) |
| **Log DB** | MongoDB | 6.0+ | Chat History & Payloads |
| **Cache** | Redis | 7.0+ | Distributed Sessions |
| **Build** | Maven | 3.9+ | Dependency Management |
| **Deploy** | Docker | Multi-Stage | Containerization |

## 🏗️ Architecture Highlights

### The "Dual-Write" Consistency Pattern
To ensure data safety without sacrificing performance, OmniChat uses a 3-step state machine for saving messages:
1.  **Promise:** Transactional save to Postgres (Status: `PENDING`).
2.  **Write:** Async save to MongoDB.
3.  **Commit:** Update Postgres status to `SYNCED`.
*Background workers retry any stuck `PENDING` messages.*

### Security Model
* **No Passwords:** We trust the Host Application's authentication.
* **Handshake:** Connection requires `X-Tenant-ID` and a valid `X-Auth-Signature` signed by the Tenant's private `API_SECRET`.

## 🚀 Getting Started (Local Dev)

### Prerequisites
* Java 25+
* Docker & Docker Compose

### 1. Start Infrastructure
OmniChat uses Docker Compose to spin up the required databases and brokers.
```bash 
docker-compose up -d
```
### 2. Run the Application
```bash
# Using the 'dev' profile for local debugging
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. Verify Connection
The WebSocket endpoint will be available at: 
```bash 
ws://localhost:8080/ws-chat
```

## 📂 Project Structure
```bash
src/main/java/com/krish/chatApp/
├── config/       # WebSocket, RabbitMQ, & Security Configs
├── controller/   # STOMP & REST Endpoints
├── model/        # JPA Entities & Mongo Documents
├── repository/   # Data Access Layers
├── service/      # Business Logic (Dual-Write, Validation)
└── util/         # HMAC Signers & Parsers
```

