package pl.edu.agh.jdziennik.wrapper;

import pl.edu.agh.jdziennik.model.GradeType;

public class WrappedGradeType extends AbstractWrapper<GradeType> {

    public WrappedGradeType(GradeType gradeType) {
        super(gradeType);
    }

    @Override
    public String toString() {
        return getObject().getTypeName();
    }
}
