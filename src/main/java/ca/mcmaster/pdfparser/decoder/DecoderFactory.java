package ca.mcmaster.pdfparser.decoder;

/**
 * @author: Seanforfun
 * @date: Created in 2019/1/6 12:05
 * @description: ${description}
 * @modified:
 * @version: 0.0.1
 */
public class DecoderFactory {
    public static CellDecoder createCellDecoder(String command){
        return new CellDecoder(command);
    }
}
