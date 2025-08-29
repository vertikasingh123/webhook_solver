# 🚀 Webhook Solver (Spring Boot)

This is a **Spring Boot** application that:

1. On startup, sends a `POST` request to generate a webhook (with your `regNo`, name, and email).  
2. Receives a `webhook` URL + `accessToken`.  
3. Chooses the correct SQL query (Question 1 if regNo last 2 digits are odd, Question 2 if even).  
4. Saves the query locally in an `output/` folder.  
5. Submits the final SQL query to the `testWebhook` endpoint with `Authorization: <accessToken>`.

---

## 📂 Project Structure
pom.xml # Maven build file
README.md # This guide
/src/main/java/com/example/webhooksolver
├── WebhookSolverApplication.java # Spring Boot main class
├── config/RestConfig.java # RestTemplate bean
├── model/
│ ├── GenerateWebhookRequest.java # Request DTO
│ └── GenerateWebhookResponse.java# Response DTO
└── runner/StartupRunner.java # Main logic on startup
/src/main/resources/
└── application.properties # Config (logging)


---

## 🛠️ Build & Run Locally

**Requirements:**
- Java 17+
- Maven 3.8+

**Build:**
```bash
mvn clean package

java -jar target/webhook-solver-0.0.1-SNAPSHOT.jar

🤖 GitHub Actions (Automatic Build)

This repo includes GitHub Actions (.github/workflows/maven.yml) which:

Builds the project with Maven

Produces the JAR target/webhook-solver-0.0.1-SNAPSHOT.jar

Uploads it as an artifact

✅ How to Download the JAR (via Actions)

Go to Actions → latest workflow run

Scroll to Artifacts

Download webhook-solver-jar (a ZIP)

Inside the ZIP:
webhook-solver-0.0.1-SNAPSHOT.jar

🌍 Public Download Link

For evaluators, the JAR is also published in Releases (as a ZIP).

