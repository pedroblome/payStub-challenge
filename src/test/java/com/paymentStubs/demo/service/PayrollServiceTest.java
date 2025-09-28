package com.paymentStubs.demo.service;

import com.paymentStubs.demo.dto.PayrollData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private PdfService pdfService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PayrollService payrollService;

    private PayrollData testData;

    @BeforeEach
    void setUp() {
        testData = new PayrollData();
        testData.setFullName("Jane Doe");
        testData.setEmail("jane.doe@example.com");
        testData.setPeriod("2025-01-01");
    }

    @Test
    @DisplayName("Should process a valid CSV and attempt to process each row")
    void processPayrollCsv_WithValidData_ShouldCallAsyncProcessing() {
        String csvContent = "full_name,email,position,health_discount_amount,social_discount_amount,taxes_discount_amount,other_discount_amount,gross_salary,gross_payment,net_payment,period\n"
                +
                "Jane Doe,jane.doe@example.com,Developer,100,150,800,50,5000,5200,4100,2025-01-01\n" +
                "John Smith,john.smith@example.com,Manager,120,180,900,60,6000,6300,4940,2025-01-01";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        String company = "acme";
        String country = "en";

        PayrollService payrollServiceSpy = spy(payrollService);
        doNothing().when(payrollServiceSpy).processRecordAsync(any(PayrollData.class), anyString(), anyString());

        payrollServiceSpy.processPayrollCsv(inputStream, company, country);

        verify(payrollServiceSpy, times(2)).processRecordAsync(any(PayrollData.class), eq(company), eq(country));
    }

    @Test
    @DisplayName("Async record processing should generate PDF and send email")
    void processRecordAsync_ShouldGenerateAndSendEmail() throws Exception {
        String company = "acme";
        String country = "en";
        byte[] fakePdfBytes = "fake-pdf-content".getBytes();

        when(pdfService.generate(any(PayrollData.class), eq(company), eq(country))).thenReturn(fakePdfBytes);
        doNothing().when(emailService).sendEmailWithAttachment(anyString(), anyString(), anyString(), any(byte[].class),
                anyString());

        payrollService.processRecordAsync(testData, company, country);

        verify(pdfService, times(1)).generate(testData, company, country);
        verify(emailService, times(1)).sendEmailWithAttachment(
                eq(testData.getEmail()),
                anyString(),
                anyString(),
                eq(fakePdfBytes),
                anyString());
    }

    @Test
    @DisplayName("Should log an error when PDF generation fails and not send email")
    void processRecordAsync_WhenPdfGenerationFails_ShouldNotSendEmail() throws Exception {
        String company = "acme";
        String country = "en";

        when(pdfService.generate(any(PayrollData.class), eq(company), eq(country)))
                .thenThrow(new IOException("PDF creation failed"));

        payrollService.processRecordAsync(testData, company, country);

        verify(pdfService, times(1)).generate(testData, company, country);
        verify(emailService, never()).sendEmailWithAttachment(any(), any(), any(), any(), any());
    }
}
