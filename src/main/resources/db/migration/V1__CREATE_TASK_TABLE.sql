CREATE TABLE task (
    id INT NOT NULL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    date DATE,
    time TIMESTAMP,
    place VARCHAR(255),
    tag VARCHAR(255),
    done BOOLEAN
);