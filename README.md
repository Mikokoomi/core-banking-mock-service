# Core Banking Mock Service

Core Banking Lite mock sở hữu domain `BankAccount`, lưu PostgreSQL và chạy tại port `8082`.

## API

- `POST /api/accounts`: tạo account; duplicate `applicationId` trả `409 ACCOUNT_ALREADY_EXISTS_FOR_APPLICATION`.
- `GET /api/accounts/{accountNumber}`: đọc account; không tồn tại trả `404 ACCOUNT_NOT_FOUND`.

## Chạy local

Tạo database `core_banking_mock`, copy `application-local.example.yml` thành `application-local.yml`, rồi đặt `CORE_BANKING_DB_URL`, `CORE_BANKING_DB_USERNAME`, `CORE_BANKING_DB_PASSWORD` trong terminal.

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Service chỉ mô phỏng account provisioning; không có balance, ledger, transaction, retry hoặc idempotency.
