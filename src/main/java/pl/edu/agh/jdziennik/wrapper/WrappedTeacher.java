package pl.edu.agh.jdziennik.wrapper;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.edu.agh.jdziennik.model.Teacher;

public class WrappedTeacher extends AbstractWrapper<Teacher> {
    private final StringProperty nameProperty;
    private final StringProperty surnameProperty;

    public WrappedTeacher(Teacher teacher) {
        super(teacher);
        nameProperty = new SimpleStringProperty(teacher.getName());
        surnameProperty = new SimpleStringProperty(teacher.getSurname());
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
        return surnameProperty.get() + " " + nameProperty.get();
    }
} 
