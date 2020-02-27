package pl.edu.agh.jdziennik.wrapper;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.edu.agh.jdziennik.model.Student;

public class WrappedStudent extends AbstractWrapper<Student> {
    private final StringProperty nameProperty;
    private final StringProperty surnameProperty;

    public WrappedStudent(Student student) {
        super(student);

        nameProperty = new SimpleStringProperty(student.getName());
        surnameProperty = new SimpleStringProperty(student.getSurname());
    }

    public void update() {
        nameProperty.set(getObject().getName());
        surnameProperty.set(getObject().getSurname());
    }

    public final StringProperty nameProperty() {
        return nameProperty;
    }

    public final StringProperty surnameProperty() {
        return surnameProperty;
    }

    @Override
    public String toString() {
        return nameProperty.get() + " " + surnameProperty.get();
    }
} 
