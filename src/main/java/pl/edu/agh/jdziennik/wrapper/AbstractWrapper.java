package pl.edu.agh.jdziennik.wrapper;

public abstract class AbstractWrapper<T> {
    private T object;

    public AbstractWrapper(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    @Override
    public boolean equals(Object other) {
        return other != null &&
                other instanceof AbstractWrapper &&
                ((AbstractWrapper<?>) other).getObject().equals(object);
    }
}
