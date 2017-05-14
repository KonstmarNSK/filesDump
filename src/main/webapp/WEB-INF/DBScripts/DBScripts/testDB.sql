
create table users(
	id int not null primary key,
    email varchar(150) not null,
    password varchar(150) not null
);

create table authorities(
	id int not null primary key,
    auth varchar(50) not null,
    
    user_id int not null
);