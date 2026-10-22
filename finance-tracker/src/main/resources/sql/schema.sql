-- Users table
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enabled    BOOLEAN DEFAULT TRUE
);

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- User-Role join
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Categories
CREATE TABLE IF NOT EXISTS categories (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    type    VARCHAR(10)  NOT NULL, -- INCOME or EXPENSE
    icon    VARCHAR(50),
    user_id BIGINT,
    CONSTRAINT uq_category_name_user UNIQUE (name, user_id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Transactions
CREATE TABLE IF NOT EXISTS transactions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    category_id     BIGINT       NOT NULL,
    amount          DECIMAL(15,2) NOT NULL,
    type            VARCHAR(10)   NOT NULL, -- INCOME or EXPENSE
    description     VARCHAR(255),
    transaction_date DATE         NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)     REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
