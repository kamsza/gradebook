package pl.edu.agh.jdziennik.wrapper;

import pl.edu.agh.jdziennik.model.Parent;

public class WrappedParent extends AbstractWrapper<Parent> {
    public WrappedParent(Parent parent) {
        super(parent);
    }

    @Override
    public String toString() {
        return getObject().getName() + " " + getObject().getSurname();
    }
} 
