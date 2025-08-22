package com.emailai.dto;

public enum EmailTemplate {
    GENERIC,            // serbest, şablonsuz
    MEETING_CONFIRM,    // toplantı teyidi
    DELAY_NOTICE,       // gecikme bildirimi
    THANK_YOU,          // teşekkür
    KYC_REQUEST,        // KYC/evrak talebi
    PAYMENT_CONFIRM,    // ödeme onayı
    INCIDENT_UPDATE,    // olay/incident güncellemesi
    RISK_CLARIFICATION  // risk/uyum (compliance) açıklaması
}