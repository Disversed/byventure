package com.byventure.app.ui.auth.logic;

import androidx.lifecycle.ViewModel;

/**
 * Общая ViewModel для всех экранов авторизации (привязана к Activity),
 * данные переживают поворот экрана и переходы между фрагментами.
 */
public class AuthViewModel extends ViewModel {

    // Вход
    private String loginUsername = "";
    private String loginPassword = "";

    // Регистрация
    private String contact = "";   // телефон или email
    private String username = "";
    private String password = "";

    public String getLoginUsername() { return loginUsername; }
    public void setLoginUsername(String v) { loginUsername = v; }

    public String getLoginPassword() { return loginPassword; }
    public void setLoginPassword(String v) { loginPassword = v; }

    public String getContact() { return contact; }
    public void setContact(String v) { contact = v; }

    public String getUsername() { return username; }
    public void setUsername(String v) { username = v; }

    public String getPassword() { return password; }
    public void setPassword(String v) { password = v; }

    /** Очистка модели после успешной регистрации/входа */
    public void clear() {
        loginUsername = "";
        loginPassword = "";
        contact = "";
        username = "";
        password = "";
    }

    // Правила валидации пароля

    public static boolean hasMinLength(String p) {
        return p.length() >= 8;
    }

    /** Есть хотя бы одна латинская буква и нет букв других алфавитов. */
    public static boolean hasLatinLetters(String p) {
        boolean hasLatin = false;
        for (int i = 0; i < p.length(); i++) {
            char c = p.charAt(i);
            if (Character.isLetter(c)) {
                boolean latin = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
                if (!latin) return false;
                hasLatin = true;
            }
        }
        return hasLatin;
    }

    public static boolean hasDigits(String p) {
        for (int i = 0; i < p.length(); i++) {
            if (Character.isDigit(p.charAt(i))) return true;
        }
        return false;
    }

    public static boolean hasSpecialSymbols(String p) {
        for (int i = 0; i < p.length(); i++) {
            char c = p.charAt(i);
            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) return true;
        }
        return false;
    }

    public static boolean isPasswordValid(String p) {
        return hasMinLength(p) && hasLatinLetters(p) && hasDigits(p) && hasSpecialSymbols(p);
    }
}