package ca.mcmaster.pdfparser.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;

@Slf4j
public class PDFDocument {
    @Setter@Getter
    private String filename;
    @Setter@Getter
    private PDDocument pdf;

    public PDFDocument(String filename, PDDocument pdf) {
        this.filename = filename;
        this.pdf = pdf;
    }
}
