/**
 * Copyright © 2023-2024 Vadzim Kavalkou. All Rights Reserved. All information contained herein is,
 * and remains the property of Vadzim Kavalkou and/or its suppliers and is protected by
 * international intellectual property law. Dissemination of this information or reproduction of
 * this material is strictly forbidden, unless prior written permission is obtained from Vadzim
 * Kavalkou.
 **/

CREATE SCHEMA IF NOT EXISTS tonttery;
SET SEARCH_PATH TO tonttery;

CREATE TABLE IF NOT EXISTS client
(
    id                UUID        NOT NULL,
    telegram_id       BIGINT      NOT NULL,
    first_name        VARCHAR(64) NOT NULL,
    last_name         VARCHAR(64) NOT NULL,
    telegram_username VARCHAR(32) NOT NULL,
    is_bot            BOOLEAN     NOT NULL,
    is_premium        BOOLEAN     NOT NULL,
    image             VARCHAR     NOT NULL,
    authenticated_at  TIMESTAMP   NOT NULL,
    created_at        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP   NOT NULL,
    CONSTRAINT client_pk PRIMARY KEY (id, telegram_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS telegram_username_unique_index
    ON client USING GIN (telegram_username);

CREATE TABLE IF NOT EXISTS lottery
(
    id         UUID PRIMARY KEY,
    winner_id  UUID REFERENCES client (id) DEFAULT NULL,
    type       VARCHAR(7)  NOT NULL,
    status     VARCHAR(11) NOT NULL,
    start_date DATE        NOT NULL,
    created_at TIMESTAMP   NOT NULL        DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL,
    UNIQUE (type, start_date)
);

CREATE TABLE IF NOT EXISTS client_lottery
(
    client_id  UUID REFERENCES client (id),
    lottery_id UUID REFERENCES lottery (id),
    CONSTRAINT client_lottery_pk PRIMARY KEY (client_id, lottery_id)
);