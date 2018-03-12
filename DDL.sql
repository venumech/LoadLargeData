--Table used to store the server log data

--create database, venu
create database venu;

--Drop the table
 --drop table SERVER_LOG;


create table SERVER_LOG
       ( incident_date timestamp (6),
	 ipAddress varchar(20),
	 method varchar(20),
	 status int,
	 comments varchar(200),
         PRIMARY KEY (incident_date , ipAddress )
       );

