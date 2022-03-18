DROP TABLE IF EXISTS invoice;
DROP TABLE IF EXISTS order_message;

CREATE TABLE invoice (
    ID VARCHAR(256) NOT NULL,
    ORDER_ID VARCHAR(256),
    PRODUCT VARCHAR(256),
    AMOUNT INTEGER,
    PRICE INT,
    TOTAL INT,
    USERNAME VARCHAR(256),
    ADDRESS VARCHAR(256),
    STATUS VARCHAR(48),
    PRIMARY KEY (ID)
);

CREATE TABLE order_message (
    ID VARCHAR(256) NOT NULL,
    ORDER_ID VARCHAR(256),
    PRODUCT VARCHAR(256),
    AMOUNT INTEGER,
    PRICE INTEGER,
    TOTAL INTEGER,
    USERNAME VARCHAR(256),
    ADDRESS VARCHAR(256),
    PRIMARY KEY (ID)
);