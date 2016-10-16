package com.cop290.ashwattha.assn1.moodle;

import java.util.ArrayList;

/**
 * Created by Vikas on 19-02-2016.
 */
class User {
    String first_name;
    String last_name;
    String email;
    String userid;
    String entry_no;
    String password;
    String registration_key ;
    String reset_password_key;
    String registration_id;
    String type_;
}
class Courses{
    String current_sem;
    String current_year;
    ArrayList<Course> courses = new ArrayList<Course>();
}
class Course{
    String code;
    String name;
    String credits;
    String description;
    String id;
    String l_t_p;
}
class Assignmentcourse{
    ArrayList<Assignment> assignments = new ArrayList<Assignment>();
    Course course ;
}
class Assignment{
    String name ;
    String description;
    String deadline;
    String file_ ;
    String late_days_allowed;
    String id;
    String created_at;
}
class Threadcourse{
    ArrayList<Thread> course_threads= new ArrayList<Thread>();
    Course course ;
}
class Thread{
    String title ;
    String description;
    String registered_course_id;
    String user_id ;
    String updated_at;
    String id;
    String created_at;

}
class ThreadDetail{
    Course course;
    Thread thread;
    ArrayList<Threadcomment> comments = new ArrayList<Threadcomment>();
    ArrayList<User> comment_users = new ArrayList<User>();
    ArrayList<String> time_readable = new ArrayList<String>();
}
class Threadcomment{
  String userid;
  String description;
  String created_at;
    String thread_id;
    String id;
}

class GradeCourse{
    ArrayList<Grade> grades= new ArrayList<Grade>();
    Course course ;

}
class Grade{
    String user_id;
    String weightage;
    String name;
    String out_of;
    String registered_course_id;
    String score;
    String id;
}
class AllGrades {
    ArrayList<Course> courses=new ArrayList<Course>();
    ArrayList<Grade> grades= new ArrayList<Grade>();
}
class AssignmentDetail {
    Assignment assignment ;
    String submissions;
    Course course;

}
class Notifications{
    ArrayList<Notification>  notifications = new ArrayList<Notification>();
}
class Notification{
    String user_id;
    String description;
    String is_seen;
    String created_at;
    String id;
}