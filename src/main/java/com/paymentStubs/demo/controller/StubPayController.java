package com.paymentStubs.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.io.exceptions.IOException;
import com.paymentStubs.demo.service.PayrollService;

@RestController
@RequestMapping("/")
public class StubPayController {

    private final PayrollService payrollService;

    public StubPayController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/process")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "country", defaultValue = "do") String country,
            @RequestParam(value = "company", defaultValue = "default") String company) throws java.io.IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file was uploaded.");
        }

        try {
            payrollService.processPayrollCsv(file.getInputStream(), company, country);
            return ResponseEntity.ok("File received. Processing has started in the background.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error reading the file: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Error during processing: " + e.getMessage());
        }
    }
}
