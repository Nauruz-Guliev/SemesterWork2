-- create database strategy_game;

drop schema if exists public cascade ;
create schema public;


create table users(
    id serial primary key,
    email varchar(100) unique not null,
    password_hash varchar(100) not null,
    nickname varchar(100) unique not null
);











