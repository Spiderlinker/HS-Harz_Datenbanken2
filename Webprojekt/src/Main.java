import java.util.Date;

public class Main {

	public static void main(String[] args) {
		
		Date date = new Date();
		System.out.println(date);
		
		java.sql.Date d2 = new java.sql.Date(System.currentTimeMillis());
		System.out.println(d2);
		
	}
	
}
