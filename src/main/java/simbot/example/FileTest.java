package simbot.example;

import org.springframework.beans.factory.annotation.Autowired;
import simbot.example.persistence.service.IKeyWordBanService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileTest {
    //按行读取文本文件的内容并储存到列表中
    public static List readFileContent(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        List list = new ArrayList();
        try {
            reader = new BufferedReader(new FileReader(file));
            String readStr;
            while ((readStr = reader.readLine()) != null) {
                list.add(readStr.split(" ")[readStr.split(" ").length-1]);
            }
            reader.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return list;
    }

    @Autowired
    //IKeyWordBanService keyWordBanService;

    public static void main(String[] args) {

    }

}
