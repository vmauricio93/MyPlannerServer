CREATE TABLE user(
    id INT NOT NULL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    roles VARCHAR(50) NOT NULL,
    full_name VARCHAR(255)
);