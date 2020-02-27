package pl.edu.agh.jdziennik.wrapper;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.edu.agh.jdziennik.model.TeachingClass;

public class WrappedClass extends AbstractWrapper<TeachingClass> {
    private final StringProperty idProperty;
    private final StringProperty tutorNameProperty;

    public WrappedClass(TeachingClass teachingClass) {
        super(teachingClass);

        idProperty = new SimpleStringProperty(teachingClass.getId());
        tutorNameProperty = new SimpleStringProperty
                (teachingClass.getTutor().getName() + " " + teachingClass.getTutor().getSurname());
    }

    public void update() {
        idProperty.set(getObject().getId());
        tutorNameProperty.set(getObject().getTutor().getName() + " " + getObject().getTutor().getSurname());
    }

    public final StringProperty idProperty() {
        return idProperty;
    }

    public final StringProperty tutorNameProperty() {
        return tutorNameProperty;
    }

    @Override
    public String toString() {
        return getObject().getId();
    }
}
