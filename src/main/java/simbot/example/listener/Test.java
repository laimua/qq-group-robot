package simbot.example.listener;

import simbot.example.utils.FileProcessing;
import simbot.example.utils.SougouScelFileProcessing;

import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        //单个scel文件转化
        FileProcessing scel = new SougouScelFileProcessing();
        scel.parseFile("./src/main/java/simbot/example/原神常用词库.scel", "./src/main/java/simbot/example/原神常用词库.txt", true);

    }

}
