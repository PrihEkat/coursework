CREATE TABLE counterparty
(
	id serial not null
		primary key,
	name varchar(20),
	inn varchar(20),
	kpp varchar(20),
	account_number varchar(30),
	bik_bank varchar(20)
);