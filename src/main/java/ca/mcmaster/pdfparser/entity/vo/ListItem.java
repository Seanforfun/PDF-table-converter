package ca.mcmaster.pdfparser.entity.vo;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * @author: Seanforfun
 * @date: Created in 2019/1/6 11:52
 * @description: ${description}
 * @modified:
 * @version: 0.0.1
 */
public class ListItem implements ParsedItem {
    private List<String> columns;

    @Override
    public JsonObject getJsonObject() {
        return null;
    }
}
