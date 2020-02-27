package pl.edu.agh.jdziennik.wrapper;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.edu.agh.jdziennik.model.Grade;
import pl.edu.agh.jdziennik.model.Student;
import pl.edu.agh.jdziennik.model.Subject;
import pl.edu.agh.jdziennik.model.dbaccess.DBAccessor;

import java.util.List;

public class WrappedStudentWithSubject extends WrappedStudent {
    private final StringProperty gradesProperty;
    private final StringProperty avgGradeProperty;
    private Subject subject;
    private List<Grade> grades;

    public WrappedStudentWithSubject(Student student) {
        super(student);

        gradesProperty = new SimpleStringProperty("");
        avgGradeProperty = new SimpleStringProperty("-");
    }

    public void update(DBAccessor accessor) {
        super.update();

        String gradesString = "";

        if (subject == null) {
            if (grades != null) grades.clear();
            avgGradeProperty.set("-");
        } else {
            float avgGrade = accessor.queryAverageGradeOfStudent(getObject(), subject);
            String avgGradeStr = avgGrade == 0.0f ? "-" : String.format("%.2f", avgGrade + 1e-3);
            avgGradeProperty.set(avgGradeStr);

            grades = accessor.queryStudentGrades(getObject(), subject);

            for (Grade g : grades) {
                gradesString += "   " + g.getGradeValue().stringValue;
            }
        }

        gradesProperty.set(gradesString);
    }

    public StringProperty gradesProperty() {
        return gradesProperty;
    }

    public StringProperty avgGradeProperty() {
        return avgGradeProperty;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
} 
