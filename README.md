# Blockchain Transaction Indexer & API

## 📌 Project Overview

This project is a **Java-based backend service** that indexes blockchain data from the Ethereum network and exposes it through clean REST APIs. The system listens to new blocks, transactions, and smart contract events, stores them in a relational database, and allows clients to query on-chain data efficiently.

The project is designed to simulate how real Web3 backend systems (e.g. exchanges, explorers, analytics platforms) ingest and process blockchain data.

---

## 🎯 Key Objectives

* Understand how blockchain data flows into backend systems
* Build a reliable event-driven indexing service
* Design backend APIs for querying on-chain data
* Practice backend engineering concepts such as idempotency, retries, and data consistency

---

## 🧱 System Architecture

```
Ethereum Network (Sepolia)
        ↓
     web3j
        ↓
Spring Boot Indexer Service
        ↓
   PostgreSQL Database
        ↓
     REST APIs
```

---

## ⚙️ Tech Stack

* **Java 17**
* **Spring Boot**
* **web3j** (Ethereum client)
* **PostgreSQL**
* **Spring Data JPA / Hibernate**
* **Docker & Docker Compose**
* *(Optional)* Redis for caching

---

## 🚀 Core Features

### Blockchain Indexing

* Subscribe to new Ethereum blocks
* Index block metadata (block number, hash, timestamp)
* Index transactions (hash, from, to, value, gas, block number)
* Subscribe to and decode smart contract events (e.g. ERC-20 `Transfer`)

### Backend Reliability

* Idempotent database writes using unique constraints
* Basic chain re-organization handling (block hash comparison)
* Retry logic for failed block processing
* Graceful handling of RPC failures

### REST APIs

* `GET /transactions?address=0x...`

  * Fetch incoming and outgoing transactions for a wallet
* `GET /blocks/latest`

  * Retrieve the most recently indexed block
* `GET /contract/{address}/events`

  * Query indexed smart contract events

---

## 🗄️ Database Design (Simplified)

### Block

* blockNumber (unique)
* blockHash
* timestamp

### Transaction

* txHash (unique)
* fromAddress
* toAddress
* value
* gasUsed
* blockNumber

### ContractEvent

* contractAddress
* eventName
* fromAddress
* toAddress
* value
* txHash
* blockNumber

---

## 🛠️ Setup & Installation

### Prerequisites

* Java 17
* Docker & Docker Compose
* Ethereum RPC endpoint (Sepolia)

### Steps

1. Clone the repository
2. Configure `application.yml` with your RPC URL
3. Start PostgreSQL using Docker Compose
4. Run the Spring Boot application

```bash
docker-compose up -d
./mvnw spring-boot:run
```

---

## 🧪 Example API Usage

```http
GET /transactions?address=0x1234...
```

```http
GET /blocks/latest
```

```http
GET /contract/0xABC.../events
```

---

## 📈 What I Learned

* How Ethereum blockchain data is streamed and indexed
* Designing event-driven backend systems
* Handling unreliable external dependencies (RPC nodes)
* Structuring backend services for Web3 applications
* Building queryable APIs over on-chain data

---

## 🔮 Possible Improvements

* Token balance aggregation
* Event replay from a specific block height
* Multi-chain support
* Prometheus metrics and monitoring
* Unit and integration tests with mocked blockchain clients

---

## 📄 Resume Description

> Developed a Java-based blockchain indexing backend using Spring Boot and web3j that subscribes to Ethereum network events, stores on-chain data in PostgreSQL, and exposes REST APIs for transaction and contract event queries with idempotency and basic re-organization handling.

---

## 🧑‍💻 Author

Yoonseo Han
