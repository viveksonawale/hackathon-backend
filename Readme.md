# Hackathon Management System — Backend Context

This document provides the context required for the AI agent to complete the backend implementation.

The project is **already partially implemented (≈20%)**.
The agent must **extend the existing codebase**, not recreate it.

---

# 1. Tech Stack

Backend Framework: Spring Boot  
Language: Java 21  
Build Tool: Maven

Dependencies already installed:

- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL Driver
- Lombok
- Validation
- JWT (jjwt)
- springdoc-openapi (Swagger)

---

# 2. Project Architecture

Root package:


com.hackathon


Folder structure:


config
controller
service
repository
model
dto
security
exception
util


Rules:

- Controllers → only handle HTTP layer
- Services → business logic
- Repositories → database access
- DTO → request/response models
- Entities → database models only
- Entities MUST NOT be returned directly in API responses

---

# 3. Database Configuration

Database: PostgreSQL

Connection:


DB_HOST=localhost
DB_PORT=5432
DB_NAME=hackathon_db
DB_USER=postgres
DB_PASSWORD=2005


Spring datasource example:


jdbc:postgresql://localhost:5432/hackathon_db


Hibernate:


spring.jpa.hibernate.ddl-auto=update


---

# 4. Database Schema

The system uses **7 tables only**.

## USERS


id
name
email
password
role (PARTICIPANT | ORGANIZER)
created_at


---

## HACKATHONS


id
title
description
prize_pool
start_date
end_date
registration_deadline
max_team_size
organizer_id
status
created_at


---

## PARTICIPANTS


id
user_id
hackathon_id
joined_at


---

## TEAMS


id
name
hackathon_id
leader_id
created_at


---

## TEAM_MEMBERS


id
team_id
user_id
role


---

## SUBMISSIONS


id
team_id
hackathon_id
project_name
description
github_url
demo_url
presentation_url
submitted_at
score


---

## INVITATIONS (optional)


id
team_id
email
status
created_at


---

# 5. Authentication System

Authentication must use **JWT**.

JWT configuration:


JWT_SECRET=C1t7czy4zbsVh3cw35+TR9jl/+IDZP22+iwmBJ2SJ+8=
JWT_EXPIRATION=86400000


Token expiration = **24 hours**

JWT payload must include:


userId
email
role


---

# 6. Password Security

Passwords must be encrypted using:


BCryptPasswordEncoder


Passwords must never be stored in plain text.

---

# 7. Authentication APIs

### Signup


POST /api/auth/signup


Request:


name
email
password
role


Response:


accessToken
user


---

### Login


POST /api/auth/login


Request:


email
password


Response:


accessToken
user


---

### Current User


GET /api/auth/me


Returns the authenticated user.

---

# 8. Hackathon APIs

### Create Hackathon


POST /api/hackathons


Access: ORGANIZER only

---

### Get All Hackathons


GET /api/hackathons


Supports query parameters:


search
status


---

### Get Hackathon Details


GET /api/hackathons/{id}


---

### Join Hackathon


POST /api/hackathons/{id}/join


Creates a record in `participants`.

---

# 9. Organizer APIs

### Organizer Dashboard


GET /api/organizer/dashboard


Response:


hackathonsCreated
participantsCount
submissionsCount


---

### Manage Hackathons


GET /api/organizer/hackathons
PUT /api/hackathons/{id}
DELETE /api/hackathons/{id}


---

# 10. Team APIs

### Create Team


POST /api/hackathons/{id}/teams


---

### Get My Team


GET /api/hackathons/{id}/team


---

### Invite Member


POST /api/teams/{id}/invite


---

### Leave Team


DELETE /api/teams/{id}/leave


---

# 11. Submission APIs

### Submit Project


POST /api/hackathons/{id}/submit


Body:


projectName
description
githubUrl
demoUrl
presentationUrl


---

### Get Submission


GET /api/hackathons/{id}/submission


---

# 12. Leaderboard API


GET /api/hackathons/{id}/leaderboard


Response fields:


rank
team
score


Results sorted by score.

---

# 13. Security Rules

Spring Security must enforce:


/api/auth/** → public
/api/hackathons → public GET
/api/hackathons/* → authenticated
/api/organizer/** → ORGANIZER only
/api/participant/** → PARTICIPANT only


JWT filter must authenticate requests.

---

# 14. API Response Format

All APIs must follow this format:


{
"success": true,
"data": {},
"message": "string"
}


---

# 15. Global Exception Handling

Use:


@RestControllerAdvice


Handle:


ResourceNotFoundException
UnauthorizedException
ValidationException


---

# 16. Testing Scenarios

The following flows must work:

1. Signup
2. Login
3. Create hackathon
4. Join hackathon
5. Create team
6. Submit project
7. View leaderboard

---

# 17. API Documentation

Swagger must be available at:


/swagger-ui.html


---

# 18. Important Development Rules

The AI agent must:

- NOT modify existing database schema
- NOT change package structure
- NOT expose entities directly
- ALWAYS use DTOs
- ALWAYS validate request bodies
- ALWAYS use service layer for business logic

---

# 19. Goal

The backend must support this complete user flow:

User signup/login  
↓  
Browse hackathons  
↓  
View hackathon details  
↓  
Join hackathon  
↓  
Create / manage team  
↓  
Submit project  
↓  
View leaderboard  
↓  
Organizer manage hackathon

Nothing more.