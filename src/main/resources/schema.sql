DROP TABLE IF EXISTS ITEM_T;
DROP TABLE IF EXISTS USER_T;
DROP TABLE IF EXISTS BOOKING_T;
DROP TABLE IF EXISTS REQUEST_T;
DROP TABLE IF EXISTS COMMENT_T;

CREATE TABLE IF NOT EXISTS USER_T (
	ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	NAME CHARACTER VARYING NOT NULL,
	EMAIL CHARACTER VARYING NOT NULL UNIQUE,
	CONSTRAINT USER_T_PK PRIMARY KEY (ID)
);
CREATE TABLE IF NOT EXISTS REQUEST_T (
	ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	REQUESTOR_ID BIGINT NOT NULL,
	DESCRIPTION CHARACTER VARYING NOT NULL,
	CREATED  TIMESTAMP WITHOUT TIME ZONE,
	CONSTRAINT REQUEST_T_PK PRIMARY KEY (ID)
);
CREATE TABLE IF NOT EXISTS ITEM_T (
	ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	NAME CHARACTER VARYING NOT NULL,
	DESCRIPTION CHARACTER VARYING NOT NULL,
	OWNER_ID BIGINT NOT NULL,
	AVAILABLE BOOLEAN NOT NULL,
	REQUEST_ID BIGINT,
	CONSTRAINT ITEM_T_PK PRIMARY KEY (ID),
	CONSTRAINT ITEM_T_FK FOREIGN KEY (OWNER_ID) REFERENCES USER_T(ID),
	CONSTRAINT ITEM_T_FK_1 FOREIGN KEY (REQUEST_ID) REFERENCES USER_T(ID),
	CONSTRAINT ITEM_T_FK_2 FOREIGN KEY (REQUEST_ID) REFERENCES REQUEST_T(ID)
);


CREATE TABLE IF NOT EXISTS BOOKING_T (
	ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	START_DATE  TIMESTAMP WITHOUT TIME ZONE,
	END_DATE  TIMESTAMP WITHOUT TIME ZONE,
	ITEM_ID BIGINT NOT NULL,
	BOOKER_ID BIGINT NOT NULL,
	STATUS CHARACTER VARYING NOT NULL,
	CONSTRAINT BOOKING_T_PK PRIMARY KEY (ID)
);



CREATE TABLE IF NOT EXISTS COMMENT_T (
	ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	TEXT CHARACTER VARYING NOT NULL,
	ITEM_ID BIGINT NOT NULL,
	AUTHOR_ID BIGINT NOT NULL,
	CREATED  TIMESTAMP WITHOUT TIME ZONE,
	CONSTRAINT COMMENT_T_PK PRIMARY KEY (ID)
);