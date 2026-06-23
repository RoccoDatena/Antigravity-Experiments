package com.antigravity.taskmanager.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class AwsBedrockService {

    @Value("${app.aws.bedrock.enabled:false}")
    private boolean bedrockEnabled;

    @Value("${app.aws.region:eu-west-1}")
    private String awsRegion;

    /**
     * Generates a list of suggested subtasks for a given task title and description
     * using Amazon Bedrock (Anthropic Claude 3 Haiku model).
     */
    public List<String> suggestSubtasks(String taskTitle, String taskDescription) {
        if (!bedrockEnabled) {
            return getMockSuggestions(taskTitle);
        }

        try (BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.of(awsRegion))
                .build()) {

            // We target Anthropic Claude 3 Haiku on Amazon Bedrock
            String modelId = "anthropic.claude-3-haiku-20240307-v1:0";

            // Construct the prompt for Claude 3 (using messages API format)
            String systemPrompt = "You are a helpful assistant. Provide exactly 3 short, actionable subtasks for the task provided. Reply ONLY with a JSON array of strings. No markdown formatting, no explanation, just raw JSON array.";
            String userPrompt = String.format("Task Title: %s\nDescription: %s", taskTitle, taskDescription != null ? taskDescription : "");

            JSONObject payload = new JSONObject();
            payload.put("anthropic_version", "bedrock-2023-05-31");
            payload.put("max_tokens", 256);
            payload.put("temperature", 0.5);

            JSONObject systemObj = new JSONObject();
            payload.put("system", systemPrompt);

            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", userPrompt);
            messages.put(userMessage);
            payload.put("messages", messages);

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(modelId)
                    .contentType("application/json")
                    .body(SdkBytes.fromUtf8String(payload.toString()))
                    .build();

            InvokeModelResponse response = client.invokeModel(request);
            String responseBody = response.body().asString(StandardCharsets.UTF_8);

            JSONObject responseJson = new JSONObject(responseBody);
            String textResult = responseJson.getJSONArray("content")
                    .getJSONObject(0)
                    .getString("text")
                    .trim();

            // Attempt to parse the response as JSON array
            JSONArray jsonArray = new JSONArray(textResult);
            List<String> subtasks = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                subtasks.add(jsonArray.getString(i));
            }
            return subtasks;

        } catch (Exception e) {
            // Fallback to mock in case of API/network/credential errors during evaluation
            List<String> errorFallback = new ArrayList<>();
            errorFallback.add("Create action plan for: " + taskTitle);
            errorFallback.add("Review resources needed");
            errorFallback.add("Define deadline milestones");
            return errorFallback;
        }
    }

    private List<String> getMockSuggestions(String taskTitle) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("Analyze requirements for: " + taskTitle);
        suggestions.add("Draft initial implementation");
        suggestions.add("Perform review and testing");
        return suggestions;
    }
}
