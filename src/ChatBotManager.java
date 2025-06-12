import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatBotManager {
    private final String apiUrl;
    private final HttpClient client = HttpClient.newHttpClient();

    public ChatBotManager(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String createPost(String title, String content, String author) throws Exception {
        String json = String.format("{\"title\": \"%s\", \"content\": \"%s\", \"author\": \"%s\"}",
                escapeJson(title),
                escapeJson(content),
                author != null ? escapeJson(author) : "Anonymous");

        String createUrl = apiUrl + "?api=blogs";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(createUrl))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return formatCreateResponse(response);
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public String viewAllPosts() throws Exception {
        String viewUrl = apiUrl + "?api=blogs";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(viewUrl))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return formatPostsResponse(response);
    }

    public String viewStatistics() throws Exception {
        String statsUrl = apiUrl + "?api=stats";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(statsUrl))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return formatStatsResponse(response);
    }

    private String formatCreateResponse(HttpResponse<String> response) {
        // Accept all 2xx success codes instead of just 200 and 201
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return "❌ Error: HTTP " + response.statusCode() + " - " + response.body();
        }

        String body = response.body();
        if (body == null || body.isEmpty()) {
            return "❌ Error: Empty response from server";
        }

        if (body.contains("\"success\": true") || body.contains("\"success\":true")) {
            String title = extractJsonValue(body, "title");
            String author = extractJsonValue(body, "author");
            String id = extractJsonValue(body, "id");
            String createdAt = extractJsonValue(body, "created_at");

            return String.format("✅ Blog post created successfully!\n" +
                            "📝 Title: %s\n" +
                            "👤 Author: %s\n" +
                            "🆔 ID: %s\n" +
                            "📅 Created: %s",
                    title != null ? title : "N/A",
                    author != null ? author : "N/A",
                    id != null ? id : "N/A",
                    createdAt != null ? createdAt : "N/A");
        } else {
            return "❌ Failed to create post: " + body;
        }
    }

    private String formatPostsResponse(HttpResponse<String> response) {
        // Accept all 2xx success codes instead of just 200
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return "❌ Error: HTTP " + response.statusCode() + " - " + response.body();
        }

        String body = response.body();
        if (body == null || body.isEmpty()) {
            return "❌ Error: Empty response from server";
        }

        if (!body.contains("\"success\": true") && !body.contains("\"success\":true")) {
            return "❌ Failed to retrieve posts: " + body;
        }

        // Extract total count
        String total = extractJsonValue(body, "total");
        String limit = extractJsonValue(body, "limit");

        StringBuilder result = new StringBuilder();
        result.append(String.format("📊 Total Posts: %s (Limit: %s)\n",
                total != null ? total : "N/A",
                limit != null ? limit : "N/A"));
        result.append("═══════════════════════════════════════\n");

        if ("0".equals(total)) {
            result.append("📭 No blog posts found. Create your first post!");
            return result.toString();
        }

        // Parse individual posts
        Pattern postPattern = Pattern.compile("\\{\\s*\"title\":\\s*\"([^\"]*)\",\\s*\"content\":\\s*\"([^\"]*)\",\\s*\"author\":\\s*\"([^\"]*)\",\\s*\"created_at\":\\s*\"([^\"]*)\",\\s*\"updated_at\":\\s*\"([^\"]*)\",\\s*\"id\":\\s*\"([^\"]*)\"\\s*\\}");
        Matcher matcher = postPattern.matcher(body);

        int postNumber = 1;
        while (matcher.find()) {
            String title = matcher.group(1);
            String content = matcher.group(2);
            String author = matcher.group(3);
            String createdAt = matcher.group(4);
            String id = matcher.group(6);

            result.append(String.format("📖 Post #%d\n", postNumber++));
            result.append(String.format("📝 Title: %s\n", title));
            result.append(String.format("📄 Content: %s\n", content));
            result.append(String.format("👤 Author: %s\n", author));
            result.append(String.format("📅 Created: %s\n", createdAt));
            result.append(String.format("🆔 ID: %s\n", id));
            result.append("───────────────────────────────────────\n");
        }

        return result.toString();
    }

    private String formatStatsResponse(HttpResponse<String> response) {
        // Accept all 2xx success codes instead of just 200
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return "❌ Error: HTTP " + response.statusCode() + " - " + response.body();
        }

        String body = response.body();
        if (body == null || body.isEmpty()) {
            return "❌ Error: Empty response from server";
        }

        String totalPosts = extractJsonValue(body, "total_posts");
        String maxPosts = extractJsonValue(body, "max_posts");
        String remainingPosts = extractJsonValue(body, "remaining_posts");
        String percentageUsed = extractJsonValue(body, "percentage_used");
        String canAddMore = extractJsonValue(body, "can_add_more");

        StringBuilder result = new StringBuilder();
        result.append("📊 BLOG STATISTICS\n");
        result.append("═══════════════════════════════════════\n");
        result.append(String.format("📝 Total Posts: %s\n", totalPosts != null ? totalPosts : "N/A"));
        result.append(String.format("🎯 Maximum Posts: %s\n", maxPosts != null ? maxPosts : "N/A"));
        result.append(String.format("📈 Remaining Posts: %s\n", remainingPosts != null ? remainingPosts : "N/A"));
        result.append(String.format("📊 Usage: %s%%\n", percentageUsed != null ? percentageUsed : "N/A"));
        result.append(String.format("✅ Can Add More: %s\n",
                "true".equals(canAddMore) ? "Yes" : ("false".equals(canAddMore) ? "No" : "N/A")));

        return result.toString();
    }

    // Simple JSON value extractor (works for simple string values)
    private String extractJsonValue(String json, String key) {
        if (json == null || key == null) return null;

        // Try string values with double quotes
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }

        // Try numeric values
        Pattern numPattern = Pattern.compile("\"" + key + "\"\\s*:\\s*([0-9.]+)");
        Matcher numMatcher = numPattern.matcher(json);
        if (numMatcher.find()) {
            return numMatcher.group(1);
        }

        // Try boolean values
        Pattern boolPattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(true|false)");
        Matcher boolMatcher = boolPattern.matcher(json);
        if (boolMatcher.find()) {
            return boolMatcher.group(1);
        }

        return null;
    }
}
