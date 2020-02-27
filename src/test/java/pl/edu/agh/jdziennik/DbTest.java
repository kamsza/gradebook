package pl.edu.agh.jdziennik;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.jdziennik.model.*;

import java.sql.Date;
import java.sql.Time;

import static org.junit.Assert.*;

public class DbTest {
    Session session = null;

    @Before
    public void before() {
        SessionFactory sessionFactory = new Configuration().configure("hibernate/test_hibernate.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
    }

    @After
    public void after() {
        session.close();
    }

    @Test
    public void newObjectsTest() {
        Teacher teacher = new Teacher("Anna", "Nowak");

        TeachingClass teachingClass = new TeachingClass("3A", teacher);

        Parent parent = new Parent("Anna", "Kowalska", "123456789");

        Student student = new Student(teachingClass, "Damian", "Kowalski", "98081687654", parent);

        Subject subject = new Subject("Math");

        Classroom classroom = new Classroom("202A");

        ClassSubject classSubject = new ClassSubject(teachingClass, subject, teacher);

	Time now = new Time(System.currentTimeMillis());
        TimeWindow nowWindow = new TimeWindow(1, now, now);
        WeekTerm weekTerm = new WeekTerm(WeekTerm.Day.CZWARTEK, nowWindow, classSubject, classroom);

        Lesson lesson =  new Lesson(weekTerm, "Dzielenie wielomianów");

        GradeType gradeType = new GradeType(teacher, "Aktywność pozalekcyjna", 5);

        Grade grade = new Grade(student, classSubject, Grade.GradeValue.BARDZO_DOBRY, gradeType, "");

        Assignment assignment = new Assignment(Assignment.AssignmentType.SPRAWDZIAN,
                new Date(System.currentTimeMillis()), "", weekTerm);

        session.save(teacher);
        session.save(teachingClass);
        session.save(parent);
        session.save(student);
        session.save(subject);
        session.save(classroom);
        session.save(classSubject);
	    session.save(nowWindow);
        session.save(weekTerm);
        session.save(lesson);
        session.save(gradeType);
        session.save(grade);
        session.save(assignment);

        session.contains(teacher);
        session.contains(teachingClass);
        session.contains(parent);
        session.contains(student);
        session.contains(subject);
        session.contains(classroom);
        session.contains(classSubject);
        session.contains(weekTerm);
        session.contains(lesson);
        session.contains(gradeType);
        session.contains(grade);
        session.contains(assignment);

        assertTrue(teacher.getId() > 0);
        assertTrue(student.getId() > 0);
        assertTrue(parent.getId() > 0);
        assertTrue(assignment.getId() > 0);
        assertTrue(classSubject.getId().getSubject() != null);
        assertTrue(classSubject.getId().getTeachingClass() != null);
        assertTrue(weekTerm.getId() > 0);
        assertTrue(grade.getId() > 0);

        Teacher teacherDB = session.get(Teacher.class, teacher.getId());
        Student studentDB = session.get(Student.class, student.getId());
        ClassSubject classSubjectDB = session.get(ClassSubject.class, classSubject.getId());
        WeekTerm weekTermDB = session.get(WeekTerm.class, weekTerm.getId());
        Grade gradeDB = session.get(Grade.class, grade.getId());
        Parent parentDB = session.get(Parent.class, parent.getId());
        Assignment assignmentDB = session.get(Assignment.class, assignment.getId());
        GradeType gradeTypeDB = session.get(GradeType.class, gradeType.getId());

        assertEquals(teacher, teacherDB);
        assertEquals(student, studentDB);
        assertEquals(classSubject, classSubjectDB);
        assertEquals(weekTerm, weekTermDB);
        assertEquals(grade, gradeDB);
        assertEquals(parent, parentDB);
        assertEquals(assignment, assignmentDB);
        assertEquals(gradeType, gradeTypeDB);
    }

    @Test
    public void studentTest() {
        Teacher teacher = new Teacher("Anna", "Nowak");
        TeachingClass teachingClass = new TeachingClass("3A", teacher);
        teacher.addClass(teachingClass);

        Parent parent = new Parent("Anna", "Kowalska", "123456778");
        session.save(parent);
        Student student = new Student(teachingClass, "Damian", "Kowalski", "12345678901", parent);
        Parent parent2 = new Parent("Kamil", "Kowalski", "123432123");
        session.save(parent2);
        Student student2 = new Student(teachingClass, "Karol", "Kowalski", "09876543210", parent2);

        session.save(teacher);
        session.save(teachingClass);
        session.save(student);
        session.save(student2);

        Parent parent3 = new Parent("Marian", "Kowalski", "987678987");
        session.save(parent3);
        Student student3 = new Student(teachingClass,"Damian", "Kowalski", "98475635243", parent3);
        session.save(student3);

        assertTrue(student.getId() >= 0);
        assertTrue(student2.getId() >= 0);
        assertNotEquals(student.getId(), student2.getId());
        assertNotEquals(student.getId(), student3.getId());

        assertTrue(student.equals(student));
        assertFalse(student.equals(student2));
        assertFalse(student.equals(student3));

        session.contains(student);
        session.contains(student2);
        session.contains(student3);
        session.contains(parent);
        session.contains(parent2);
        session.contains(parent3);
        session.contains(teacher);
        session.contains(teachingClass);
    }
}
