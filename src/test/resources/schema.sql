drop table if exists bookings;
drop table if exists timeslots;
drop table if exists subjects;
drop table if exists students;

create table students(
    register_number int primary key,
    name varchar(50) not null,
    surname varchar(50) not null
);

create table timeslots(
    start_time varchar(50),
    end_time varchar(50),
    primary key (start_time, end_time)
);

create table subjects(
    name varchar(50) primary key
);

create table bookings(
    student_register_number int,
    subject_name varchar(50),
    start_time varchar(50),
    end_time varchar(50),
    primary key(subject_name, start_time, end_time),
    foreign key(student_register_number) references students(register_number),
    foreign key(subject_name) references subjects(name),
    foreign key(start_time, end_time) references timeslots(start_time, end_time)
);