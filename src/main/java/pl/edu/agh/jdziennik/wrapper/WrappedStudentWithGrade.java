package pl.edu.agh.jdziennik.wrapper;

import pl.edu.agh.jdziennik.model.Student;

public class WrappedStudentWithGrade extends WrappedStudent {
    private WrappedGradeValue gradeValue;
    private WrappedSubject subject;
    private WrappedClass tClass;

    public WrappedStudentWithGrade(Student student) {
        super(student);
    }

    public WrappedClass getTeachingClass() {
        return this.tClass;
    }

    public void setTeachingClass(WrappedClass teachingClass) {
        this.tClass = teachingClass;
    }

    public WrappedGradeValue getGradeValue() {
        return this.gradeValue;
    }

    public void setGradeValue(WrappedGradeValue gradeValue) {
        this.gradeValue = gradeValue;
    }

    public WrappedSubject getSubject() {
        return this.subject;
    }

    public void setSubject(WrappedSubject subject) {
        this.subject = subject;
    }
}
