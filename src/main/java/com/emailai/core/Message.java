package com.emailai.core;


import lombok.Data;

/**
 * Asıl metin (AI'nin yazdığı yanıt) "content" alanında gelir.
 */
@Data
public class Message {
    private String role;     // "assistant", "user" vb.
    private String content;  // AI'nin yazdığı e-posta cevabı
}