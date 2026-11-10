# Architecture Decision Records

Индекс архитектурных решений проекта **Bank Account — Hexagonal Service**.

| № | Название | Статус |
|---|----------|--------|
| [0001](0001-hexagonal-architecture.md) | Гексагональная архитектура (Ports & Adapters) | ✅ Accepted |
| [0002](0002-persistence-jpa-postgresql.md) | Персистентность: Spring Data JPA + PostgreSQL | ✅ Accepted |
| [0003](0003-domain-events-kafka.md) | Публикация доменных событий в Kafka | ✅ Accepted |
| [0004](0004-transactional-outbox.md) | Transactional Outbox для надёжной публикации событий | 🔶 Proposed |

## Формат

Каждый ADR описывает одно решение в формате:

- **Контекст** — какая проблема решается и какие альтернативы рассматривались;
- **Решение** — что выбрано и почему;
- **Последствия** — плюсы и минусы принятого решения.

Статусы: `Proposed` → `Accepted` → `Deprecated` / `Superseded`.
