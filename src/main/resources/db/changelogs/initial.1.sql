create sequence StonesSeq;

create table STONES
(
	ID BIGINT default StonesSeq.nextval,
	NAME VARCHAR not null,
	PRICE DECIMAL,
	constraint STONES_PK
		primary key (ID)
);

create unique index STONES_ID_UINDEX
	on STONES (ID);

