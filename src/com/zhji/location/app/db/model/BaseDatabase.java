
package com.zhji.location.app.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhji.location.app.db.DatabaseManager;

public abstract class BaseDatabase {
    /* Some attributes */
    private final String mTableName;
    protected final DatabaseManager mDBManager;

    /**
     * Constructor method
     * 
     * @param databaseManager
     * @param tableName
     */
    public BaseDatabase(final DatabaseManager databaseManager, final String tableName) {
        mDBManager = databaseManager;
        mTableName = tableName;
    }

    /**
     * Query the given table, returning a Cursor over the result set.
     * 
     * @param projection The table name to compile the query against. columns A
     *            list of which columns to return. Passing null will return all
     *            columns, which is discouraged to prevent reading data from
     *            storage that isn't going to be used.
     * @param selection A filter declaring which rows to return, formatted as an
     *            SQL WHERE clause (excluding the WHERE itself). Passing null
     *            will return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     *            replaced by the values from selectionArgs, in order that they
     *            appear in the selection. The values will be bound as Strings.
     * @param groupBy A filter declaring how to group rows, formatted as an SQL
     *            GROUP BY clause (excluding the GROUP BY itself). Passing null
     *            will cause the rows to not be grouped.
     * @param having A filter declare which row groups to include in the cursor,
     *            if row grouping is being used, formatted as an SQL HAVING
     *            clause (excluding the HAVING itself). Passing null will cause
     *            all row groups to be included, and is required when row
     *            grouping is not being used.
     * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause
     *            (excluding the ORDER BY itself). Passing null will use the
     *            default sort order, which may be unordered.
     * @param limit Limits the number of rows returned by the query, formatted
     *            as LIMIT clause. Passing null denotes no LIMIT clause.
     * @return A Cursor object, which is positioned before the first entry. Note
     *         that Cursors are not synchronized, see the documentation for more
     *         details.
     */
    public Cursor query(final String[] projection, final String selection,
            final String[] selectionArgs, final String groupBy, final String having,
            final String orderBy, final String limit) {
        if (mDBManager != null) {
            final Cursor cursor = mDBManager.getReadableDatabase().query(mTableName, projection,
                    selection, selectionArgs, groupBy, having, orderBy, limit);
            return cursor;
        }
        return null;
    }

    /**
     * Convenience method for inserting a row into the database.
     * 
     * @param values this map contains the initial column values for the row.
     *            The keys should be the column names and the values the column
     *            values
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insert(final ContentValues values) {
        if (mDBManager != null) {
            final SQLiteDatabase db = mDBManager.getWritableDatabase();
            final long id = db.insert(mTableName, null, values);
            return id;
        }
        return -1;
    }

    /**
     * Convenience method for updating rows in the database.
     * 
     * @param values this map contains the initial column values for the row.
     *            The keys should be the column names and the values the column
     *            values
     * @param selection A filter declaring which rows to return, formatted as an
     *            SQL WHERE clause (excluding the WHERE itself). Passing null
     *            will return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     *            replaced by the values from selectionArgs, in order that they
     *            appear in the selection. The values will be bound as Strings.
     * @return the number of rows affected
     */
    public int update(final ContentValues values, final String selection,
            final String[] selectionArgs) {
        if (mDBManager != null) {
            final SQLiteDatabase db = mDBManager.getWritableDatabase();
            final int count = db.update(mTableName, values, selection, selectionArgs);
            return count;
        }
        return -1;
    }

    /**
     * Convenience method for deleting rows in the database.
     * 
     * @param selection A filter declaring which rows to return, formatted as an
     *            SQL WHERE clause (excluding the WHERE itself). Passing null
     *            will return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     *            replaced by the values from selectionArgs, in order that they
     *            appear in the selection. The values will be bound as Strings.
     * @return the number of rows affected if a selection is passed in, 0
     *         otherwise. To remove all rows and get a count pass "1" as the
     *         selection.
     */
    public int delete(final String selection, final String[] selectionArgs) {
        if (mDBManager != null) {
            final SQLiteDatabase db = mDBManager.getWritableDatabase();
            final int count = db.delete(mTableName, selection, selectionArgs);
            return count;
        }
        return -1;
    }

    /**
     * Convenience method for getting number of rows
     * 
     * @return
     */
    public int getNumberOfRows() {
        final SQLiteDatabase db = mDBManager.getReadableDatabase();
        final Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + mTableName, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                return cursor.getInt(cursor.getColumnIndex("COUNT(*)"));
            }
            cursor.close();
        }
        return 0;
    }
}
