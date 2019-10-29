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

create sequence LogSeq;


create table AuditLog (
    id bigint default LogSeq.nextval,
    user VARCHAR not null,
    operation VARCHAR not null,
    operationDate TIMESTAMP not null,
    constraint AutidLog_PK primary key (id)
);