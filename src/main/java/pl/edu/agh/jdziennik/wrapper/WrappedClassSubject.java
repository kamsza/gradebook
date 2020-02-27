package pl.edu.agh.jdziennik.wrapper;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.edu.agh.jdziennik.model.ClassSubject;

public class WrappedClassSubject
        extends AbstractWrapper<ClassSubject> {
    private final StringProperty classIdProperty;
    private final StringProperty subjectNameProperty;
    private final StringProperty teacherNameProperty;

    public WrappedClassSubject(ClassSubject classSubject) {
        super(classSubject);

        classIdProperty = new SimpleStringProperty
                (classSubject.getTeachingClass().getId());
        subjectNameProperty = new SimpleStringProperty
                (classSubject.getSubject().getName());
        teacherNameProperty = new SimpleStringProperty
                (classSubject.getTeacher().getName() + " " + classSubject.getTeacher().getSurname());
    }

    public void update() {
        classIdProperty.set
                (getObject().getTeachingClass().getId());
        subjectNameProperty.set
                (getObject().getSubject().getName());
        teacherNameProperty.set
                (getObject().getTeacher().getName() + " " + getObject().getTeacher().getSurname());
    }

    public final StringProperty classIdProperty() {
        return classIdProperty;
    }

    public final StringProperty subjectNameProperty() {
        return subjectNameProperty;
    }

    public final StringProperty teacherNameProperty() {
        return teacherNameProperty;
    }
} 
