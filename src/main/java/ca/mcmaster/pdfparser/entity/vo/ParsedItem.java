package ca.mcmaster.pdfparser.entity.vo;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: Seanforfun
 * @date: Created in 2019/1/6 10:58
 * @description: ${description}
 * @modified:
 * @version: 0.0.1
 */
public interface ParsedItem {
    public JsonObject getJsonObject();
}
