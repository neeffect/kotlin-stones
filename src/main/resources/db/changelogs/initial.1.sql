create table STONES
(
	ID BIGINT not null,
	NAME VARCHAR not null,
	PRICE DECIMAL,
	constraint STONES_PK
		primary key (ID)
);

create unique index STONES_ID_UINDEX
	on STONES (ID);

