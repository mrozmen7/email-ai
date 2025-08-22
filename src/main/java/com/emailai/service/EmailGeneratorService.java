package com.emailai.service;

import com.emailai.core.ChatCompletionResponse;
import com.emailai.core.Message;
import com.emailai.dto.EmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Bu sınıf işin mutfağı.
 * - Kullanıcıdan gelen metni ve tonu alır
 * - OpenAI'ye uygun formatta gönderir
 * - Dönüşte reply metnini çıkarır ve Controller'a verir
 */
@Service
public class EmailGeneratorService {

    private final WebClient webClient; // HTTP isteklerini atar
    private final String model;        // Hangi OpenAI modeli (örn: gpt-4o-mini)
    private final double temperature;  // Yaratıcılık seviyesi (0-1 arası)
    private final Duration timeout;    // Kaç ms sonra vazgeçilsin

    // Yapıcı (constructor): Uygulama ayarlarını alıp WebClient'ı hazırlar
    public EmailGeneratorService(
            WebClient.Builder builder,
            @Value("${openai.api.url}") String apiUrl,
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.model}") String model,
            @Value("${openai.temperature:0.7}") double temperature,
            @Value("${openai.timeout-ms:15000}") long timeoutMs
    ) {
        this.webClient = builder
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.model = model;
        this.temperature = temperature;
        this.timeout = Duration.ofMillis(timeoutMs);
    }

    /**
     * Dışarıdan EmailRequest alır, OpenAI'ye sorar, metin cevabını döner.
     */
    public String generateEmailReply(EmailRequest req) {

        // 1) Sistem talimatı: Banka uyumlu, kısa ve net yaz
        String systemPrompt = """
                You are an assistant writing email replies for a BANK. 
                Principles:
                - Be clear, concise, and professional.
                - Do NOT create a subject line.
                - Keep the reply in the same language as the original email.
                - Follow bank tone: polite, respectful, compliant, and risk-aware.
                - If a "tone" is provided (e.g., Formal, Professional, Friendly, Concise), reflect it.
                - If a "length" is provided, keep reply length accordingly:
                  SHORT = 1–2 sentences, MEDIUM = 3–6 sentences, LONG = more detail but still concise.
                - Never include internal or confidential bank data in the reply.
                """;

        // 2) Banka şablon talimatı (template’e göre yönlendirici ipuçları)
        String templateHint = switch (req.getTemplate()) {
            case MEETING_CONFIRM -> """
                    Template: Meeting Confirmation.
                    Goals:
                    - Confirm the meeting time/date.
                    - Offer alternative if needed.
                    - Mention agenda briefly if exists.
                    """;
            case DELAY_NOTICE -> """
                    Template: Delay Notice.
                    Goals:
                    - Inform about the delay with a brief reason (no sensitive details).
                    - Provide a new ETA or next step.
                    - Apologize briefly and keep a professional tone.
                    """;
            case THANK_YOU -> """
                    Template: Thank You.
                    Goals:
                    - Acknowledge the counterpart's effort.
                    - Express appreciation in a professional tone.
                    """;
            case KYC_REQUEST -> """
                    Template: KYC / Document Request.
                    Goals:
                    - Politely request the required documents (e.g., ID, proof of address).
                    - Mention secure channel and data privacy briefly.
                    - Provide a deadline or next step if applicable.
                    """;
            case PAYMENT_CONFIRM -> """
                    Template: Payment Confirmation.
                    Goals:
                    - Confirm receipt (do NOT invent transaction details).
                    - Mention next steps or when funds will be available.
                    """;
            case INCIDENT_UPDATE -> """
                    Template: Incident / Service Interruption Update.
                    Goals:
                    - Provide a brief status update without internal details.
                    - State impact (if any) and expected next update time.
                    """;
            case RISK_CLARIFICATION -> """
                    Template: Risk/Compliance Clarification.
                    Goals:
                    - Clarify policy or risk point in simple terms.
                    - Avoid legal advice; be factual and careful.
                    """;
            case GENERIC -> """
                    Template: Generic business reply.
                    Goal: Write a clear, polite, action-oriented response.
                    """;
        };

        // 3) Kullanıcı içeriğini birleştir
        String userContent = "Original email:\n" + req.getEmailContent()
                + (req.getTone() != null && !req.getTone().isBlank() ? ("\nTone: " + req.getTone()) : "")
                + (req.getLength() != null ? ("\nDesired length: " + req.getLength()) : "");

        // 4) Mesajlar
        List<Map<String, Object>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "system", "content", templateHint),
                Map.of("role", "user", "content", userContent)
        );

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", messages,
                "temperature", temperature
        );

        try {
            ChatCompletionResponse res = webClient.post()
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                            r -> r.bodyToMono(String.class)
                                    .map(msg -> new RuntimeException("OpenAI error " + r.statusCode() + ": " + msg)))
                    .bodyToMono(ChatCompletionResponse.class)
                    .timeout(timeout)
                    .block();

            if (res == null || res.getChoices() == null || res.getChoices().isEmpty())
                throw new RuntimeException("Empty response from OpenAI");

            Message msg = res.getChoices().get(0).getMessage();
            if (msg == null || msg.getContent() == null || msg.getContent().isBlank())
                throw new RuntimeException("No content in OpenAI response");

            return msg.getContent().trim();

        } catch (WebClientResponseException e) {
            throw new RuntimeException("OpenAI HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate reply: " + e.getMessage(), e);
        }
    }
}