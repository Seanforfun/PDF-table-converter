package ca.mcmaster.pdfparser.decoder;

import ca.mcmaster.pdfparser.entity.Table;
import ca.mcmaster.pdfparser.entity.TabulaTable;
import ca.mcmaster.pdfparser.entity.vo.ParsedItem;

import java.util.List;
import java.util.Map;

/**
 * @author: Seanforfun
 * @date: Created in 2019/1/6 9:57
 * @description: ${description}
 * @modified:
 * @version: 0.0.1
 */
public interface Decoder {
    /**
     * @param command The command line for check
     * @return If current command is valid.
     */
    public boolean compile(String command);

    /**
     * @param command get a decoder which can decode current format
     * @return The decoder instance
     */
    public Decoder getDecoder(String command);

    /**
     * @param row The starting row of the begining row number.
     * @param col The starting col of the begining column number.
     * @return
     */
    public ParsedItem decode(TabulaTable table, int row, int col);
}
