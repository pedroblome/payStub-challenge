package com.paymentStubs.demo.controller;

import com.paymentStubs.demo.service.PayrollService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StubPayController.class)
class StubPayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PayrollService payrollService;

    @Test
    @DisplayName("Should return 200 OK when a valid file is uploaded")
    void uploadFile_WithValidFile_ShouldReturnOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "data.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "header1,header2\nvalue1,value2".getBytes());

        doNothing().when(payrollService).processPayrollCsv(any(), anyString(), anyString());

        mockMvc.perform(multipart("/process")
                .file(file)
                .param("country", "en")
                .param("company", "atdev"))
                .andExpect(status().isOk())
                .andExpect(content().string("Arquivo recebido. O processamento foi iniciado em segundo plano."));

        verify(payrollService, times(1)).processPayrollCsv(any(), eq("atdev"), eq("en"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when the file is empty")
    void uploadFile_WithEmptyFile_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.csv",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]);

        mockMvc.perform(multipart("/process")
                .file(file)
                .param("country", "en")
                .param("company", "atdev"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Arquivo não enviado."));

        verify(payrollService, never()).processPayrollCsv(any(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error when service throws an exception")
    void uploadFile_WhenServiceFails_ShouldReturnInternalServerError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "data.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "header1,header2\nvalue1,value2".getBytes());

        // Corrected line: Throw an unchecked exception that the controller handles.
        doThrow(new RuntimeException("Test service exception")).when(payrollService).processPayrollCsv(any(),
                anyString(), anyString());

        mockMvc.perform(multipart("/process")
                .file(file)
                .param("country", "en")
                .param("company", "atdev"))
                .andExpect(status().isInternalServerError());
    }
}
