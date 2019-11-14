package de.hsharz.empfehlungssystem.database.function;

import java.sql.SQLException;

public interface SqlFunction<R, T> {

	R apply(T t) throws SQLException;

}
