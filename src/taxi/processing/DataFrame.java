package taxi.processing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class DataFrame {

    private List<Record> records;

    DataFrame() {
        records = new ArrayList<>();
    }

    void process(String inPath, String outPath, String outPathSmall, boolean isTrain) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileOutputStream outputStreamSmall = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(inPath);
            outputStream = new FileOutputStream(outPath);
            outputStreamSmall = new FileOutputStream(outPathSmall);
            sc = new Scanner(inputStream, "UTF-8");
            int i = 0;
            while (sc.hasNextLine()) {
                if (i == 0) {
                    i++;
                    sc.nextLine();
                    outputStream.write(Record.getHeader().getBytes());
                    outputStreamSmall.write(Record.getHeader().getBytes());
                    continue;
                }
                String line = sc.nextLine();
                System.out.println(i++);
                Record record = new Record(line.split(","), isTrain);
                outputStream.write(record.toString().getBytes());
                if (Math.random() < 0.05) {
                    outputStreamSmall.write(record.toString().getBytes());
                }
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }

            inputStream.close();
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    void load(String path) {
//        try (Stream<String> stream = Files.lines(Paths.get(path)).skip(1)) {
//
//            stream.map(this::lineToRecord)
//                    .forEach(record -> records.add(record));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    void output(String path) {
//        List<String> outputLines = new ArrayList<>(records.size()+1);
//
//        outputLines.add(Record.getHeader());
//        records.stream()
//                .map(Record::toString)
//                .forEach(outputLines::add);
//        try {
//            Files.write(Paths.get(path), outputLines);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Record lineToRecord(String line) {
//        String[] values = line.split(",");
//        return new Record(values);
//    }

}
