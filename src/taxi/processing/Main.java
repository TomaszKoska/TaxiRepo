package taxi.processing;

public class Main {

    private static final String WORKSPACE = "C:\\Users\\MM\\Desktop\\workspace\\taxi\\";
    private static final String SUFFIX = "Tom";
    private static final String TRAIN_PATH = WORKSPACE + "raw\\trainWithoutDropoff.csv";
    private static final String TEST_PATH = WORKSPACE + "raw\\test.csv";

    public static void main(String[] args) {

        DataFrame df = new DataFrame();

        df.processHeader(TRAIN_PATH, true);
        df.processHeader(TEST_PATH, false);

        String outPath = WORKSPACE + "output\\train" + SUFFIX + ".csv";
        String outPathSmall = WORKSPACE + "output\\train" + SUFFIX + "5.csv";
        df.process(TRAIN_PATH, outPath, outPathSmall, true);

        outPath = WORKSPACE + "output\\test" + SUFFIX + ".csv";
        outPathSmall = WORKSPACE + "output\\test" + SUFFIX + "5.csv";
        df.process(TEST_PATH, outPath, outPathSmall, false);
    }
}
