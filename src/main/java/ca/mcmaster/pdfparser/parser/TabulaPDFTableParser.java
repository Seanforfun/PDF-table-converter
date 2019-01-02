package ca.mcmaster.pdfparser.parser;

import ca.mcmaster.pdfparser.entity.TabulaTable;
import ca.mcmaster.pdfparser.exceptions.ExtractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TabulaPDFTableParser implements PDFTableParser {
    @Override
    public Table parserPDFTable(PDDocument pdf) throws IOException {
        if(pdf == null) {
            throw new ExtractException("pdf file is null.");
        }
        ObjectExtractor extractor = new ObjectExtractor(pdf);
        int pageNum = pdf.getNumberOfPages();
        /**
         * First get all cells and texttrunks from the pdf, we want to create our own
         * treemap by using the coordinates of the cells.
         */
        List<Cell> cells = new ArrayList<>();
        List<TextChunk> textChunks = new ArrayList<>();
        for(int i = 1; i <= pageNum; i++){
            Page page = extractor.extract(i);
            cells.addAll(getCellsFromSinglePage(page));
            textChunks.addAll(getTextChunksFromSinglePage(page));
        }
        final Table pdfTable = new TabulaTable(cells, textChunks);
        return pdfTable;
    }

    private List<Cell> getCellsFromSinglePage(Page page){
        if(page == null) throw new NullPointerException("page cannot be null when extracting cells.");
        List<Ruling> h = page.getHorizontalRulings();
        List<Ruling> v = page.getVerticalRulings();
        return SpreadsheetExtractionAlgorithm.findCells(h, v);
    }

    private List<TextChunk> getTextChunksFromSinglePage(Page page){
        if(page == null) throw new NullPointerException("page cannot be null when extracting cells.");
        return TextElement.mergeWords(page.getText(), page.getVerticalRulings());
    }
}
