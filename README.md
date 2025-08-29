# webhook-solver

Spring Boot app that on startup:
1. Calls the `generateWebhook` endpoint to get a webhook URL and accessToken.
2. Chooses the SQL (Question 1 or Question 2) based on last two digits of `regNo`.
3. Saves the final SQL locally.
4. Submits the final SQL to `testWebhook` endpoint with the `Authorization` header set to the accessToken.

## How to build

Requirements:
- Java 17+
- Maven

Build:
```bash
mvn clean package
```

Run:
```bash
java -jar target/webhook-solver-0.0.1-SNAPSHOT.jar
```

Notes:
- The project does NOT expose any HTTP controllers; the flow runs in `StartupRunner` on application startup.
- If your real regNo ends with odd last-two digits, replace the placeholder SQL for Question 1 in `StartupRunner`.
- The code stores solution files in the `output/` folder.

## Files included
- Full source under `src/main/java/...`
- `pom.xml`
- `README.md`

## What I produced
This ZIP contains the full project source and README. You must run `mvn clean package` locally to produce the JAR. If you want, I can also attempt to build the JAR here — but building requires Maven being available in the environment.
