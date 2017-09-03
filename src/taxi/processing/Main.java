package taxi.processing;

public class Main {

    private static final String WORKSPACE = "C:\\Users\\MM\\Desktop\\workspace\\taxi\\";

    public static void main(String[] args) {

        String inPath = WORKSPACE + "raw\\trainWithoutDropoff.csv";
        String outPath = WORKSPACE + "output\\trainProcessedNoDummies.csv";
        String outPathSmall = WORKSPACE + "output\\trainProcessedNoDummiesSmall.csv";

        DataFrame dfTrain = new DataFrame();
        dfTrain.process(inPath, outPath, outPathSmall, true);


        inPath = WORKSPACE + "raw\\test.csv";
        outPath = WORKSPACE + "output\\testProcessedNoDummies.csv";
        outPathSmall = WORKSPACE + "output\\testProcessedNoDummiesSmall.csv";

        DataFrame dfTest = new DataFrame();
        dfTest.process(inPath, outPath, outPathSmall, false);
    }
}
