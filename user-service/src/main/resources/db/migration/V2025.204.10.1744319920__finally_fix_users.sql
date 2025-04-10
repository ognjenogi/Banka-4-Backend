CREATE TABLE users
(
    id              uuid         NOT NULL,
    first_name      VARCHAR(255) NOT NULL,
    last_name       VARCHAR(255) NOT NULL,
    date_of_birth   date         NOT NULL,
    gender          VARCHAR(255) NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
    email           VARCHAR(255) NOT NULL UNIQUE,
    phone           VARCHAR(255) NOT NULL,
    address         VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    enabled         BOOLEAN      NOT NULL,
    permission_bits BIGINT       NOT NULL,
    primary key (id)
);

INSERT INTO users (id, first_name, last_name, date_of_birth, gender, email, phone, address, password, enabled,
                   permission_bits)
SELECT id,
       first_name,
       last_name,
       date_of_birth,
       gender,
       email,
       phone,
       address,
       password,
       enabled,
       permission_bits
FROM employees
UNION ALL
SELECT id,
       first_name,
       last_name,
       date_of_birth,
       gender,
       email,
       phone,
       address,
       password,
       enabled,
       permission_bits
FROM clients;

ALTER TABLE employees
    DROP COLUMN first_name;
ALTER TABLE employees
    DROP COLUMN last_name;
ALTER TABLE employees
    DROP COLUMN date_of_birth;
ALTER TABLE employees
    DROP COLUMN gender;
ALTER TABLE employees
    DROP COLUMN email;
ALTER TABLE employees
    DROP COLUMN phone;
ALTER TABLE employees
    DROP COLUMN address;
ALTER TABLE employees
    DROP COLUMN password;
ALTER TABLE employees
    DROP COLUMN enabled;
ALTER TABLE employees
    DROP COLUMN permission_bits;


ALTER TABLE clients
    DROP COLUMN first_name;
ALTER TABLE clients
    DROP COLUMN last_name;
ALTER TABLE clients
    DROP COLUMN date_of_birth;
ALTER TABLE clients
    DROP COLUMN gender;
ALTER TABLE clients
    DROP COLUMN email;
ALTER TABLE clients
    DROP COLUMN phone;
ALTER TABLE clients
    DROP COLUMN address;
ALTER TABLE clients
    DROP COLUMN password;
ALTER TABLE clients
    DROP COLUMN enabled;
ALTER TABLE clients
    DROP COLUMN permission_bits;

ALTER TABLE employees
    ADD CONSTRAINT fk_employees_users
        FOREIGN KEY (id) REFERENCES users (id);

ALTER TABLE clients
    ADD CONSTRAINT fk_clients_users
        FOREIGN KEY (id) REFERENCES users (id);
