package pl.edu.agh.jdziennik.wrapper;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.edu.agh.jdziennik.model.Attendance;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class WrappedAttendance extends AbstractWrapper<Attendance> {
    StringProperty sign;

    public WrappedAttendance(Attendance attendance) {
        super(attendance);

        sign = attendance == null ? null : new SimpleStringProperty(attendance.getAttendanceValue().getSign());
    }

    public StringProperty signProperty() {
        return sign;
    }

    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        Date date = getObject().getLesson().getDate();
        int lessonId = getObject().getLesson().getWeekTerm().getTimeWindow().getId();
        String subjectName = getObject().getLesson().getWeekTerm().getClassSubject().getSubject().getName();

        return String.format("%7s %10s          %4d.      %-15s", " ", dateFormat.format(date), lessonId, subjectName);
    }
}
