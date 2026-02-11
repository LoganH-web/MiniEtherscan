Day 1–2: Project Setup

Deliverables
- Spring Boot app runs
- PostgreSQL connected
- Docker-ready

Tasks
- Create Spring Boot project
- Add dependencies:
    web3j
    Spring Data JPA
    PostgreSQL
- Basic application.yml
- Dockerfile + docker-compose (Postgres)

Checkpoint
- GET /health returns OK

Day 3–4: Ethereum Connection (web3j)

Deliverables
- Connected to Sepolia RPC
- Can fetch latest block

Tasks
- Setup web3j client
- Fetch:
    - latest block number
    - gas price

- Log results on startup

Checkpoint
App logs:
- Connected to Sepolia. Latest block: XXXXX

Day 5–6: Block Listener
Deliverables
- Subscribed to new blocks

Tasks
- Use web3j block subscription
- On new block:
    - block number
    - timestamp
    - tx count

Checkpoint
- New blocks printed live

Day 7: Persist Blocks
Deliverables
- Blocks saved to DB

Tasks
- Create BlockEntity
- Save blocks with:
    - blockNumber (unique)
    - blockHash
    - timestamp

Backend Skill Shown
- Idempotent writes (ignore duplicates)

🔹 Week 2 — Transaction Indexing + Core APIs
🎯 Goal: Turn raw blockchain data into queryable backend APIs

Day 8–9: Transaction Indexing
Deliverables
- Transactions stored in DB

Tasks
- for each block
    - iterate transactions
    - store:
        - hash
        - from
        - to
        - value
        - gas
        - blockNumber

Important: transaction hash = unique constraint 

Day 10: REST API — Transactions
Deliverables: "GET /transactions?address=0x..."

Features:
- incoming OR outgoing txs
- Pagination

Backend Signal
- Query optimization
- Pagination design

Day 11: Latest Block API 
Deliverables: "GET /blocks/latest"

Returns:
- block number
- timestamp
- tx count

Day 12-13: error handling & retries
Deliverables: Stable ingestion 

Tasks:
- retry failed block processing
- graceful RPC failure handling

