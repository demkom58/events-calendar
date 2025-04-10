CREATE TABLE events
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    start_date_time DATETIME     NOT NULL,
    end_date_time   DATETIME     NOT NULL,
    location        VARCHAR(255),
    CHECK ( start_date_time < end_date_time ),
    INDEX (id)
);
