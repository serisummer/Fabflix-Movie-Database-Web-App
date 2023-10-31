create database moviedb;
use moviedb;
create table movies
(
        id varchar(10) primary key,
        title varchar(100) not null,
        year integer not null,
        director varchar(100) not null
);

create table stars
(
    id varchar(10) primary key,
    name varchar(100) not null,
    birthYear integer default null
);

create table stars_in_movies
(
    starId varchar(10) not null references stars(id),
    movieId varchar(10) not null references movies(id)
);

create table genres
(
    id integer auto_increment primary key,
    name varchar(32) not null
);

create table genres_in_movies
(
    genreId integer not null references genres(id) ,
    movieId varchar(10) not null references movies(id)
);

create table customers
(
    id integer auto_increment primary key,
    firstName varchar(50) not null,
    lastName varchar(50) not null,
    ccId varchar(20) not null references creditcards(id),
    address varchar(200) not null,
    email varchar(50) not null,
    password varchar(20) not null
);

create table sales
(
    id integer auto_increment primary key,
    customerId integer not null references customers(id),
    movieId varchar(10) not null references movies(id),
    saleDate date not null
);

create table creditcards
(
    id varchar(20) primary key,
    firstName varchar(50) not null,
    lastName varchar(50) not null,
    expiration date not null
);

create table ratings
(
    movieId varchar(10) references movies(id),
    rating float not null,
    numVotes integer not null
);