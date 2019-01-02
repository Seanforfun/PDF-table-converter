package ca.mcmaster.pdfparser.parser;

import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.Table;

import java.io.IOException;

public interface PDFTableParser {
    Table parserPDFTable(PDDocument pdf) throws IOException;
}
