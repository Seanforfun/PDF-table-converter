package ca.mcmaster.pdfparser.exceptions;

public class EmptyArgumentError extends RuntimeException {
    public EmptyArgumentError(String message) {
        super(message);
    }
}
