create sequence GlobalStonesSeq;

create table  Stones
(
	id bigint not null, -- flaw in JOOQ or H2 (no returning works_
	name varchar not null,
	color varchar not null,
	size integer not null,
	constraint Stones_pk
		primary key (ID)
);

create unique INDEX STONES_ID_UINDEX
	on Stones (ID);

create sequence GlobalVotesSeq;

create table Votes
(
    id bigint not null,
    stone_id bigint not null,
    voter varchar not null,
    constraint Votes_pk
        primary key (id),
    foreign key (stone_id) references Stones(id),
    constraint single_vote unique (stone_id, voter)

);

CREATE TABLE AuditLogs (
    id BIGINT not null,
    user VARCHAR not null,
    operation VARCHAR not null,
    operationDate TIMESTAMP not null,
    constraint AutidLog_PK primary key (id)
);
