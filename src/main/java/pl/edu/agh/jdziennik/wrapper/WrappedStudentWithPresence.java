package pl.edu.agh.jdziennik.wrapper;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import pl.edu.agh.jdziennik.model.Attendance;
import pl.edu.agh.jdziennik.model.Student;
import pl.edu.agh.jdziennik.wrapper.wrappingaccess.WrappingAccessor;

import java.sql.Date;
import java.util.List;

public class WrappedStudentWithPresence extends WrappedStudent {
    private WrappedAttendance attendance;

    private List<WrappedAttendance> attendanceList;
    private ObjectProperty<Long> presenceCount = new SimpleObjectProperty<>();
    private ObjectProperty<Long> absenceCount = new SimpleObjectProperty<>();
    private ObjectProperty<Long> lateCount = new SimpleObjectProperty<>();
    private ObjectProperty<Long> excusedAbsenceCount = new SimpleObjectProperty<>();

    public WrappedStudentWithPresence(Student student) {
        super(student);
    }

    public void update(WrappingAccessor accessor, WrappedLesson lesson) {
        super.update();

        attendance = lesson == null ? null :
                accessor.wqueryAttendance(this.getObject(), lesson.getObject());
    }

    public void setAttendance(WrappingAccessor accessor, Date from, Date to) {
        attendanceList = accessor.wqueryPresenceWithDate(getObject(), Attendance.AttendanceValue.NIEOBECNOSC, from, to);

        presenceCount.setValue(accessor.queryPresenceCount(this.getObject(), Attendance.AttendanceValue.OBECNOSC));
        absenceCount.set(accessor.queryPresenceCount(this.getObject(), Attendance.AttendanceValue.NIEOBECNOSC));
        lateCount.set(accessor.queryPresenceCount(this.getObject(), Attendance.AttendanceValue.SPOZNIENIE));
        excusedAbsenceCount.set(accessor.queryPresenceCount(this.getObject(), Attendance.AttendanceValue.NIEOBECNOSC_USPRAWIEDLIWIONA));
    }

    public final WrappedAttendance getAttendance() {
        return attendance;
    }

    public final StringProperty attendanceProperty() {
        return attendance == null ? null : attendance.signProperty();
    }

    public ObjectProperty<Long> presenceCountProperty() {
        return presenceCount;
    }

    public ObjectProperty<Long> absenceCountProperty() {
        return absenceCount;
    }

    public ObjectProperty<Long> lateCountProperty() {
        return lateCount;
    }

    public ObjectProperty<Long> excusedAbsenceCountProperty() {
        return excusedAbsenceCount;
    }


    public List<WrappedAttendance> getAbsence() {
        return this.attendanceList;
    }
}
