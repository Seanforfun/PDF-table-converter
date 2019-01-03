package ca.mcmaster.pdfparser.algorithm;

import ca.mcmaster.pdfparser.entity.Page;
import ca.mcmaster.pdfparser.entity.Table;

import java.util.List;

public interface ExtractionAlgorithm {

    List<? extends Table> extract(Page page);
    String toString();
    
}
