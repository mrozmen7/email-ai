#  Email-AI: AI-Powered Email Responder for UBS Bank

##  About the Project
**Email-AI** is an AI-powered **automated email response system** designed to simplify customer communication in the banking sector.  
This project has been customized for **UBS Bank**, generating **professional replies based on tone, length, and predefined templates**.

###  Purpose
- Reduce the workload of customer representatives
- Improve customer satisfaction
- Ensure consistent and professional communication

---

##  Features
-  **AI-powered email response generation**
-  **Tone selection** (Formal, Friendly, etc.)
-  **Length adjustment** (Short, Medium, Long)
-  **Template support** (Security Alert, Payment Reminder, Meeting Invite, Dinner Invite)
-  **UBS Bank corporate signature** is automatically appended at the end of all responses
-  **Swagger UI** for easy API testing
-  **Docker integration** for one-command deployment
-  **GitHub Actions (CI/CD)** for automated build and test on every push

---

##  Tech Stack
- **Java 17**
- **Spring Boot 3**
- **Spring WebFlux** – Asynchronous HTTP client
- **OpenAI GPT-4o-mini** – AI model for natural language generation
- **Swagger / OpenAPI** – API documentation
- **Docker** – Containerization
- **GitHub Actions** – CI/CD pipeline
- **Maven** – Build and dependency management

---

##  Project Structure
```bash
email-ai/
 ├── src/main/java/com/emailai
 │    ├── controller/        # API endpoints
 │    ├── service/           # Business logic (OpenAI integration)
 │    ├── dto/               # Request/Response objects
 │    └── EmailAiApplication.java
 ├── src/main/resources
 │    └── application.yml
 ├── .env                    # API key (OpenAI)
 ├── Dockerfile
 ├── docker-compose.yml      # (optional)
 └── README.md 

```
##  Testing the API with Swagger

After starting the application, you can test the API using **Swagger UI**:

👉 Open in your browser:  
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

###  Start the Application with `.env`
Make sure your **OpenAI API Key** is loaded before starting:

```bash
export $(cat .env | xargs)
./mvnw spring-boot:run