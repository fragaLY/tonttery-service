CREATE SCHEMA IF NOT EXISTS lottery;

CREATE TABLE IF NOT EXISTS lottery.lottery
(
    id         UUID PRIMARY KEY,
    type       VARCHAR(7)  NOT NULL,
    status     VARCHAR(11) NOT NULL,
    start_date DATE        NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL,
    UNIQUE (type, start_date)
);