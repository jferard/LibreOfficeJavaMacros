/*
 * LibreOffice Java Macros. Write and embed LibreOffice Java macros with Maven.
 *     Copyright (C) 2021 Julien Férard
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.jferard.libreofficemacrosexample;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Data {
    public static void createBase(File file) throws SQLException {
        System.setProperty("textdb.allow_full_path", "true");
        Connection connection = DriverManager
                .getConnection("jdbc:hsqldb:file:./base.h2", null, null);
        try {
            Statement statement = connection.createStatement();
            statement.execute(
                    "DROP TABLE temp IF EXISTS");
            statement.execute(
                    "CREATE TEXT TABLE temp (field1 INTEGER, field2 VARCHAR(100), field3 VARCHAR(100))");
            statement.execute(
                    "SET TABLE temp SOURCE \"" + file.getAbsolutePath().replace("\\", "/") +
                            ";ignore_first=true;fs=\\semi\"");
            statement.execute(
                    "DROP TABLE mytable IF EXISTS");
            statement.execute(
                    "CREATE TABLE mytable (source VARCHAR(100), field1 INTEGER, field2 VARCHAR(100), field3 VARCHAR(100))");
            statement.execute(
                    "INSERT INTO mytable (SELECT 't', temp.* FROM temp)");
        } finally {
            connection.close();
        }
    }
}
