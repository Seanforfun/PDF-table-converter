package ca.mcmaster.pdfparser.loader;

import ca.mcmaster.pdfparser.entity.PDFDocument;
import ca.mcmaster.pdfparser.exceptions.InvalidPathException;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileLoader {
    public static void load(String path, List<PDFDocument> pdfFiles) throws IOException {
        if(path == null || path.length() == 0)
            throw new InvalidPathException("Provided Path cannot be null or empty.");
        File resource = new File(path);
        load(resource, pdfFiles);
    }

    private static void load(File f, List<PDFDocument> pdfFiles) throws IOException {
        if(f.isFile()){
            if(f.getName().toLowerCase().endsWith(".pdf")){
                pdfFiles.add(new PDFDocument(f.getName(), PDDocument.load(f)));
            }
        }else{
            if(!f.isDirectory())
                throw new InvalidPathException(f.getName() + " is Not a file or path.");
            File[] files = f.listFiles();
            for(File file : files)
                load(file, pdfFiles);
        }
    }
}
