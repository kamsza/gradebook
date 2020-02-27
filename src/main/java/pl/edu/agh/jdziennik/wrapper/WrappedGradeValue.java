package pl.edu.agh.jdziennik.wrapper;

import pl.edu.agh.jdziennik.model.Grade;

import java.util.LinkedList;
import java.util.List;

public class WrappedGradeValue extends AbstractWrapper<Grade.GradeValue> {

    public WrappedGradeValue(Grade.GradeValue gradeValue) {
        super(gradeValue);
    }

    public static List<WrappedGradeValue> values() {
        List<WrappedGradeValue> values = new LinkedList<>();

        for (Grade.GradeValue value : Grade.GradeValue.values())
            values.add(new WrappedGradeValue(value));

        return values;
    }

    public static WrappedGradeValue fromString(String string) {
        for (Grade.GradeValue value : Grade.GradeValue.values()) {
            if (value.stringValue.equals(string))
                return new WrappedGradeValue(value);
        }

        return null;
    }

    @Override
    public String toString() {
        return getObject().stringValue;
    }
}
