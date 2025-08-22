package com.emailai.core;

import lombok.Data;
import java.util.List;

/**
 * OpenAI chat/completions cevabının ana modeli.
 * "choices" listesi içinden mesajın içeriğini (reply) alacağız.
 */
@Data
public class ChatCompletionResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
}