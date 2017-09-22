package taxi.processing;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class Record {

    private final static double UP_BORDER = 41.5;
    private final static double LEFT_BORDER = -75;

    private final boolean isTrain;

    //raw
    private String id;
    private String vendor_id;
    private String pickup_datetime;
    private String passenger_count;
    private String pickup_longitude;
    private String pickup_latitude;
    private String dropoff_longitude;
    private String dropoff_latitude;
    private String store_and_fwd_flag;
    private String trip_duration;

    //new
    private final static List<String> headersDummiesDays = new ArrayList<>(185);
    private final static List<String> headersDummies5min = new ArrayList<>(12*24);
    private final static List<String> headersDummiesSeasons = Arrays.asList("isWinter", "isSpring", "isSummer");
    private final static List<String> headersDummiesWeek = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");
    private final static List<String> headersDummiesPickBig = new ArrayList<>();
    private final static List<String> headersDummiesDropBig = new ArrayList<>();
    private final static List<String> headersDummiesPickSmall = new ArrayList<>();
    private final static List<String> headersDummiesDropSmall = new ArrayList<>();
    private final static List<String> headersDummiesBigTimesBig = new ArrayList<>();
    private List<String> dummiesDays = new ArrayList<>(185);
    private List<String> dummies5min = new ArrayList<>(12*24);
    private List<String> dummiesSeasons = new ArrayList<>(3);
    private List<String> dummiesWeek = new ArrayList<>(7);
    private List<String> dummiesPickBig = new ArrayList<>();
    private List<String> dummiesDropBig = new ArrayList<>();
    private List<String> dummiesPickSmall = new ArrayList<>();
    private List<String> dummiesDropSmall = new ArrayList<>();
    private List<String> dummiesBigTimesBig = new ArrayList<>();
    private String distancePitagoras;
    private String distanceManhatan;
    private String pickBigSquare;
    private String dropBigSquare;
    private String pickSmallSquare;
    private String dropSmallSquare;
    private String pickSquareHour;
    private String bigTimesBig;


    static {
        // initialize headers
        LocalDate start = LocalDate.parse("2016-01-01");
        LocalDate end = LocalDate.parse("2016-06-30");
        Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .forEach(s -> headersDummiesDays.add("is"+s));

        LocalTime startTime = LocalTime.parse("00:00");
        LocalTime endTime = LocalTime.parse("00:05");
        for (int i=1; i<=12*24; i++) {
            headersDummies5min.add("is" + startTime + "to" + endTime);
            startTime = startTime.plusMinutes(5);
            endTime = endTime.plusMinutes(5);
        }
    }

    @Override
    public String toString() {
        if (isTrain && (Double.parseDouble(pickup_longitude) < LEFT_BORDER
                || Double.parseDouble(dropoff_longitude) < LEFT_BORDER
                || Double.parseDouble(pickup_latitude) > UP_BORDER
                || Double.parseDouble(dropoff_latitude) > UP_BORDER))
            return "";
        return id
                + "," + vendor_id
                //+ "," + pickup_datetime
                + "," + passenger_count
                + "," + pickup_longitude
                + "," + pickup_latitude
                + "," + dropoff_longitude
                + "," + dropoff_latitude
                + "," + store_and_fwd_flag
                + "," + trip_duration
                + "," + String.join(",", dummiesDays)
                + "," + String.join(",", dummiesWeek)
                + "," + String.join(",", dummies5min)
                + "," + String.join(",", dummiesSeasons)
                + "," + distancePitagoras
                + "," + distanceManhatan
//                + "," + pickBigSquare
//                + "," + dropBigSquare
//                + "," + pickSmallSquare
//                + "," + dropSmallSquare
//                + "," + pickSquareHour
//                + "," + bigTimesBig
                + "," + String.join(",", dummiesPickBig) //file with big
                + "," + String.join(",", dummiesDropBig) //file with big
//                + "," + String.join(",", dummiesPickSmall) // file with small
//                + "," + String.join(",", dummiesDropSmall) // file with small
//                + "," + String.join(",", dummiesBigTimesBig) // file with bigXbig
                + "\r\n";
    }

    static String getHeader() {
        return "id"
                + "," + "vendor_id"
                //+ "," + "pickup_datetime"
                + "," + "passenger_count"
                + "," + "pickup_longitude"
                + "," + "pickup_latitude"
                + "," + "dropoff_longitude"
                + "," + "dropoff_latitude"
                + "," + "store_and_fwd_flag"
                + "," + "trip_duration"
                + "," + String.join(",", headersDummiesDays)
                + "," + String.join(",", headersDummiesWeek)
                + "," + String.join(",", headersDummies5min)
                + "," + String.join(",", headersDummiesSeasons)
                + "," + "distancePitagoras"
                + "," + "distanceManhatan"
//                + "," + "pickBigSquare"
//                + "," + "dropBigSquare"
//                + "," + "pickSmallSquare"
//                + "," + "dropSmallSquare"
//                + "," + "pickSquareHour"
//                + "," + "bigTimesBig"
                + "," + String.join(",", headersDummiesPickBig) // file with big
                + "," + String.join(",", headersDummiesDropBig) // file with big
//                + "," + String.join(",", headersDummiesPickSmall) //file with small
//                + "," + String.join(",", headersDummiesDropSmall) // file with small
//                + "," + String.join(",", headersDummiesBigTimesBig) // file with bigXbig
                + "\r\n";
    }

    Record(String[] values, boolean isTrain) {
        this.isTrain = isTrain;
        int i = 0;
        this.id = values[i++];
        this.vendor_id = values[i++];
        this.pickup_datetime = values[i++];

        this.passenger_count = values[i++];
        this.pickup_longitude = values[i++];
        this.pickup_latitude = values[i++];
        this.dropoff_longitude = values[i++];
        this.dropoff_latitude = values[i++];
        this.store_and_fwd_flag = values[i++];
        if (isTrain) {
            this.trip_duration = values[i];
        } else {
            this.trip_duration = "";
        }

        processSquares();
    }

    private void processDatetime(String pickup_datetime) {
        LocalDate pickupDate = LocalDate.parse(pickup_datetime.substring(0,10));
        DayOfWeek dayOfWeek = pickupDate.getDayOfWeek();
        LocalTime pickupTime = LocalTime.parse(pickup_datetime.substring(11, 19));

        // days
        LocalDate start = LocalDate.parse("2016-01-01");
        LocalDate end = LocalDate.parse("2016-06-30");

        Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .map(d -> d.equals(pickupDate) ? "1" : "0")
                .forEach(s -> dummiesDays.add(s));

        // weekdays
        for (int i=1; i<=7; i++) {
            dummiesWeek.add(dayOfWeek.getValue() == i ? "1" : "0");
        }

        // seasons
        dummiesSeasons.add(pickupDate.isBefore(LocalDate.parse("2016-03-20")) ? "1" : "0");
        dummiesSeasons.add(!pickupDate.isBefore(LocalDate.parse("2016-03-20"))
                && !pickupDate.isAfter(LocalDate.parse("2016-06-20"))? "1" : "0");
        dummiesSeasons.add(pickupDate.isAfter(LocalDate.parse("2016-06-20")) ? "1" : "0");

        // 5min
        LocalTime startTime = LocalTime.parse("00:00");
        LocalTime endTime = LocalTime.parse("00:05");
        for (int i=1; i<=12*24; i++) {
            dummies5min.add(!pickupTime.isBefore(startTime) && pickupTime.isBefore(endTime) ? "1" : "0");
            startTime = startTime.plusMinutes(5);
            endTime = endTime.plusMinutes(5);
        }
    }

    private void processDistances() {
        double pickLat = Double.parseDouble(pickup_latitude);
        double pickLong = Double.parseDouble(pickup_longitude);
        double dropLat = Double.parseDouble(dropoff_latitude);
        double dropLong = Double.parseDouble(dropoff_longitude);
        double diffLat = Math.abs(dropLat-pickLat);
        double diffLong = Math.abs(dropLong - pickLong);

        double manhatan = diffLat + diffLong;
        double pitagoras = Math.sqrt(Math.pow(diffLat,2)+Math.pow(diffLong,2));
        distanceManhatan = Double.toString(manhatan);
        distancePitagoras = Double.toString(pitagoras);
    }

    private void processSquares() {
        double bigPickLong = Math.round(Double.parseDouble(pickup_longitude)*20)/20.0;
        double bigPickLat = Math.round(Double.parseDouble(pickup_latitude)*20)/20.0;
        double bigDropLong = Math.round(Double.parseDouble(dropoff_longitude)*20)/20.0;
        double bigDropLat = Math.round(Double.parseDouble(dropoff_latitude)*20)/20.0;

        double smallPickLong = Math.round(Double.parseDouble(pickup_longitude)*200)/200.0;
        double smallPickLat = Math.round(Double.parseDouble(pickup_latitude)*200)/200.0;
        double smallDropLong = Math.round(Double.parseDouble(dropoff_longitude)*200)/200.0;
        double smallDropLat = Math.round(Double.parseDouble(dropoff_latitude)*200)/200.0;

        Locale l = Locale.ENGLISH;
        pickBigSquare = String.format(l, "%1$,.2f", bigPickLong) + "x" + String.format(l, "%1$,.2f", bigPickLat);
        dropBigSquare = String.format(l, "%1$,.2f", bigDropLong) + "x" + String.format(l, "%1$,.2f", bigDropLat);
        pickSmallSquare = String.format(l, "%1$,.3f", smallPickLong) + "x" + String.format(l, "%1$,.3f", smallPickLat);
        dropSmallSquare = String.format(l, "%1$,.3f", smallDropLong) + "x" + String.format(l, "%1$,.3f", smallDropLat);
        pickSquareHour = pickBigSquare + "x" + pickup_datetime.substring(11, 13);
        bigTimesBig = pickBigSquare + "x" + dropBigSquare;
    }

    static void fillDummiesHeader(Record record) {
        if (record.isTrain && (Double.parseDouble(record.pickup_longitude) < LEFT_BORDER
                || Double.parseDouble(record.dropoff_longitude) < LEFT_BORDER
                || Double.parseDouble(record.pickup_latitude) > UP_BORDER
                || Double.parseDouble(record.dropoff_latitude) > UP_BORDER))
            return;
        if (!headersDummiesPickBig.contains(record.pickBigSquare)) {
            headersDummiesPickBig.add(record.pickBigSquare);
        }
        if (!headersDummiesDropBig.contains(record.dropBigSquare)) {
            headersDummiesDropBig.add(record.dropBigSquare);
        }
        if (!headersDummiesPickSmall.contains(record.pickSmallSquare)) {
            headersDummiesPickSmall.add(record.pickSmallSquare);
        }
        if (!headersDummiesDropSmall.contains(record.dropSmallSquare)) {
            headersDummiesDropSmall.add(record.dropSmallSquare);
        }
        if (!headersDummiesBigTimesBig.contains(record.bigTimesBig)) {
            headersDummiesBigTimesBig.add(record.bigTimesBig);
        }
    }

    public void processSquareDummies() {
        processDatetime(pickup_datetime);
        processDistances();
        insertToDummiesSquares(headersDummiesPickBig, dummiesPickBig, pickBigSquare);
        insertToDummiesSquares(headersDummiesDropBig, dummiesDropBig, dropBigSquare);
        insertToDummiesSquares(headersDummiesPickSmall, dummiesPickSmall, pickSmallSquare);
        insertToDummiesSquares(headersDummiesDropSmall, dummiesDropSmall, dropSmallSquare);
        insertToDummiesSquares(headersDummiesBigTimesBig, dummiesBigTimesBig, bigTimesBig);
    }

    private void insertToDummiesSquares(List<String> headers, List<String> dummies, String field) {
        for (String header : headers) {
            if (header.equals(field)) {
                dummies.add("1");
            } else {
                dummies.add("0");
            }
        }
    }
}
