# Java CLI Chatbot with Gemini — Implementation Plan

## Tech choices
- **Build tool:** Maven (single `pom.xml`, no plugins to install separately)
- **HTTP client:** `java.net.http.HttpClient` (built into Java 11+, zero extra deps)
- **JSON:** `org.json` library (lightweight, ~350KB jar)
- **Java version:** 17+

---

## Step 1 — Project structure

Create this directory tree by hand:

```
java-chatbot/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── chatbot/
                    ├── Main.java
                    ├── GeminiClient.java
                    └── ConversationHistory.java
```

---

## Step 2 — `pom.xml`

Configure Maven with:
- `<groupId>com.chatbot</groupId>`, `<artifactId>java-chatbot</artifactId>`
- Java 17 compiler source/target
- One dependency: `org.json:json:20240303`
- The `maven-assembly-plugin` set to produce a fat/executable jar with `Main` as the entry point (`jar-with-dependencies`)

---

## Step 3 — `ConversationHistory.java`

This class maintains the chat history as a list of `{role, text}` pairs so each request to Gemini includes the full conversation context.

```java
// Fields
private List<Message> messages = new ArrayList<>();

// Methods
void addUserMessage(String text)
void addModelMessage(String text)
JSONArray toGeminiContents()   // formats history into Gemini's "contents" array format
```

The `toGeminiContents()` method builds a `JSONArray` where each element is:
```json
{ "role": "user" | "model", "parts": [{ "text": "..." }] }
```

---

## Step 4 — `GeminiClient.java`

Handles all HTTP communication with the Gemini REST API.

```java
// Constructor takes apiKey string
GeminiClient(String apiKey)

// Core method
String sendMessage(JSONArray contents) throws IOException, InterruptedException
```

Inside `sendMessage`:
1. Build the request body as a `JSONObject`:
   ```json
   { "contents": <the contents array> }
   ```
2. POST to:
   ```
   https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=YOUR_KEY
   ```
3. Parse the response JSON to extract:
   ```
   response -> candidates[0] -> content -> parts[0] -> text
   ```
4. Return that text string.

Use `HttpClient`, `HttpRequest`, `HttpResponse` — all from `java.net.http`.

---

## Step 5 — `Main.java`

The entry point and REPL loop.

```java
public static void main(String[] args) {
    // 1. Read API key from environment variable GEMINI_API_KEY
    //    (fail fast with a clear error if missing)

    // 2. Create GeminiClient and ConversationHistory instances

    // 3. Print a welcome message

    // 4. Open a Scanner on System.in

    // 5. Loop:
    //    a. Print "You: " prompt
    //    b. Read a line
    //    c. If "quit" or "exit", break
    //    d. Add user message to history
    //    e. Call client.sendMessage(history.toGeminiContents())
    //    f. Add model reply to history
    //    g. Print "Bot: <reply>"
    //    h. Catch exceptions and print a user-friendly error
}
```

---

## Step 6 — Get a Gemini API key

1. Go to Google AI Studio: https://aistudio.google.com/app/apikey
2. Create a free API key
3. Set it as an environment variable:
   ```bash
   export GEMINI_API_KEY=your_key_here
   ```

---

## Step 7 — Build and run

```bash
# Build fat jar
mvn clean package -q

# Run
java -jar target/java-chatbot-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## Step 8 — Optional enhancements (after the basics work)

- Add a **system prompt** by prepending a `{ "role": "user", "parts": [{ "text": "You are a helpful assistant..." }] }` message followed by a model acknowledgement to the history before the loop starts.
- Add `generationConfig` to the request body to control temperature, max output tokens, etc.
- Color the terminal output with ANSI escape codes (`[32m` for green "Bot:", etc.).

---

## Key Gemini API reference

- **Endpoint:** `POST https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={API_KEY}`
- **Auth:** API key as a query parameter (no Bearer token needed for the free tier)
- **Docs:** https://ai.google.dev/gemini-api/docs/quickstart?lang=rest
