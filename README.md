# online-course-platform-api
Secure REST API for online course platform with JWT authentication and role-based access control

# 🎓 Online Course Platform API - Status Checklist

## 🛠 Verplichte Onderdelen (Security & Core)
- [x] [cite_start]**BCrypt Password Hashing**: Wachtwoorden veilig opgeslagen[cite: 126].
- [x] [cite_start]**JWT Generation**: Inclusief `sub` (username) en `role` claims[cite: 127].
- [x] [cite_start]**JwtAuthFilter**: Verwerkt de `Authorization: Bearer <token>` header[cite: 128, 161].
- [x] [cite_start]**Stateless Session**: Geen HTTP sessies, puur JWT[cite: 129].
- [x] [cite_start]**Autorisatiematrix**: Endpoints beveiligd op basis van ADMIN, INSTRUCTOR, STUDENT[cite: 20, 131].
- [x] [cite_start]**CORS**: Geconfigureerd voor frontend toegang (poort 5173)[cite: 130].
- [x] [cite_start]**Global Exception Handling**: Nette JSON errors voor 404, 403 en duplicates[cite: 133, 138].
- [x] [cite_start]**DataLoader (Bootstrap)**: Automatische database seeding bij opstart[cite: 139, 141].

## 📊 Database & Modellen
- [x] [cite_start]**AuditModel**: Alle entiteiten hebben `createdAt` en `updatedAt`[cite: 18, 63].
- [x] [cite_start]**Relaties**: Correcte JPA mapping tussen User, Course en Enrollment[cite: 17, 168].
- [x] [cite_start]**MySQL**: Volledig functionele database integratie[cite: 150, 172].

## 🚀 Excellente Score (Extra's)
- [x] [cite_start]**Pagination & Sorting**: Voor het ophalen van cursussen.
- [x] [cite_start]**Swagger/OpenAPI**: Interactieve API documentatie.
- [x] [cite_start]**Unit Tests**: Testen van de business logica in de Services.
- [ ] [cite_start]**Refresh Tokens**: Mechanisme om JWT's te vernieuwen.

## 📁 Oplevering
- [x] [cite_start]Postman Collectie Export[cite: 156, 170].
- [x] [cite_start]SQL Dump van de database[cite: 162, 172].