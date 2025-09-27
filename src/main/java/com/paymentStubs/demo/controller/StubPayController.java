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

    // Injeção de dependência via construtor é a melhor prática.
    public StubPayController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/process")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "country", defaultValue = "do") String country,
            @RequestParam(value = "company", defaultValue = "default") String company) throws java.io.IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Arquivo não enviado.");
        }

        try {
            payrollService.processPayrollCsv(file.getInputStream(), company, country);
            return ResponseEntity.ok("Arquivo recebido. O processamento foi iniciado em segundo plano.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao ler o arquivo: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Erro no processamento: " + e.getMessage());
        }
    }
}