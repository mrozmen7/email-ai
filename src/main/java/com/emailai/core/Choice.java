package com.emailai.core;


import lombok.Data;

/**
 * OpenAI cevabındaki her bir seçenek.
 * Genelde ilk seçenek (index 0) yeterli olur.
 */
@Data
public class Choice {
    private int index;
    private Message message;
    private String finish_reason;
}