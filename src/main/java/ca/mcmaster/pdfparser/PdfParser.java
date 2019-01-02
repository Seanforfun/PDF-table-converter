package ca.mcmaster.pdfparser;

import ca.mcmaster.pdfparser.exceptions.EmptyArgumentError;
import ca.mcmaster.pdfparser.loader.FileLoader;
import ca.mcmaster.pdfparser.utils.PDFLayoutTextStripper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PdfParser {
    public static void main(String[] args) throws IOException {
        if(args.length == 0)
            throw new EmptyArgumentError("Please provide a path (or file) for parsing");
        List<PDDocument> pdfFiles = new LinkedList<>();
        for(String arg : args){
            FileLoader.load(arg, pdfFiles);
        }
        for(PDDocument pdf : pdfFiles){

        }
    }
}
