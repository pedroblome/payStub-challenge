package com.paymentStubs.demo.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MessageSourceConfig.class)
class MessageSourceConfigTest {

    @Autowired
    private MessageSource messageSource;

    @Test
    @DisplayName("Should load MessageSource bean into the context")
    void messageSourceBean_ShouldBeLoaded() {
        assertNotNull(messageSource, "MessageSource bean should not be null.");
    }

    @Test
    @DisplayName("Should resolve English messages correctly from test resources")
    void getMessage_ForEnglishLocale_ShouldReturnCorrectMessage() {
        Locale englishLocale = Locale.US;
        String expectedMessage = "This is a test message.";
        String actualMessage = messageSource.getMessage("test.message", null, englishLocale);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Should resolve Spanish messages correctly from test resources")
    void getMessage_ForSpanishLocale_ShouldReturnCorrectMessage() {
        Locale spanishLocale = new Locale("es");
        String expectedMessage = "Este es un mensaje de prueba.";
        String actualMessage = messageSource.getMessage("test.message", null, spanishLocale);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Should throw NoSuchMessageException for a nonexistent code")
    void getMessage_ForNonexistentCode_ShouldThrowException() {
        assertThrows(NoSuchMessageException.class, () -> {
            messageSource.getMessage("nonexistent.code", null, Locale.US);
        });
    }
}
