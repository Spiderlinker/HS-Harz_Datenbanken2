INSERT INTO PAYMETHOD VALUES (PAYMETHODIDSEQ.nextval, 'Paypal', 0.35);
INSERT INTO PAYMETHOD VALUES (PAYMETHODIDSEQ.nextval, 'Lastschrift', 0);
INSERT INTO PAYMETHOD VALUES (PAYMETHODIDSEQ.nextval, 'Vorkasse', 0);
INSERT INTO PAYMETHOD VALUES (PAYMETHODIDSEQ.nextval, 'Rechnung', 1.50);
INSERT INTO PAYMETHOD VALUES (PAYMETHODIDSEQ.nextval, 'Kreditkarte', 0.5);
INSERT INTO PAYMETHOD VALUES (PAYMETHODIDSEQ.nextval, 'Nachnahme', 5);
INSERT INTO PAYMETHOD VALUES (PAYMETHODIDSEQ.nextval, 'Sofortueberweisung', 1.20);
INSERT INTO PAYMETHOD VALUES (PAYMETHODIDSEQ.nextval, 'Klarna', 0);
INSERT INTO PAYMETHOD VALUES (PAYMETHODIDSEQ.nextval, 'Giropay', 0);

select * from paymethod;

commit;