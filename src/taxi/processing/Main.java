package taxi.processing;

public class Main {

    private static final String WORKSPACE = "C:\\Users\\MM\\Desktop\\workspace\\taxi\\";

    public static void main(String[] args) {

        String inPath = WORKSPACE + "raw\\trainWithoutDropoff.csv";
        String outPath = WORKSPACE + "output\\trainProcessed.csv";
        String outPathSmall = WORKSPACE + "output\\trainProcessedSmall.csv";

        DataFrame dfTrain = new DataFrame();
        dfTrain.process(inPath, outPath, outPathSmall, true);


        inPath = WORKSPACE + "raw\\test.csv";
        outPath = WORKSPACE + "output\\testProcessed.csv";
        outPathSmall = WORKSPACE + "output\\testProcessedSmall.csv";

        DataFrame dfTest = new DataFrame();
        dfTest.process(inPath, outPath, outPathSmall, false);
    }
}
