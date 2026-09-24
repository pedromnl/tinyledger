# Tiny Ledger

A lightweight REST API for creating accounts, recording deposits and withdrawals, checking balances, and viewing transaction history.

## Quick start

### Prerequisites

- Java 25 (download at https://www.oracle.com/java/technologies/javase/jdk25-archive-downloads.html)
- Maven wrapper included in the project (`mvnw` / `mvnw.cmd`)

### Run locally

From the project root:

```bash
./mvnw spring-boot:run
```

The API defaults to `http://localhost:8080`.

## OpenAPI / Swagger

Once the app is running, OpenAPI documentation is available on:

- `http://localhost:8080/docs`
- `http://localhost:8080/swagger-ui/index.html`

## Features

- Create a new account
- View all accounts
- View a single account by ID
- View current balance for an account
- Record a deposit
- Record a withdrawal
- View transaction history for an account
- In-memory data storage only (no database required)

## Available endpoints

### 1) List all accounts

```http
GET /api/v1/accounts
```

### 2) Create an account

```http
POST /api/v1/accounts
Content-Type: application/json
```

Request body:

```json
{
  "accountName": "Checking Account"
}
```

### 3) Get an account by id

```http
GET /api/v1/accounts/{accountId}
```

### 4) Get balance

```http
GET /api/v1/accounts/{accountId}/balance
```

### 5) Get transaction history

```http
GET /api/v1/accounts/{accountId}/transactions
```

### 6) Deposit funds

```http
POST /api/v1/accounts/{accountId}/transactions/deposit
Content-Type: application/json
```

Request body:

```json
{
  "amount": 150.25
}
```

### 7) Withdraw funds

```http
POST /api/v1/accounts/{accountId}/transactions/withdrawal
Content-Type: application/json
```

Request body:

```json
{
  "amount": 50.00
}
```

## Example usage

### Create an account

```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{"accountName":"Checking Account"}'
```

Example response:

```json
{
  "accountId": "550e8400-e29b-41d4-a716-446655440000",
  "accountName": "Checking Account",
  "createdAt": "2026-09-24T12:00:00Z"
}
```

### View all accounts

```bash
curl http://localhost:8080/api/v1/accounts
```

### View balance

```bash
curl http://localhost:8080/api/v1/accounts/550e8400-e29b-41d4-a716-446655440000/balance
```

### Deposit money

```bash
curl -X POST http://localhost:8080/api/v1/accounts/550e8400-e29b-41d4-a716-446655440000/transactions/deposit \
  -H "Content-Type: application/json" \
  -d '{"amount":150.25}'
```

### Withdraw money

```bash
curl -X POST http://localhost:8080/api/v1/accounts/550e8400-e29b-41d4-a716-446655440000/transactions/withdrawal \
  -H "Content-Type: application/json" \
  -d '{"amount":50.00}'
```

### View transaction history

```bash
curl http://localhost:8080/api/v1/accounts/550e8400-e29b-41d4-a716-446655440000/transactions
```


## Assumptions

- Accounts are kept in memory while the app is running.
- There is no authentication or authorization layer.
- Account names must have at least three characters.
- Amounts must be positive numbers and withdrawals cannot exceed the available balance.
- Accounts start with a balance of zero.

## Design decisions

- Deposit and withdrawal are modeled as separate API endpoints because they are distinct business operations with different validation rules and error handling.
- Balance is stored directly on the `Account` domain model so the current balance can be returned without scanning all transactions for every balance lookup.
  - Downside: the balance becomes a second source of truth in addition to transaction history, so it must be kept consistent with every deposit and withdrawal, making the system more prone to inconsistency and bugs.
- The `getBalance`, `deposit`, and `withdraw` operations are synchronized to avoid race conditions when multiple threads update the same account concurrently.
- Transaction history is retained on the account to support auditability and historical queries.

## Testing strategy

- A comprehensive integration test suite covers the end-to-end API flow, including successful operations and failure scenarios.
- A focused unit test suite validates the `Account` domain model, especially critical behaviors like deposit, withdrawal, insufficient funds, and positive-amount validation.


To run unit tests: 
```bash
./mvnw test
```
To run integration tests:
```bash
./mvnw integration-test
```