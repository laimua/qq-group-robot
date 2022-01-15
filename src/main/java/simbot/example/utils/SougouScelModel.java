package simbot.example.utils;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class SougouScelModel {
    private Map<String, List<String>> wordMap;
    private String name;
    private String type;
    private String description;
    private String sample;
}
