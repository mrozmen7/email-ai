package com.emailai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequest {

    @NotBlank(message = "emailContent must not be blank")
    @JsonProperty("emailContent")
    private String emailContent;

    // backwards-compat (emailContext adıyla gelenler için)
    @JsonProperty("emailContext")
    public void setEmailContextCompat(String legacy) { this.emailContent = legacy; }

    // Ton: Formal, Casual, Friendly, Professional, Concise vs. (serbest metin)
    private String tone;

    /**
     * Uzunluk: SHORT (1–2 cümle), MEDIUM (3–6 cümle),
     * LONG (daha detaylı ama yine net)
     */
    private Length length = Length.MEDIUM;

    /**
     * Bankaya uygun şablon seçimi (zorunlu değil).
     * Şablon yoksa GENERIC kullanılır.
     */
    private EmailTemplate template = EmailTemplate.GENERIC;

    public enum Length { SHORT, MEDIUM, LONG }
}