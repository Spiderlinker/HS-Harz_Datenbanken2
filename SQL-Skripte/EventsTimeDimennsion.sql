SELECT to_char(to_date('07.11.19','DD.MM.YY'),'J') from dual;

DROP SEQUENCE julianSeq;
CREATE SEQUENCE julianSeq START WITH 2458300 INCREMENT BY 1;

DROP TABLE times;

CREATE TABLE times (
JulianID NUMBER PRIMARY KEY,
"Date" DATE);

INSERT INTO times (SELECT julianSeq.nextval, null from Probe);

UPDATE times set "Date" = to_date(JulianID, 'J');

ALTER TABLE times ADD ("Month" VARCHAR2(10));
ALTER TABLE times ADD ("Month_Short" CHAR(3));
ALTER TABLE times ADD ("Month_Number" NUMBER(2));
ALTER TABLE times ADD ("Day_of_Week" VARCHAR2(10));
ALTER TABLE times ADD ("Day_Short" VARCHAR2(10));
ALTER TABLE times ADD ("Year" NUMBER(4));
ALTER TABLE times ADD ("Quarter" NUMBER(1));
ALTER TABLE times ADD ("Quarter_Year" VARCHAR2(6));
ALTER TABLE times ADD ("Week_number" NUMBER(2));
ALTER TABLE times ADD ("Day_in_Year" VARCHAR2(10));
ALTER TABLE times ADD ("Day_in_Month" NUMBER(2));
ALTER TABLE times ADD ("Day_in_Week" NUMBER(1));
ALTER TABLE times ADD ("Week_of_Month" NUMBER(2));
ALTER TABLE times ADD ("Month_Year" VARCHAR2(10));

UPDATE times set "Month" = to_char("Date", 'Month');
UPDATE times set "Month_Short"  = to_char("Date", 'Mon');
UPDATE times set "Month_Number" = to_char("Date", 'MM');
UPDATE times set "Day_of_Week" = to_char("Date", 'Day');
UPDATE times set "Day_Short" = to_char("Date", 'DY');
UPDATE times set "Year" = to_char("Date",'YYYY');
UPDATE times set "Quarter" = to_char("Date",'Q');
UPDATE times set "Quarter_Year" = to_char("Date",'Q.YYYY');
UPDATE times set "Week_number" = to_char("Date",'IW');
UPDATE times set "Day_in_Year" = to_char("Date",'DDD');
UPDATE times set "Day_in_Month" = to_char("Date",'DD');
UPDATE times set "Day_in_Week" = to_char("Date",'D');
UPDATE times set "Week_of_Month" = to_char("Date",'W');
UPDATE times set "Month_Year" = to_char("Date",'MM.YYYY');

DROP TABLE "HOUR";
CREATE TABLE "HOUR"("HOUR" NUMBER);

INSERT INTO "HOUR" values(0);
INSERT INTO "HOUR" values(1);
INSERT INTO "HOUR" values(2);
INSERT INTO "HOUR" values(3);
INSERT INTO "HOUR" values(4);
INSERT INTO "HOUR" values(5);
INSERT INTO "HOUR" values(6);
INSERT INTO "HOUR" values(7);
INSERT INTO "HOUR" values(8);
INSERT INTO "HOUR" values(9);
INSERT INTO "HOUR" values(10);
INSERT INTO "HOUR" values(11);
INSERT INTO "HOUR" values(12);
INSERT INTO "HOUR" values(13);
INSERT INTO "HOUR" values(14);
INSERT INTO "HOUR" values(15);
INSERT INTO "HOUR" values(16);
INSERT INTO "HOUR" values(17);
INSERT INTO "HOUR" values(18);
INSERT INTO "HOUR" values(19);
INSERT INTO "HOUR" values(20);
INSERT INTO "HOUR" values(21);
INSERT INTO "HOUR" values(22);
INSERT INTO "HOUR" values(23);
			
commit;

DROP TABLE "MINUTE";
CREATE TABLE "MINUTE" ("Minute" NUMBER);

INSERT INTO "MINUTE" values(0);
INSERT INTO "MINUTE" values(1);
INSERT INTO "MINUTE" values(2);
INSERT INTO "MINUTE" values(3);
INSERT INTO "MINUTE" values(4);
INSERT INTO "MINUTE" values(5);
INSERT INTO "MINUTE" values(6);
INSERT INTO "MINUTE" values(7);
INSERT INTO "MINUTE" values(8);
INSERT INTO "MINUTE" values(9);
INSERT INTO "MINUTE" values(10);
INSERT INTO "MINUTE" values(11);
INSERT INTO "MINUTE" values(12);
INSERT INTO "MINUTE" values(13);
INSERT INTO "MINUTE" values(14);
INSERT INTO "MINUTE" values(15);
INSERT INTO "MINUTE" values(16);
INSERT INTO "MINUTE" values(17);
INSERT INTO "MINUTE" values(18);
INSERT INTO "MINUTE" values(19);
INSERT INTO "MINUTE" values(20);
INSERT INTO "MINUTE" values(21);
INSERT INTO "MINUTE" values(22);
INSERT INTO "MINUTE" values(23);
INSERT INTO "MINUTE" values(24);
INSERT INTO "MINUTE" values(25);
INSERT INTO "MINUTE" values(26);
INSERT INTO "MINUTE" values(27);
INSERT INTO "MINUTE" values(28);
INSERT INTO "MINUTE" values(29);
INSERT INTO "MINUTE" values(30);
INSERT INTO "MINUTE" values(31);
INSERT INTO "MINUTE" values(32);
INSERT INTO "MINUTE" values(33);
INSERT INTO "MINUTE" values(34);
INSERT INTO "MINUTE" values(35);
INSERT INTO "MINUTE" values(36);
INSERT INTO "MINUTE" values(37);
INSERT INTO "MINUTE" values(38);
INSERT INTO "MINUTE" values(39);
INSERT INTO "MINUTE" values(40);
INSERT INTO "MINUTE" values(41);
INSERT INTO "MINUTE" values(42);
INSERT INTO "MINUTE" values(43);
INSERT INTO "MINUTE" values(44);
INSERT INTO "MINUTE" values(45);
INSERT INTO "MINUTE" values(46);
INSERT INTO "MINUTE" values(47);
INSERT INTO "MINUTE" values(48);
INSERT INTO "MINUTE" values(49);
INSERT INTO "MINUTE" values(50);
INSERT INTO "MINUTE" values(51);
INSERT INTO "MINUTE" values(52);
INSERT INTO "MINUTE" values(53);
INSERT INTO "MINUTE" values(54);
INSERT INTO "MINUTE" values(55);
INSERT INTO "MINUTE" values(56);
INSERT INTO "MINUTE" values(57);
INSERT INTO "MINUTE" values(58);
INSERT INTO "MINUTE" values(59);

DROP TABLE TimeTable;
CREATE TABLE TimeTable AS (SELECT * FROM times, "HOUR", "MINUTE");

DROP SEQUENCE standardSeqTime;
CREATE SEQUENCE standardSeqTime start with 1 increment by 1;
ALTER TABLE TimeTable ADD (time_ID NUMBER default standardSeqTime.nextval);
ALTER TABLE TimeTable ADD CONSTRAINT td_pk PRIMARY KEY (time_ID);

CREATE DIMENSION TimeTable_dimension
LEVEL "Minute" is TimeTable."MINUTE"
LEVEL "Hour" is TimeTable."HOUR"
LEVEL "Date" is TimeTable."Date"
LEVEL "Month" is TimeTable."Month"
LEVEL "Quarter" is TimeTable."Quarter"
LEVEL "Year" is TimeTable."Year"
HIERARCHY TimeTable_rollup(
"Minute" CHILD OF
"Hour" CHILD OF
"Date" CHILD OF
"Month" CHILD OF
"Quarter" CHILD OF
"Year");