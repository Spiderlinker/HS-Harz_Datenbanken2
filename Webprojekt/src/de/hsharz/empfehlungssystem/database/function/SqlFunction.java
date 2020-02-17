package de.hsharz.empfehlungssystem.database.function;

import java.sql.SQLException;

public interface SqlFunction<T, R> {

	R apply(T t) throws SQLException;

}

