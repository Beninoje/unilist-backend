package com.unilist.campora.records.auth;

public record ResetPasswordRequest(String email, String newPassword) {
}
