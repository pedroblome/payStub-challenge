package com.paymentStubs.demo.service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.paymentStubs.demo.dto.PayrollData;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;

public class PdfService {

    private static final String DEST_FOLDER = "dest/";

    /**
     * Gera um arquivo PDF para um único registro de folha de pagamento.
     * @param data Os dados do funcionário e pagamento.
     * @throws IOException Se ocorrer um erro de I/O ao criar o arquivo ou a pasta.
     */
    public static void generate(PayrollData data) throws IOException {
        // Garante que o diretório de destino exista
        Files.createDirectories(Paths.get(DEST_FOLDER));

        String destFile = DEST_FOLDER + "paystub_" + data.getFullName().replace(" ", "_") + "_" + data.getPeriod() + ".pdf";

        PdfWriter writer = new PdfWriter(destFile);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(30, 30, 30, 30);

        // --- CABEÇALHO ---
        Table headerTable = createHeader(data);
        document.add(headerTable);

        // --- CORPO COM DADOS FINANCEIROS ---
        document.add(new Paragraph("\n")); // Espaçamento
        Table bodyTable = createBody(data);
        document.add(bodyTable);

        // --- RODAPÉ COM PAGAMENTO LÍQUIDO ---
        Table footerTable = createFooter(data);
        document.add(footerTable);

        document.close();
    }

    private static Table createHeader(PayrollData data) throws IOException {
        Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        table.setBorder(Border.NO_BORDER);

        // Coluna da Esquerda: Logo
        Cell logoCell = new Cell();
        URL logoResource = PdfService.class.getClassLoader().getResource("logos/FakeClients.png");
        if (logoResource != null) {
            Image logo = new Image(ImageDataFactory.create(logoResource));
            logo.setWidth(UnitValue.createPercentValue(60));
            logoCell.add(logo);
        } else {
            logoCell.add(new Paragraph("Logo not found").setFontSize(20).setBold());
        }
        logoCell.setBorder(Border.NO_BORDER);
        table.addCell(logoCell);

        // Coluna da Direita: Informações do Pagamento
        Cell infoCell = new Cell();
        infoCell.add(new Paragraph("Comprobante de pago " + data.getPeriod()).setTextAlignment(TextAlignment.RIGHT));
        infoCell.add(new Paragraph(data.getFullName()).setBold().setFontSize(14).setTextAlignment(TextAlignment.RIGHT));
        infoCell.add(new Paragraph(data.getPosition()).setTextAlignment(TextAlignment.RIGHT));
        infoCell.setBorder(Border.NO_BORDER);
        table.addCell(infoCell);

        return table;
    }

    private static Table createBody(PayrollData data) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25})).useAllAvailableWidth();
        table.setBorder(Border.NO_BORDER);

        // --- Lado Esquerdo: Salários ---
        table.addCell(createSimpleCell("Salario bruto"));
        table.addCell(createValueCell(formatCurrency(data.getGrossSalary())));
        
        // --- Lado Direito: Descontos ---
        table.addCell(createSimpleCell("Descuentos").setBold());
        table.addCell(createValueCell("")); // Célula vazia para alinhamento

        table.addCell(createSimpleCell("Pago Bruto"));
        table.addCell(createValueCell(formatCurrency(data.getGrossPayment())));
        
        table.addCell(createSimpleCell("SFS"));
        table.addCell(createValueCell(formatCurrency(data.getSocialDiscountAmount())));

        table.addCell(createSimpleCell("")); // Célula vazia
        table.addCell(createSimpleCell("")); // Célula vazia

        table.addCell(createSimpleCell("AFP"));
        table.addCell(createValueCell(formatCurrency(data.getHealthDiscountAmount())));
        
        table.addCell(createSimpleCell("")); // Célula vazia
        table.addCell(createSimpleCell("")); // Célula vazia

        table.addCell(createSimpleCell("ISR"));
        table.addCell(createValueCell(formatCurrency(data.getTaxesDiscountAmount())));
        
        table.addCell(createSimpleCell("")); // Célula vazia
        table.addCell(createSimpleCell("")); // Célula vazia

        table.addCell(createSimpleCell("Otros"));
        table.addCell(createValueCell(formatCurrency(data.getOtherDiscountAmount())));

        // --- Linha do Total ---
        double totalDescontos = data.getSocialDiscountAmount() + data.getHealthDiscountAmount() + data.getTaxesDiscountAmount() + data.getOtherDiscountAmount();
        table.addCell(createSimpleCell("")); // Célula vazia
        table.addCell(createSimpleCell("")); // Célula vazia
        table.addCell(createSimpleCell("Total").setBold());
        table.addCell(createValueCell(formatCurrency(totalDescontos)).setBold());

        return table;
    }

    private static Table createFooter(PayrollData data) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{25, 25})).useAllAvailableWidth();
        table.setBorderTop(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.BLACK, 1));
        table.setMarginTop(10);

        Cell labelCell = new Cell().add(new Paragraph("Pago neto").setBold().setFontSize(14));
        labelCell.setBorder(Border.NO_BORDER);

        Cell valueCell = new Cell().add(new Paragraph(formatCurrency(data.getNetPayment())).setBold().setFontSize(14));
        valueCell.setTextAlignment(TextAlignment.RIGHT);
        valueCell.setBorder(Border.NO_BORDER);

        table.addCell(labelCell);
        table.addCell(valueCell);

        return table;
    }

    private static Cell createSimpleCell(String text) {
        Cell cell = new Cell().add(new Paragraph(text));
        cell.setBorder(Border.NO_BORDER);
        return cell;
    }

    private static Cell createValueCell(String text) {
        Cell cell = createSimpleCell(text);
        cell.setTextAlignment(TextAlignment.RIGHT);
        return cell;
    }

    private static String formatCurrency(Double value) {
        if (value == null) return "0.00";
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(value);
    }
}
