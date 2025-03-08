package com.example.optimizer;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ChatGPTActivity extends AppCompatActivity {

    private EditText queryEditText;
    private TextView responseTextView;
    private Button sendButton;

    private static final String API_KEY = "sk-proj-gXw5TxO13WXbG4c0wdTHT3BlbkFJQo5DHO6FRbMGm6oMTod8";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final boolean USE_MOCK_RESPONSE = true; // Toggle this to enable/disable mock response

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_gptactivity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        queryEditText = findViewById(R.id.chatgpt_query);
        responseTextView = findViewById(R.id.chatgpt_response);
        sendButton = findViewById(R.id.chatgpt_send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = queryEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    if (USE_MOCK_RESPONSE) {
                        useMockResponse(query);
                    } else {
                        sendQueryToChatGPT(query);
                    }
                } else {
                    Toast.makeText(ChatGPTActivity.this, "Please enter a query", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendQueryToChatGPT(String query) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer " + API_KEY);

        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("model", "gpt-3.5-turbo");

            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", query);
            messages.put(message);

            jsonParams.put("messages", messages);

            StringEntity entity = new StringEntity(jsonParams.toString(), "UTF-8");

            client.post(this, API_URL, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        String reply = response.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                        responseTextView.setText(reply);
                    } catch (Exception e) {
                        Log.e("ChatGPTActivity", "Error parsing response: " + e.getMessage());
                        Toast.makeText(ChatGPTActivity.this, "Error processing response", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("ChatGPTActivity", "API call failed: " + responseString);
                    Toast.makeText(ChatGPTActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e("ChatGPTActivity", "API call failed: " + throwable.getMessage());
                    if (errorResponse != null) {
                        Log.e("ChatGPTActivity", "Error response: " + errorResponse.toString());
                    }
                    Toast.makeText(ChatGPTActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("ChatGPTActivity", "Error creating JSON params: " + e.getMessage());
            Toast.makeText(ChatGPTActivity.this, "Error creating request parameters", Toast.LENGTH_SHORT).show();
        }
    }

    private void useMockResponse(String query) {
        String mockResponse = "This is a mock response to the query: " + query;
        responseTextView.setText(mockResponse);
    }
}