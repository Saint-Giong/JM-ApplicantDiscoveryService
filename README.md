# JM-ApplicantDiscoveryService

Talent discovery and matching service for Premium companies on the Job Manager platform.

## Overview

The Applicant Discovery Service is a **Premium-only feature** that allows companies to create search profiles to discover and match with potential candidates based on skills, location, employment preferences, salary expectations, and educational background.

## Features

- **Search Profile Management**: Create talent discovery profiles
- **Skill-Based Matching**: Match candidates by required skills
- **Employment Type Filtering**: Full-time, Part-time, Internship, Contract, Fresher
- **Location Targeting**: Country-based candidate search
- **Salary Range Filtering**: Min/max salary expectations
- **Education Level Filtering**: Bachelor, Master, Doctorate
- **Premium Access Control**: Only Premium subscribers can access
- **Multi-Criteria Search**: Combine multiple filters for precise matching

## Tech Stack

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**: Database persistence
- **PostgreSQL**: Search profiles database
- **Kafka**: Event-driven communication
- **BitSet**: Efficient employment type storage
- **Lombok**: Reduce boilerplate code

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL database
- Kafka broker
- JM-SkillTagService running
- JM-CompanySubscriptionService running

## Database Schema

### Table: `search_profile`

| Column            | Type      | Description                                |
| ----------------- | --------- | ------------------------------------------ |
| `profile_id`      | UUID (PK) | Search profile identifier (auto-generated) |
| `company_id`      | UUID      | Company ID (FK to Auth service)            |
| `salary_min`      | DECIMAL   | Minimum salary requirement                 |
| `salary_max`      | DECIMAL   | Maximum salary requirement                 |
| `highest_degree`  | ENUM      | BACHELOR, MASTER, DOCTORATE                |
| `employment_type` | BITSET    | Employment types (bit flags)               |
| `country`         | VARCHAR   | Target country for candidates              |

### Employment Type BitSet

```
Bit 0: FULL_TIME
Bit 1: PART_TIME
Bit 2: FRESHER
Bit 3: INTERNSHIP
Bit 4: CONTRACT
```

### Degree Type Enum

```
0: BACHELOR
1: MASTER
2: DOCTORATE
```

## Data Seeding

The service automatically seeds **2 search profiles for Premium companies only**:

### Premium Company 1: Netcompany - Software Engineering

```yaml
Company ID: 33333333-3333-3333-3333-333333333333
Profile Type: Software Engineer Talent Discovery

Search Criteria:
  Skills: [React (5), Spring Boot (8), Docker (12)]
  Employment Types: [FULL_TIME (bit 0), INTERNSHIP (bit 3)]
  Country: Vietnam
  Salary Min: > 800 USD
  Salary Max: Unlimited (null)
  Highest Degree: Any (null)

Target: Full-time and Intern Software Engineers in Vietnam
```

### Premium Company 2: Shopee - Data Engineering

```yaml
Company ID: 44444444-4444-4444-4444-444444444444
Profile Type: Data Engineer Talent Discovery

Search Criteria:
  Skills: [Python (2), AWS (14), Snowflake (10)]
  Employment Types: [CONTRACT (bit 4)]
  Country: Singapore
  Salary Min: > 1200 USD
  Salary Max: Unlimited (null)
  Highest Degree: Any (null)

Target: Contractual Data Engineers in Singapore
```

> **Note**: Only **Premium** companies have search profiles. Freemium companies (NAB, Google) do NOT have access to this feature.

## Premium Access Control

**Access Rules:**

- ✅ **Premium** companies: Full access to all discovery features
- ❌ **Freemium** companies: HTTP 403 Forbidden

## Search Profile Limits

| Subscription Tier | Max Search Profiles     |
| ----------------- | ----------------------- |
| Freemium          | 0 (No access)           |
| Premium           | 10 profiles per company |
