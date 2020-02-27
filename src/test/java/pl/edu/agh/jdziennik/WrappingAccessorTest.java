package pl.edu.agh.jdziennik;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.jdziennik.model.*;
import pl.edu.agh.jdziennik.wrapper.*;
import pl.edu.agh.jdziennik.wrapper.wrappingaccess.WrappingAccessor;

import java.util.List;
import java.sql.Date;
import java.sql.Time;

import static org.junit.Assert.*;

public class WrappingAccessorTest {
    private WrappingAccessor accessor;

    @Before
    public void before() {
        SessionFactory sessionFactory = new Configuration().configure("hibernate/test_hibernate.cfg.xml").buildSessionFactory();
        accessor = new WrappingAccessor(sessionFactory.openSession());
    }

    @After
    public void after() {
        accessor.close();
    }

    @Test
    public void emptyQueriesTest() {
        assertTrue(accessor.wqueryAllClasses().size() == 0);
        assertTrue(accessor.wqueryAllTeachers().size() == 0);
        assertTrue(accessor.wqueryAllParents().size() == 0);
        assertTrue(accessor.wqueryAllStudents().size() == 0);
        assertTrue(accessor.wqueryAllSubjects().size() == 0);
        assertTrue(accessor.wqueryAllClassrooms().size() == 0);
        assertTrue(accessor.wqueryAllTimeWindows().size() == 0);
    }

    private void addTestObjects() {
        new DataGenerator(accessor).generateData();
    }

    @Test
    public void queriesTest() {
        addTestObjects();

        WrappedTeacher exampleTeacher;
        WrappedClass exampleClass;
        WrappedStudent exampleStudent;
        WrappedTimeWindow exampleWindow;
        WrappedClassSubject exampleClassSubject;
        WrappedParent exampleParent;
        WrappedWeekTerm exampleWeekTerm;

        exampleClass = accessor.wqueryAllClasses().get(0);
        assertTrue(accessor.wqueryClassStudentsWithPresence(exampleClass).size() ==
                accessor.wqueryClassStudentsWithSubject(exampleClass.getObject()).size());
        assertTrue(accessor.wqueryClassStudentsWithGrade(exampleClass.getObject()).size() ==
                accessor.wqueryClassStudents(exampleClass).size());

        exampleTeacher = new WrappedTeacher(exampleClass.getObject().getTutor());
        assertTrue(accessor.wqueryClassesTutoredByTeacher(exampleTeacher).contains(exampleClass));

        for (WrappedStudent student : accessor.wqueryClassStudents(exampleClass))
            assertEquals(student.getObject().getTeachingClass(), exampleClass.getObject());

        List<WrappedSubject> subjects = accessor.wquerySubjectsOfTeacher(exampleTeacher);

        for (WrappedSubject subject : accessor.wquerySubjectsOfTeacherAndClass(exampleTeacher, exampleClass)){
            assertTrue(subjects.contains(subject));
        }


        List<WrappedClass> classes = accessor.wqueryClassesOfTeacher(exampleTeacher);

        for (WrappedClassSubject classSubject : accessor.wqueryClassSubjectsOfTeacher(exampleTeacher))
            assertTrue(classes.contains(new WrappedClass(classSubject.getObject().getTeachingClass())));

        for (WrappedClassSubject classSubject : accessor.wqueryClassSubjectsOfClass(exampleClass))
            assertEquals(classSubject.getObject().getTeachingClass(), exampleClass.getObject());

        for (WrappedStudentWithSubject subStudent : accessor.wqueryClassStudentsWithSubject(exampleClass.getObject()))
            assertEquals(accessor.wqueryClassOfStudent(subStudent), exampleClass);

        exampleStudent = accessor.wqueryAllStudents().get(0);
        assertTrue(accessor.wqueryParentOfStudent(exampleStudent).getObject().getChildList().contains(exampleStudent.getObject()));

        exampleWindow = accessor.wqueryAllTimeWindows().get(0);
        assertTrue(accessor.wqueryLesson(new Date(5), new WrappedWeekTerm(exampleWindow.getObject().getWeekTermList().get(0))) == null);

        exampleWeekTerm = new WrappedWeekTerm(exampleWindow.getObject().getWeekTermList().get(0));
        ClassSubject tmpClassSubject = exampleWeekTerm.getObject().getClassSubject();

        assertEquals(accessor.wqueryWeekTerm(new WrappedDay(exampleWeekTerm.getObject().getDay()), exampleWindow, new WrappedTeacher(tmpClassSubject.getTeacher()),
                new WrappedClass(tmpClassSubject.getTeachingClass())),
                exampleWeekTerm);

        exampleParent = accessor.wqueryAllParents().get(0);
        WrappedStudent newStudent = accessor.createStudent(exampleClass, "Arak", "Jurczek", "09876543210", exampleParent);
        assertTrue(newStudent.getObject().getId() != 0);
        assertEquals(newStudent.getObject().getTeachingClass(), exampleClass.getObject());

        WrappedClass newClass = accessor.createClass("5i", exampleTeacher);
        assertEquals(newClass.getObject().getTutor(), exampleTeacher.getObject());

        WrappedParent newParent = accessor.createParent("Janusz", "Noga", "500500500");
        assertTrue(newParent.getObject().getId() != 0);

        WrappedTeacher newTeacher = accessor.createTeacher("Mikołaj", "Kontek");
        assertTrue(newTeacher.getObject().getId() != 0);

        WrappedLesson newLesson = accessor.createLesson(exampleWeekTerm, "");
        assertTrue(newLesson.getObject().getId() != 0);
        assertEquals(newLesson.getObject().getWeekTerm(), exampleWeekTerm.getObject());

        WrappedAttendance newAttendance = accessor.createAttendance(newLesson, newStudent,
                new WrappedAttendanceValue(Attendance.AttendanceValue.OBECNOSC));
        assertTrue(newAttendance.getObject().getId() != 0);
        assertEquals(newAttendance.getObject().getLesson(), newLesson.getObject());

        assertTrue(accessor.wqueryPresenceWithDate(newStudent.getObject(), Attendance.AttendanceValue.OBECNOSC,
                newLesson.getObject().getDate(), newLesson.getObject().getDate())
                .contains(newAttendance));

        Subject tmpSubject = new Subject("widmologia");
        accessor.persistObject(tmpSubject);
        WrappedClassSubject newClassSubject =
                accessor.createClassSubject(exampleClass, new WrappedSubject(tmpSubject), newTeacher);
        assertEquals(newClassSubject.getObject().getTeachingClass(), exampleClass.getObject());
    }
}
