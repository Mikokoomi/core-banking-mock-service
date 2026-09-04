CREATE TABLE bank_accounts (
    account_id UUID PRIMARY KEY,
    account_number VARCHAR(40) NOT NULL UNIQUE,
    application_id UUID NOT NULL UNIQUE,
    customer_id VARCHAR(100) NOT NULL,
    product_code VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    opened_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
