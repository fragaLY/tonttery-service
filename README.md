[![Tonttery CI/CD](https://github.com/fragaLY/tonttery-service/actions/workflows/tonterry-service.yml/badge.svg?branch=main)](https://github.com/fragaLY/tonttery-service/actions/workflows/tonterry-service.yml)
# tonttery-service

## Browser compatibility

Google Chrome the latest version and previous.

## Performance

| Definition                          | Option |
| :---                                | :---:  |
| Average Page Load Time              | `500ms`|
| Max Page Load Time                  | `1s`   |
| Average Search Result Time          | `2s`   |
| Max Search Result Time              | `3s`   |
| Max Page Save Time                  | `500ms`|
| Caching	CDN                         | `95%`  |
| Backend Cache Hit Ratio             | `95%`  |
| Browser Cache - Cache Hit Ratio     | `95%`  |

## Testability

| Definition                          | Option |
| :---                                | :---:  |
| Minimized manual testing            | `yes`  |
| Unit test coverage                  | `~100%`|
| Integration test coverage           | `~100%`|

## Security

| Definition                          | Option |
| :---                                | :---:  |
| DAST scanning                       | `yes`  |
| SAST scanning                       | `yes`  |

## Integrity

| Definition                          | Option |
| :---                                | :---:  |
| Back up                             | `Production DB should be backed up at least once per day to prevent data loss.`  |
| Recovery plan                       | `Tested recovery procedures to ensure data integrity in case of system failures or disasters.`  |
| Audit log                           | `All inserts, modifications and deletes of data must be appropriately audited so that every change can be tracked to identify what the change was and who performed the change.`  |
| Version Control                     | `It should be possible to track any changes to code and configuration, maintaining a record of who made the changes and when.`  |
| Access Control                      | `Strict access controls and user permissions must be enforced to ensure that only authorized users can access and modify data and system functions.`  |
| Data Validation                     | `All user inputs must be rigorously validated to prevent the entry of erroneous or malicious data into the system.`  |

## Availability

| Definition                                    | Option |
| :---                                          | :---:  |
| High availability	Uptime                      | `99.9` |
| Health checks (liveness and readiness probes) | `yes` |

## Scalability

| Definition                          | Option                                     |
| :---                                | :---:                                      |
| Number of users in the nearest year | `1000`                                     |
| Number of users in 1-3 years        | `10000`                                    |
| Number of users in 2030             | 1% of Telegram Expected Users `15_000_000` |

##	Capacity

| Definition                                              | Target   | Supported |
| :---                                                    | :---:    | :---:     |
| Number of concurrent users                              | `1000`   | `TBD`     |
| Number of supported concurrent transactions in a second | `10_000` | `TBD`     |

## Data storage volume

Client (~ 469 bytes per record)

| Column           | Type        | Size (bytes) |
| :---             | :---        | :---:        |
|id                | UUID        |   `16`       |
|telegram_id       | BIGINT      |   `8`        |
|first_name        | VARCHAR(64) |   `65`       |
|last_name         | VARCHAR(64) |   `65`       |
|telegram_username | VARCHAR(32) |   `33`       |
|is_bot            | BOOLEAN     |   `1`        |
|is_premium        | BOOLEAN     |   `1`        |
|image             | VARCHAR(255)|   `256`      |
|authenticated_at  | TIMESTAMP   |   `8`        |
|created_at        | TIMESTAMP   |   `8`        |
|updated_at        | TIMESTAMP   |   `8`        |

Lottery (~ 72 bytes per record)

| Column           | Type        | Size (bytes) |
| :---             | :---        | :---:        |
|id                | UUID        |   `16`       |
|winner_id         | UUID        |   `16`       |
|type              | VARCHAR(7)  |   `8`        |
|status            | VARCHAR(11) |   `12`       |
|start_date        | DATE        |   `4`        |
|created_at        | TIMESTAMP   |   `8`        |
|updated_at        | TIMESTAMP   |   `8`        |

Client's Lotteries (~ 32 bytes per record)

| Column           | Type        | Size (bytes) |
| :---             | :---        | :---:        |
|client_id         | UUID        |   `16`       |
|lottery_id        | UUID        |   `16`       |

Total Volume Per Year At Max

| Entity           | Amount              | Total Size |
| :---             | :---                | :---:      |
|client            | ~15_000_000         |   `7GB`    |
|lottery           | ~500                |   `36KB`   |
|clients_lotteries | clients * lotteries |   `240GB`  |

In total `~250 GB` per year at max.
The data will be archived once per year.

## Bandwidth, Latency, and Throughput

Typical latencies:
- Reading 1 MB from RAM: 0.25 ms
- Reading 1 MB from SSD: 1 ms
- Transfer 1 MB over network: 10 ms
- Reading 1 MB from HDD: 20 ms
- Inter-continental round trip: 150 ms

Taking in advance the targeted transactions per second that should be handled ~10_000, we can calculate the latency by default in non-cached request at max
? ms.

TODO DEFINE THE NETWORK COSTS DEFINITION.

- [SECURITY-POLICY](/SECURITY.md)
- [LICENSE](/LICENSE.md)

Copyright © 2023-2024 Vadzim Kavalkou. All rights reserved.