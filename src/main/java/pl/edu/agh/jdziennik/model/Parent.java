package pl.edu.agh.jdziennik.model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a parent. This class is mapped by hibernate.
 */
@Entity
public class Parent extends Person {

    private String phoneNumber;

    @OneToMany(mappedBy = "parent")
    private List<Student> childList = new LinkedList<>();

    /**
     * for hibernate
     */
    Parent() {
        super();
    }

    /**
     * Creates new Parent using values passed. The newly created
     * object does not have id set.
     */
    public Parent(final String name, final String surname,
                  final String phoneNumber) {
        super(name, surname);
        this.phoneNumber = phoneNumber;
    }

    /**
     * Adds student to list of children of this parent.
     * Does not set the reference to parent in student.
     */
    void addChild(Student student) {
        childList.add(student);
    }

    public List<Student> getChildList() {
        return childList;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Checks if parents are equal using id, name and surname.
     */
    @Override
    public boolean equals(final Object other) {
        return super.equals(other) && other instanceof Parent;
    }
}
