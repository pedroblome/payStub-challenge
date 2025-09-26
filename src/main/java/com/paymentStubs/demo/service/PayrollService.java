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

    public void processPayrollCsv(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            var csvToBean = new CsvToBeanBuilder<PayrollData>(reader)
                    .withType(PayrollData.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withThrowExceptions(false)
                    .build();

            // O loop principal agora apenas dispara tarefas para serem executadas em
            // paralelo.
            for (PayrollData payrollData : csvToBean) {
                // A chamada agora é para o novo método assíncrono.
                processRecordAsync(payrollData);
            }

            if (!csvToBean.getCapturedExceptions().isEmpty()) {
                for (CsvException exception : csvToBean.getCapturedExceptions()) {
                    logger.warn("Falha no parsing da linha {}: {}", exception.getLineNumber(), exception.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao ler o arquivo CSV.", e);
            throw new RuntimeException("Falha ao processar o arquivo.", e);
        }
    }

    /**
     * Processa um único registro de folha de pagamento de forma assíncrona.
     * 
     * @param data O objeto PayrollData a ser processado.
     */
    @Async("csvTaskExecutor") // Diz ao Spring para executar este método no nosso pool de threads customizado.
    public void processRecordAsync(PayrollData data) {
        // Esta lógica agora roda em uma thread separada.
        logger.info("Processando holerite para: {} na thread: {}", data.getFullName(),
                Thread.currentThread().getName());
        try {
            // Simulação da lógica de negócio (gerar PDF, enviar email)
            // 1. Chamar um PdfService.generate(data)
            // 2. Chamar um EmailService.send(pdf, data.email())

            // Simular um trabalho demorado

        } catch (Exception e) {
            // É CRUCIAL tratar exceções aqui, pois elas ocorrem em outra thread
            // e não seriam capturadas pelo chamador principal.
            logger.error("Falha ao processar registro para o funcionário: {}", data.getFullName(), e);
        }
    }
}