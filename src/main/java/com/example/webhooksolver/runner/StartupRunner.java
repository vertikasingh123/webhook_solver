package com.example.webhooksolver.runner;

import com.example.webhooksolver.model.GenerateWebhookRequest;
import com.example.webhooksolver.model.GenerateWebhookResponse;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.Map;

@Component
public class StartupRunner implements ApplicationRunner {

    @Autowired
    private RestTemplate restTemplate;

    private static final String GENERATE_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String TEST_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        GenerateWebhookRequest req = new GenerateWebhookRequest("John Doe", "REG12347", "john@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GenerateWebhookRequest> entity = new HttpEntity<>(req, headers);

        System.out.println("[startup] Sending generateWebhook request...");
        ResponseEntity<GenerateWebhookResponse> respEntity;
        try {
            respEntity = restTemplate.postForEntity(GENERATE_URL, entity, GenerateWebhookResponse.class);
        } catch (Exception ex) {
            System.err.println("[startup] Error when calling generateWebhook: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        if (respEntity.getStatusCode() != HttpStatus.OK || respEntity.getBody() == null) {
            System.err.println("[startup] generateWebhook returned non-OK or empty body: " + respEntity.getStatusCode());
            return;
        }

        GenerateWebhookResponse resp = respEntity.getBody();
        System.out.println("[startup] Received webhook: " + resp.getWebhook());
        System.out.println("[startup] Received accessToken: (hidden)");

        String regNo = req.getRegNo();
        int lastTwo = extractLastTwoDigits(regNo);
        boolean isEven = (lastTwo % 2 == 0);

        String finalQuery;
        if (isEven) {
            finalQuery =
                "SELECT\n" +
                "  e.emp_id,\n" +
                "  e.first_name,\n" +
                "  e.last_name,\n" +
                "  d.department_name,\n" +
                "  (\n" +
                "    SELECT COUNT(*)\n" +
                "    FROM employee e2\n" +
                "    WHERE e2.department = e.department\n" +
                "      AND e2.dob > e.dob\n" +
                "  ) AS younger_employees_count\n" +
                "FROM employee e\n" +
                "JOIN department d ON e.department = d.department_id\n" +
                "ORDER BY e.emp_id DESC;";
        } else {
            finalQuery = "/* Question 1: final SQL goes here. Replace with actual SQL for Question 1. */";
        }

        try {
            Path outDir = Paths.get("output");
            if (!Files.exists(outDir)) Files.createDirectories(outDir);
            String filename = "solution-" + regNo + "-" + Instant.now().toEpochMilli() + ".txt";
            Path outFile = outDir.resolve(filename);
            String contents = "regNo: " + regNo + "\nwebhook: " + resp.getWebhook() + "\n\nfinalQuery:\n" + finalQuery + "\n";
            Files.writeString(outFile, contents, StandardOpenOption.CREATE_NEW);
            System.out.println("[startup] Saved solution to " + outFile.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("[startup] Failed to save solution locally: " + e.getMessage());
        }

        try {
            HttpHeaders h2 = new HttpHeaders();
            h2.setContentType(MediaType.APPLICATION_JSON);
            h2.set("Authorization", resp.getAccessToken());

            Map<String, String> bodyMap = Map.of("finalQuery", finalQuery);
            HttpEntity<Map<String, String>> ent2 = new HttpEntity<>(bodyMap, h2);

            System.out.println("[startup] Submitting finalQuery to testWebhook...");
            ResponseEntity<String> postResp = restTemplate.exchange(TEST_WEBHOOK_URL, HttpMethod.POST, ent2, String.class);
            System.out.println("[startup] testWebhook response: " + postResp.getStatusCode() + " - " + postResp.getBody());
        } catch (Exception ex) {
            System.err.println("[startup] Error when submitting finalQuery to testWebhook: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private int extractLastTwoDigits(String regNo) {
        if (regNo == null) return 0;
        String digits = regNo.replaceAll("\\D+", "");
        if (digits.length() == 0) return 0;
        if (digits.length() == 1) return Integer.parseInt(digits);
        String lastTwo = digits.substring(digits.length() - 2);
        try {
            return Integer.parseInt(lastTwo);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
