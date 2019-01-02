package ca.mcmaster.pdfparser.exceptions;

import java.io.IOException;

public class ExtractException extends IOException {
    public ExtractException(String message) {
        super(message);
    }
}
