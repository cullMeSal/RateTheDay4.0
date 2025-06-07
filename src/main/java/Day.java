import java.time.LocalDate;

public class Day {
    private String note;
    private int rating;
    private int year;
    private int month;
    private int dayOfYear;
    private int dayOfMonth;
    private int dayOfWeek;
    private LocalDate date;

    private String dateISO;

    public Day() {
        super();
    }


    public Day(String dateInISO) {
        LocalDate date = LocalDate.parse(dateInISO);
        this.date = date;
        this.year = date.getYear();
        this.month = date.getMonthValue()+1;
        this.dayOfYear = date.getDayOfYear();
        this.dayOfMonth = date.getDayOfMonth();
        this.dayOfWeek = date.getDayOfWeek().getValue()+1;
        this.dateISO = dateInISO;

    }

    public Day(String dateInISO, int rating) {
        LocalDate date = LocalDate.parse(dateInISO);
        this.year = date.getYear();
        this.month = date.getMonthValue()+1;
        this.dayOfYear = date.getDayOfYear();
        this.dayOfMonth = date.getDayOfMonth();
        this.dayOfWeek = date.getDayOfWeek().getValue()+1;
        this.dateISO = dateInISO;

        this.rating = rating;
    }

    public Day(String dateInISO, int rating, String note) {
        LocalDate date = LocalDate.parse(dateInISO);
        this.date = date;
        this.year = date.getYear();
        this.month = date.getMonthValue()+1;
        this.dayOfYear = date.getDayOfYear();
        this.dayOfMonth = date.getDayOfMonth();
        this.dayOfWeek = date.getDayOfWeek().getValue()+1;
        this.dateISO = dateInISO;

        this.note = note;
        this.rating = rating;
    }


    public String getNote() {
        return note;
    }

    @Override
    public String toString() {
        return "Day{" +
                "note='" + note + '\'' +
                ", score=" + rating +
                ", dayISO='" + getDateISO() + "'" +
                '}';
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getYear() {
        return year;
    }
    public int getMonth() {
        return month;
    }
    public int getDayOfYear() {
        return dayOfYear;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDateISO() {
        return dateISO;
    }
}
