# Core Banking Mock Service

Core Banking Lite mock sở hữu domain `BankAccount`, lưu PostgreSQL và chạy tại port `8082`.

## API

- `POST /api/accounts`: tạo/replay account; yêu cầu header `Idempotency-Key` (tối đa 200 ký tự).
- `GET /api/accounts/{accountNumber}`: đọc account; không tồn tại trả `404 ACCOUNT_NOT_FOUND`.

Lần đầu với key/application mới tạo account và trả `201`. Replay cùng key và cùng `applicationId/customerId/productCode` trả `200` với đúng account đã có. Cùng key nhưng payload khác, hoặc cùng application với key khác, trả `409 IDEMPOTENCY_KEY_CONFLICT`.

Migration V2 thêm `idempotency_key` bằng partial unique index. Row legacy từ V1 có key null được bind nếu application/customer/product khớp. Database vẫn bảo vệ uniqueness của `application_id`, `account_number` và non-null `idempotency_key`.

## Chạy local

Tạo database `core_banking_mock`, copy `application-local.example.yml` thành `application-local.yml`, rồi đặt `CORE_BANKING_DB_URL`, `CORE_BANKING_DB_USERNAME`, `CORE_BANKING_DB_PASSWORD` trong terminal.

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Service chỉ mô phỏng idempotent account provisioning; không có balance, ledger, transaction hoặc retry engine.
