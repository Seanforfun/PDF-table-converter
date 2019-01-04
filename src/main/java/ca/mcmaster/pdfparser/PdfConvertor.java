package ca.mcmaster.pdfparser;

import ca.mcmaster.pdfparser.entity.PDFDocument;
import ca.mcmaster.pdfparser.entity.TabulaTable;
import ca.mcmaster.pdfparser.exceptions.EmptyArgumentError;
import ca.mcmaster.pdfparser.loader.FileLoader;
import ca.mcmaster.pdfparser.parser.PDFTableParser;
import ca.mcmaster.pdfparser.parser.TabulaPDFTableParser;
import ca.mcmaster.pdfparser.utils.PDFLayoutTextStripper;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class PdfConvertor {
    public static void main(String[] args) throws IOException {
        if(args.length == 0)
            throw new EmptyArgumentError("Please provide a path (or file) for parsing");
        List<PDFDocument> pdfFiles = new LinkedList<>();
        for(String arg : args){
            FileLoader.load(arg, pdfFiles);
        }
        PDFTableParser parser = new TabulaPDFTableParser();
        for(int i = 0; i < pdfFiles.size(); i++){
            PDFDocument pdf = null;
            try{
                pdf = pdfFiles.get(i);
                final String pdfName = pdf.getFilename();
                log.info("Start parsing file {}", pdfName);
                final PDDocument pdDocument = pdf.getPdf();
                TabulaTable table = (TabulaTable)parser.parserPDFTable(pdDocument);
//                final TabulaTable.TableIterator<String> iterator = table.getTableIterator();
//                while (iterator.hasNext()){
//                    log.info("{}", iterator.next());
//                }
//                log.info("{}", table.getContentFromTable(6, 2));
                System.out.println(table.showTableByLine());
            }finally {
                pdf.getPdf().close();
            }
        }
    }
}
