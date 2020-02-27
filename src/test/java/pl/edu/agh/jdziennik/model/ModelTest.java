package pl.edu.agh.jdziennik.model;

import org.junit.Test;
import org.mockito.internal.util.reflection.FieldSetter;
import pl.edu.agh.jdziennik.App;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ModelTest {

    @Test
    public void studentTest() {
        TeachingClass teachingClass = mock(TeachingClass.class);
        Parent parent = mock(Parent.class);

        Student student1 = new Student(teachingClass, "Jan", "Kowalski", "98081687654", parent);

        assertNotNull(student1.getName());
        assertNotNull(student1.getSurname());
        assertTrue(student1.equals(student1));

        TeachingClass teachingClass2 = mock(TeachingClass.class);
        student1.changeClass(teachingClass2);
        assertEquals(student1.getTeachingClass(), teachingClass2);

        Grade grade = mock(Grade.class);
        student1.addGrade(grade);
        assertEquals(student1.getGradeList().iterator().next(), grade);
        student1.removeGrade(grade);
        assertTrue(student1.getGradeList().isEmpty());
    }

    @Test
    public void attendanceTest() {
        Student student = new Student(mock(TeachingClass.class), "Adrian", "Nowak", "12432252138", mock(Parent.class));
        Lesson lesson  = new Lesson(mock(WeekTerm.class), "AAA");

        Attendance attendance =
	    new Attendance(lesson, student,
			   Attendance.AttendanceValue.OBECNOSC);

        assertFalse(lesson.getAttendanceList().isEmpty());
        assertFalse(student.getAttendanceList().isEmpty());

        assertEquals(lesson.getAttendanceList(), student.getAttendanceList());
        assertFalse(student.getAttendanceList().iterator().next().getStudent() == null);
        assertFalse(lesson.getAttendanceList().iterator().next().getStudent() == null);
        assertFalse(student.getAttendanceList().iterator().next().getLesson() == null);
        assertFalse(lesson.getAttendanceList().iterator().next().getLesson() == null);
        assertEquals(student.getAttendanceList().iterator().next().getAttendanceValue(), Attendance.AttendanceValue.OBECNOSC);
        assertEquals(lesson.getAttendanceList().iterator().next().getAttendanceValue(), Attendance.AttendanceValue.OBECNOSC);

        assertEquals(attendance.getLesson(), lesson);
        assertEquals(attendance.getStudent(), student);
        assertEquals(attendance.getAttendanceValue(), Attendance.AttendanceValue.OBECNOSC);
}


    @Test
    public void classroomTest() {
        Classroom classroom1 = new Classroom("biology classroom");

        assertEquals(classroom1.getId(), "biology classroom");

        assertTrue(classroom1.equals(classroom1));

        WeekTerm weekTerm = mock(WeekTerm.class);
        classroom1.addWeekTerm(weekTerm);
        assertEquals(classroom1.getWeekTermList().iterator().next(), weekTerm);
        classroom1.removeWeekTerm(weekTerm);
        assertTrue(classroom1.getWeekTermList().isEmpty());
    }


    @Test
    public void classSubjectTest() {
        TeachingClass teachingClass = mock(TeachingClass.class);
        Subject subject = mock(Subject.class);
        Teacher teacher = mock(Teacher.class);

	when(subject.getName()).thenReturn("Fizyka");
	when(teachingClass.getId()).thenReturn("3c");
	
        ClassSubject classSubject = new ClassSubject(teachingClass, subject, teacher);

        assertTrue(classSubject.equals(classSubject));

        assertEquals(classSubject.getTeachingClass(), teachingClass);
        assertEquals(classSubject.getTeacher(), teacher);
        assertEquals(classSubject.getSubject(), subject);

        Teacher teacher2 = mock(Teacher.class);
        classSubject.changeTeacher(teacher2);
        assertEquals(classSubject.getTeacher(), teacher2);

        WeekTerm weekTerm = mock(WeekTerm.class);
        classSubject.addWeekTerm(weekTerm);
        assertEquals(classSubject.getWeekTermList().iterator().next(), weekTerm);
        classSubject.removeWeekTerm(weekTerm);
        assertTrue(classSubject.getWeekTermList().isEmpty());
	
        assertTrue(classSubject.getGradeList().isEmpty());
        Grade grade = mock(Grade.class);
        classSubject.addGrade(grade);
        assertEquals(classSubject.getGradeList().iterator().next(), grade);
    }

    @Test
    public void gradeTest() {
        Student student = mock(Student.class);
        ClassSubject classSubject = mock(ClassSubject.class);
        GradeType gradeType = mock(GradeType.class);
        Grade.GradeValue gradeValue = Grade.GradeValue.BARDZO_DOBRY;
        String comment = "";

        Grade grade = new Grade(student, classSubject, gradeValue, gradeType, comment);

        assertEquals(grade.getStudent(), student);
        assertEquals(grade.getClassSubject(), classSubject);
        assertEquals(grade.getGradeValue(), gradeValue);
        assertEquals(grade.getType(), gradeType.getTypeName());
        assertEquals(grade.getComment(), comment);
    }

    @Test
    public void lessonTest() {
        WeekTerm weekTerm = mock(WeekTerm.class);
        String topic = "java tests";

        Lesson lesson = new Lesson(weekTerm, topic);


        assertEquals(lesson.getWeekTerm(), weekTerm);
        assertEquals(lesson.getTopic(), topic);
        assertTrue(lesson.equals(lesson));

        Attendance attendance = mock(Attendance.class);
        assertTrue(lesson.getAttendanceList().isEmpty());

	lesson.addAttendance(attendance);
        assertEquals(lesson.getAttendanceList().iterator().next(), attendance);
    }


    @Test
    public void subjectTest() {
        ClassSubject classSubject = mock(ClassSubject.class);

        Subject subject = new Subject("TO2");

        assertEquals(subject.getName(), "TO2");
        assertTrue(subject.equals(subject));

        subject.addClassSubject(classSubject);
        assertEquals(subject.getClassSubjectList().iterator().next(), classSubject);
        subject.removeClassSubject(classSubject);
        assertTrue(subject.getClassSubjectList().isEmpty());
    }

    @Test
    public void teacherTest() {
        TeachingClass teachingClass = mock(TeachingClass.class);
        ClassSubject classSubject = mock(ClassSubject.class);

        Teacher teacher = new Teacher("Jan", "Kowalski");
        assertEquals(teacher.getName(), "Jan");
        assertEquals(teacher.getSurname(), "Kowalski");

        assertTrue(teacher.equals(teacher));

        teacher.addClass(teachingClass);
        assertEquals(teacher.getClassList().iterator().next(), teachingClass);
        teacher.removeClass(teachingClass);
        assertTrue(teacher.getClassList().isEmpty());

        teacher.addClassSubject(classSubject);
        assertEquals(teacher.getClassSubjectList().iterator().next(), classSubject);
        teacher.removeClassSubject(classSubject);
        assertTrue(teacher.getClassSubjectList().isEmpty());
    }

    @Test
    public void teachingClassTest() {
        Teacher teacher = mock(Teacher.class);
        Student student = mock(Student.class);
        ClassSubject classSubject = mock(ClassSubject.class);

        TeachingClass teachingClass = new TeachingClass("3A", teacher);

        assertEquals(teachingClass.getTutor(), teacher);
        assertTrue(teachingClass.equals(teachingClass));
        assertFalse(teachingClass.equals(new TeachingClass("3B", teacher)));

        teachingClass.addClassSubject(classSubject);
        assertEquals(teachingClass.getClassSubjectList().iterator().next(), classSubject);
        teachingClass.removeClassSubject(classSubject);
        assertTrue(teachingClass.getClassSubjectList().isEmpty());

        teachingClass.addStudent(student);
        assertEquals(teachingClass.getStudentList().iterator().next(), student);
        teachingClass.removeStudent(student);
        assertTrue(teachingClass.getStudentList().isEmpty());
    }

    @Test
    public void weekTermTest() {
        ClassSubject classSubject = mock(ClassSubject.class);
        Classroom classroom = mock(Classroom.class);
        Lesson lesson = mock(Lesson.class);

        when(classSubject.getTeachingClass()).thenReturn(mock(TeachingClass.class));

        Time now = new Time(System.currentTimeMillis());
        TimeWindow nowWindow = new TimeWindow(1, now, now);
	
        WeekTerm weekTerm = new WeekTerm(WeekTerm.Day.CZWARTEK, nowWindow, classSubject, classroom);

        assertTrue(weekTerm.equals(weekTerm));
        assertTrue(weekTerm.equals(new WeekTerm(WeekTerm.Day.CZWARTEK, nowWindow, classSubject, classroom)));
        assertFalse(weekTerm.equals(new WeekTerm(WeekTerm.Day.PIATEK, nowWindow, classSubject, classroom)));

        assertEquals(weekTerm.getDay(), WeekTerm.Day.CZWARTEK);
        assertEquals(weekTerm.getTimeWindow(), nowWindow);
        assertEquals(weekTerm.getClassSubject(), classSubject);
        assertEquals(weekTerm.getClassroom(), classroom);

        weekTerm.addLesson(lesson);
        assertEquals(weekTerm.getLessonList().iterator().next(), lesson);
        weekTerm.removeLesson(lesson);
        assertTrue(weekTerm.getLessonList().isEmpty());

        Classroom classroom2 = mock(Classroom.class);
        weekTerm.changeClassroom(classroom2);
        assertEquals(weekTerm.getClassroom(), classroom2);
    }
}
