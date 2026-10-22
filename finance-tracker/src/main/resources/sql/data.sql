-- Roles
MERGE INTO roles (name) KEY(name) VALUES ('ROLE_USER');
MERGE INTO roles (name) KEY(name) VALUES ('ROLE_ADMIN');

-- Demo user: password = "password" (BCrypt encoded)
MERGE INTO users (username, email, password, full_name, enabled)
KEY(username)
VALUES ('demo', 'demo@example.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'Demo User', TRUE);

-- Assign ROLE_USER to demo
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'demo' AND r.name = 'ROLE_USER'
  AND NOT EXISTS (SELECT 1 FROM user_roles ur
                  JOIN users u2 ON ur.user_id = u2.id
                  JOIN roles r2 ON ur.role_id = r2.id
                  WHERE u2.username = 'demo' AND r2.name = 'ROLE_USER');

-- Default categories (global, user_id = NULL) - use INSERT if not exists via WHERE NOT EXISTS
INSERT INTO categories (name, type, icon) SELECT 'Salary',        'INCOME',  'bi-briefcase'  WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Salary'        AND user_id IS NULL);
INSERT INTO categories (name, type, icon) SELECT 'Freelance',     'INCOME',  'bi-laptop'     WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Freelance'     AND user_id IS NULL);
INSERT INTO categories (name, type, icon) SELECT 'Investments',   'INCOME',  'bi-graph-up'   WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Investments'   AND user_id IS NULL);
INSERT INTO categories (name, type, icon) SELECT 'Other Income',  'INCOME',  'bi-plus-circle' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Other Income'  AND user_id IS NULL);
INSERT INTO categories (name, type, icon) SELECT 'Food',          'EXPENSE', 'bi-egg-fried'  WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Food'          AND user_id IS NULL);
INSERT INTO categories (name, type, icon) SELECT 'Transport',     'EXPENSE', 'bi-car-front'  WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Transport'     AND user_id IS NULL);
INSERT INTO categories (name, type, icon) SELECT 'Housing',       'EXPENSE', 'bi-house'      WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Housing'       AND user_id IS NULL);
INSERT INTO categories (name, type, icon) SELECT 'Utilities',     'EXPENSE', 'bi-lightbulb'  WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Utilities'     AND user_id IS NULL);
INSERT INTO categories (name, type, icon) SELECT 'Health',        'EXPENSE', 'bi-heart-pulse' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Health'        AND user_id IS NULL);
INSERT INTO categories (name, type, icon) SELECT 'Entertainment', 'EXPENSE', 'bi-controller' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Entertainment' AND user_id IS NULL);
INSERT INTO categories (name, type, icon) SELECT 'Shopping',      'EXPENSE', 'bi-bag'        WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Shopping'      AND user_id IS NULL);
INSERT INTO categories (name, type, icon) SELECT 'Other Expense', 'EXPENSE', 'bi-three-dots' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Other Expense' AND user_id IS NULL);

-- Sample transactions for demo user
INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date)
SELECT u.id, c.id, 3500.00, 'INCOME', 'Monthly salary', '2024-05-01'
FROM users u JOIN categories c ON c.name = 'Salary' AND c.user_id IS NULL
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM transactions t2 WHERE t2.description = 'Monthly salary' AND t2.transaction_date = '2024-05-01');

INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date)
SELECT u.id, c.id, 750.00, 'INCOME', 'Web project payment', '2024-05-10'
FROM users u JOIN categories c ON c.name = 'Freelance' AND c.user_id IS NULL
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM transactions t2 WHERE t2.description = 'Web project payment');

INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date)
SELECT u.id, c.id, 1200.00, 'EXPENSE', 'Rent May', '2024-05-02'
FROM users u JOIN categories c ON c.name = 'Housing' AND c.user_id IS NULL
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM transactions t2 WHERE t2.description = 'Rent May');

INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date)
SELECT u.id, c.id, 320.00, 'EXPENSE', 'Groceries May', '2024-05-05'
FROM users u JOIN categories c ON c.name = 'Food' AND c.user_id IS NULL
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM transactions t2 WHERE t2.description = 'Groceries May');

INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date)
SELECT u.id, c.id, 85.00, 'EXPENSE', 'Monthly transit pass', '2024-05-03'
FROM users u JOIN categories c ON c.name = 'Transport' AND c.user_id IS NULL
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM transactions t2 WHERE t2.description = 'Monthly transit pass');

INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date)
SELECT u.id, c.id, 150.00, 'EXPENSE', 'Electricity and gas', '2024-05-06'
FROM users u JOIN categories c ON c.name = 'Utilities' AND c.user_id IS NULL
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM transactions t2 WHERE t2.description = 'Electricity and gas');

INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date)
SELECT u.id, c.id, 60.00, 'EXPENSE', 'Netflix and Spotify', '2024-05-07'
FROM users u JOIN categories c ON c.name = 'Entertainment' AND c.user_id IS NULL
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM transactions t2 WHERE t2.description = 'Netflix and Spotify');

INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date)
SELECT u.id, c.id, 3500.00, 'INCOME', 'Monthly salary June', '2024-06-01'
FROM users u JOIN categories c ON c.name = 'Salary' AND c.user_id IS NULL
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM transactions t2 WHERE t2.description = 'Monthly salary June');

INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date)
SELECT u.id, c.id, 200.00, 'INCOME', 'Dividend', '2024-06-15'
FROM users u JOIN categories c ON c.name = 'Investments' AND c.user_id IS NULL
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM transactions t2 WHERE t2.description = 'Dividend');

INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date)
SELECT u.id, c.id, 1200.00, 'EXPENSE', 'Rent June', '2024-06-02'
FROM users u JOIN categories c ON c.name = 'Housing' AND c.user_id IS NULL
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM transactions t2 WHERE t2.description = 'Rent June');

INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date)
SELECT u.id, c.id, 290.00, 'EXPENSE', 'Groceries June', '2024-06-10'
FROM users u JOIN categories c ON c.name = 'Food' AND c.user_id IS NULL
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM transactions t2 WHERE t2.description = 'Groceries June');

INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date)
SELECT u.id, c.id, 430.00, 'EXPENSE', 'Clothes and shoes', '2024-06-18'
FROM users u JOIN categories c ON c.name = 'Shopping' AND c.user_id IS NULL
WHERE u.username = 'demo'
  AND NOT EXISTS (SELECT 1 FROM transactions t2 WHERE t2.description = 'Clothes and shoes');
