package ca.mcmaster.pdfparser.parser;

import ca.mcmaster.pdfparser.entity.Table;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;

public interface PDFTableParser {
    Table parserPDFTable(PDDocument pdf) throws IOException;
}
