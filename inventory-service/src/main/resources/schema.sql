DROP TABLE IF EXISTS `inventory`;
DROP TABLE IF EXISTS `order_event`;

CREATE TABLE `inventory` (
    id INTEGER NOT NULL AUTO_INCREMENT,
    product VARCHAR(256),
    amount INTEGER,
    price INTEGER,
    PRIMARY KEY (id)
);

CREATE TABLE `order_event` (
    order_id VARCHAR(256) NOT NULL,
    product VARCHAR(256),
    amount INTEGER,
    status VARCHAR(50),
    reason VARCHAR(256),
    date_time DATETIME,
    PRIMARY KEY (order_id, status)
);

INSERT INTO inventory (product, amount, price) VALUES ("Iphone", 200, 1000);