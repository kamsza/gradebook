package pl.edu.agh.jdziennik.wrapper;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import pl.edu.agh.jdziennik.model.Attendance;


public class WrappedAttendanceValue extends AbstractWrapper<Attendance.AttendanceValue> {
    StringProperty sign;

    public WrappedAttendanceValue(Attendance.AttendanceValue attendanceValue) {
        super(attendanceValue);

        sign = attendanceValue == null ? null : new SimpleStringProperty(attendanceValue.getSign());
    }

    /**
     * @return AttendanceValue assigned to button
     */
    public static WrappedAttendanceValue getAttendanceValueFromButton(Button button) {
        if (button.getId().equals("buttonPresent"))
            return new WrappedAttendanceValue(Attendance.AttendanceValue.OBECNOSC);
        if (button.getId().equals("buttonAbsent"))
            return new WrappedAttendanceValue(Attendance.AttendanceValue.NIEOBECNOSC);
        if (button.getId().equals("buttonLate"))
            return new WrappedAttendanceValue(Attendance.AttendanceValue.SPOZNIENIE);
        if (button.getId().equals("buttonExcusedAbsence"))
            return new WrappedAttendanceValue(Attendance.AttendanceValue.NIEOBECNOSC_USPRAWIEDLIWIONA);
        return null;
    }

    /**
     * @return AttendanceValue with value equal to given string
     */
    public static WrappedAttendanceValue getAttendanceValueFromString(String stringValue) {
        Attendance.AttendanceValue[] attendanceValues = Attendance.AttendanceValue.values();

        for (Attendance.AttendanceValue attendanceValue : attendanceValues) {
            if (attendanceValue.name().equals(stringValue))
                return new WrappedAttendanceValue(attendanceValue);
        }

        return null;
    }
}
