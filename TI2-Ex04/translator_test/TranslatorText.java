package translator_test;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Translates English text to French with Azure AI Translator.
 *
 * Required environment variables:
 *   AZURE_TRANSLATOR_KEY    - the Translator resource key
 *   AZURE_TRANSLATOR_REGION - the resource region (for regional resources)
 *
 * Usage:
 *   java TranslatorText "Hello, how are you?"
 *   echo "Hello, world!" | java TranslatorText
 */
public class TranslatorText {
    private static final String TRANSLATOR_ENDPOINT =
            "https://api.cognitive.microsofttranslator.com/translate";

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static void main(String[] args) {
        try {
            String text = readText(args);
            if (text.isBlank()) {
                System.err.println("Provide English text as an argument or through standard input.");
                System.exit(1);
            }

            System.out.println(translateToFrench(text));
        } catch (IllegalStateException | IOException | InterruptedException exception) {
            System.err.println("Translation failed: " + exception.getMessage());
            System.exit(1);
        }
    }

    private static String translateToFrench(String englishText)
            throws IOException, InterruptedException {
        String subscriptionKey = requiredEnvironmentVariable("AZURE_TRANSLATOR_KEY");
        String region = requiredEnvironmentVariable("AZURE_TRANSLATOR_REGION");

        String requestBody = "[{\"Text\":\"" + escapeJson(englishText) + "\"}]";
        String query = "?api-version=3.0&from=en&to=fr";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TRANSLATOR_ENDPOINT + query))
                .header("Content-Type", "application/json")
                .header("Ocp-Apim-Subscription-Key", subscriptionKey)
                .header("Ocp-Apim-Subscription-Region", region)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(
                request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() / 100 != 2) {
            throw new IOException(
                    "Azure Translator returned HTTP " + response.statusCode()
                            + ": " + response.body());
        }

        return extractTranslatedText(response.body());
    }

    private static String readText(String[] args) {
        if (args.length > 0) {
            return String.join(" ", args);
        }

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
    }

    private static String requiredEnvironmentVariable(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Set the " + name + " environment variable before running the program.");
        }
        return value;
    }

    private static String extractTranslatedText(String responseBody) throws IOException {
        String marker = "\"text\":\"";
        int valueStart = responseBody.indexOf(marker);
        if (valueStart < 0) {
            throw new IOException("Azure returned an unexpected response: " + responseBody);
        }

        valueStart += marker.length();
        StringBuilder translatedText = new StringBuilder();
        boolean escaped = false;
        for (int index = valueStart; index < responseBody.length(); index++) {
            char character = responseBody.charAt(index);
            if (escaped) {
                translatedText.append(unescapeJsonCharacter(character));
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else if (character == '"') {
                return translatedText.toString();
            } else {
                translatedText.append(character);
            }
        }

        throw new IOException("Azure returned an unterminated translation value.");
    }

    private static char unescapeJsonCharacter(char character) throws IOException {
        switch (character) {
            case '"':
            case '\\':
            case '/':
                return character;
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            default:
                throw new IOException("Unsupported JSON escape in Azure response.");
        }
    }

    private static String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder(value.length());
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
            }
        }
        return escaped.toString();
    }
}