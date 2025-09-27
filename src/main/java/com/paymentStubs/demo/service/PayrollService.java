package com.paymentStubs.demo.service;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import com.paymentStubs.demo.dto.PayrollData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class PayrollService {

    private static final Logger logger = LoggerFactory.getLogger(PayrollService.class);
    private final PdfService pdfService;

    // Inject the PdfService via constructor to make it available to this class.
    public PayrollService(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    /**
     * Processes the CSV file from an InputStream, delegating each record for asynchronous processing.
     * @param inputStream The CSV file content.
     * @param company The company identifier, used for branding (e.g., logos).
     * @param country The country code, used for localization (e.g., language).
     */
    public void processPayrollCsv(InputStream inputStream, String company, String country) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            var csvToBean = new CsvToBeanBuilder<PayrollData>(reader)
                    .withType(PayrollData.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withThrowExceptions(false)
                    .build();

            for (PayrollData payrollData : csvToBean) {
                // Dispatch each record for asynchronous processing with the provided context.
                processRecordAsync(payrollData, company, country);
            }

            if (!csvToBean.getCapturedExceptions().isEmpty()) {
                for (CsvException exception : csvToBean.getCapturedExceptions()) {
                    logger.warn("Failed to parse row {}: {}", exception.getLineNumber(), exception.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Error reading the CSV file stream.", e);
            throw new RuntimeException("Failed to process the file.", e);
        }
    }

    /**
     * Asynchronously processes a single payroll record by generating a PDF.
     * @param data The payroll data for one employee.
     * @param company The company identifier.
     * @param country The country code for localization.
     */
    @Async("csvTaskExecutor")
    public void processRecordAsync(PayrollData data, String company, String country) {
        logger.info("Generating PDF for: {} on thread: {}", data.getFullName(), Thread.currentThread().getName());
        try {
            // Delegate PDF generation to the specialized PdfService.
            pdfService.generate(data, company, country);
            logger.info("PDF successfully generated for: {}", data.getFullName());

        } catch (Exception e) {
            logger.error("Failed to process record for employee: {}", data.getFullName(), e);
        }
    }
}