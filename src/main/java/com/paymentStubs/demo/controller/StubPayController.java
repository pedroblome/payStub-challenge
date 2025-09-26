package com.paymentStubs.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.io.exceptions.IOException;
import com.paymentStubs.demo.service.PayrollService;

@RestController
@RequestMapping("/api/stubpay")
public class StubPayController {

    private final PayrollService payrollService;

    // Injeção de dependência via construtor é a melhor prática.
    public StubPayController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws java.io.IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Arquivo não enviado.");
        }

        try {
            payrollService.processPayrollCsv(file.getInputStream());
            return ResponseEntity.ok("Arquivo processado. Verifique os logs para detalhes e possíveis erros de linha.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao ler o arquivo: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Erro no processamento: " + e.getMessage());
        }
    }
}