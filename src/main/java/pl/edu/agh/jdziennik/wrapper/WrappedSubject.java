package pl.edu.agh.jdziennik.wrapper;

import pl.edu.agh.jdziennik.model.Subject;

public class WrappedSubject extends AbstractWrapper<Subject> {
    public WrappedSubject(Subject subject) {
        super(subject);
    }

    @Override
    public String toString() {
        return getObject().getName();
    }
}
