# HS-Harz_Datenbanken2

Tutorials zu JavaServlets, JSP und JDBC:
- https://o7planning.org/en/10285/create-a-simple-java-web-application-using-servlet-jsp-and-jdbc
  - JavaServlets: https://o7planning.org/en/10169/java-servlet-tutorial
  - JSP: https://o7planning.org/en/10263/java-jsp-tutorial
  - JDBC: https://o7planning.org/en/10167/java-jdbc-tutorial


Partitioning:
- https://docs.oracle.com/en/database/oracle/oracle-database/12.2/vldbg/partition-concepts.html#GUID-256BA7EE-BF49-42DE-9B38-CD2480A73129


Tipps & Tricks:
- Aufspalten von sysdate:
  - select to_char(sysdate, 'HH24') from dual; -- Stunde
  - select to_char(sysdate, 'MI') from dual; -- Minute
  - select to_char(sysdate, 'MM') from dual; -- Monat
  - select to_char(sysdate, 'YYYY') from dual; -- Jahr
  - select to_char(sysdate, 'DAY') from dual; -- Wochentag


Analysen:
- https://canvasjs.com/
