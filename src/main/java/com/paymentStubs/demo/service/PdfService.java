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
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.paymentStubs.demo.dto.PayrollData;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Locale;

@Service
public class PdfService {

    private final MessageSource messageSource;

    public PdfService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Generates a PDF file in memory for a single payroll record.
     * 
     * @param data    The employee and payment data.
     * @param company The company name, used to load the correct logo.
     * @param country The country code, used for localization.
     * @return The generated PDF as a byte array.
     * @throws IOException If an I/O error occurs.
     */
    public byte[] generate(PayrollData data, String company, String country) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(30, 30, 30, 30);

        Locale locale = (country.equalsIgnoreCase("es") || country.equalsIgnoreCase("do"))
                ? new Locale("es", "DO")
                : Locale.US;

        Table headerTable = createHeader(data, company, locale);
        document.add(headerTable);

        document.add(new Paragraph("\n"));
        Table bodyTable = createBody(data, locale);
        document.add(bodyTable);

        Table footerTable = createFooter(data, locale);
        document.add(footerTable);

        document.close();

        return baos.toByteArray();
    }

    private Table createHeader(PayrollData data, String company, Locale locale) throws IOException {
        Table table = new Table(UnitValue.createPercentArray(new float[] { 50, 50 })).useAllAvailableWidth();
        table.setBorder(Border.NO_BORDER);

        Cell logoCell = new Cell().setBorder(Border.NO_BORDER);
        String logoPath = "logos/" + company.toLowerCase() + ".png";
        URL logoResource = getClass().getClassLoader().getResource(logoPath);
        if (logoResource == null) {
            logoResource = getClass().getClassLoader().getResource("logos/default.png"); // Fallback to default
        }

        if (logoResource != null) {
            Image logo = new Image(ImageDataFactory.create(logoResource));
            logo.setWidth(UnitValue.createPercentValue(60));
            logoCell.add(logo);
        } else {
            logoCell.add(new Paragraph("Logo not found").setFontSize(20).setBold());
        }
        table.addCell(logoCell);

        Cell infoCell = new Cell().setBorder(Border.NO_BORDER);
        infoCell.add(new Paragraph(messageSource.getMessage("paystub.title", new Object[] { data.getPeriod() }, locale))
                .setTextAlignment(TextAlignment.RIGHT));
        infoCell.add(new Paragraph(data.getFullName()).setBold().setFontSize(14).setTextAlignment(TextAlignment.RIGHT));
        infoCell.add(new Paragraph(data.getPosition()).setTextAlignment(TextAlignment.RIGHT));
        table.addCell(infoCell);

        return table;
    }

    private Table createBody(PayrollData data, Locale locale) {
        Table table = new Table(UnitValue.createPercentArray(new float[] { 25, 20, 10, 25, 20 }))
                .useAllAvailableWidth();
        table.setBorder(Border.NO_BORDER);

        table.addCell(createSimpleCell(messageSource.getMessage("paystub.gross_salary", null, locale)));
        table.addCell(createValueCell(formatCurrency(data.getGrossSalary())));
        table.addCell(createSimpleCell("")); // Spacer column

        table.addCell(createSimpleCell(messageSource.getMessage("paystub.discounts", null, locale)).setBold());
        table.addCell(createValueCell(""));

        table.addCell(createSimpleCell(messageSource.getMessage("paystub.gross_payment", null, locale)));
        table.addCell(createValueCell(formatCurrency(data.getGrossPayment())));
        table.addCell(createSimpleCell("")); // Spacer column

        table.addCell(createSimpleCell(messageSource.getMessage("paystub.sfs", null, locale)));
        table.addCell(createValueCell(formatCurrency(data.getSocialDiscountAmount())));

        table.addCell(createSimpleCell(""));
        table.addCell(createSimpleCell(""));
        table.addCell(createSimpleCell(""));

        table.addCell(createSimpleCell(messageSource.getMessage("paystub.afp", null, locale)));
        table.addCell(createValueCell(formatCurrency(data.getHealthDiscountAmount())));

        table.addCell(createSimpleCell(""));
        table.addCell(createSimpleCell(""));
        table.addCell(createSimpleCell(""));

        table.addCell(createSimpleCell(messageSource.getMessage("paystub.isr", null, locale)));
        table.addCell(createValueCell(formatCurrency(data.getTaxesDiscountAmount())));

        table.addCell(createSimpleCell(""));
        table.addCell(createSimpleCell(""));
        table.addCell(createSimpleCell(""));

        table.addCell(createSimpleCell(messageSource.getMessage("paystub.others", null, locale)));
        table.addCell(createValueCell(formatCurrency(data.getOtherDiscountAmount())));

        double totalDiscounts = data.getSocialDiscountAmount() + data.getHealthDiscountAmount()
                + data.getTaxesDiscountAmount() + data.getOtherDiscountAmount();
        table.addCell(createSimpleCell(""));
        table.addCell(createSimpleCell(""));
        table.addCell(createSimpleCell(""));
        table.addCell(createSimpleCell(messageSource.getMessage("paystub.total", null, locale)).setBold());
        table.addCell(createValueCell(formatCurrency(totalDiscounts)).setBold());

        return table;
    }

    private Table createFooter(PayrollData data, Locale locale) {
        Table table = new Table(UnitValue.createPercentArray(new float[] { 50, 50 })).useAllAvailableWidth();
        table.setBorderTop(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.BLACK, 1));
        table.setMarginTop(10);

        Cell labelCell = new Cell().add(
                new Paragraph(messageSource.getMessage("paystub.net_payment", null, locale)).setBold().setFontSize(14));
        labelCell.setBorder(Border.NO_BORDER);

        Cell valueCell = new Cell().add(new Paragraph(formatCurrency(data.getNetPayment())).setBold().setFontSize(14));
        valueCell.setTextAlignment(TextAlignment.RIGHT);
        valueCell.setBorder(Border.NO_BORDER);

        table.addCell(labelCell);
        table.addCell(valueCell);

        return table;
    }

    private Cell createSimpleCell(String content) {
        if (content == null || content.isBlank()) {
            content = " ";
        }
        return new Cell().add(new Paragraph(new Text(content)));
    }

    private Cell createValueCell(String text) {
        return createSimpleCell(text).setTextAlignment(TextAlignment.RIGHT);
    }

    private String formatCurrency(Double value) {
        if (value == null)
            return "0.00";
        return new DecimalFormat("#,##0.00").format(value);
    }
}
