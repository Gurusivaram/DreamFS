package com.example.dreamfilesystem

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.dreamfilesystem.Constants.FILE_TYPE

/**
 * Helper class to add or remove or get the file structure data
 */
class DBHelper(
    context: Context,
    factory: SQLiteDatabase.CursorFactory?
) :
    SQLiteOpenHelper(
        context, DATABASE_NAME,
        factory, DATABASE_VERSION
    ) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "dream_fs.db"
        const val TABLE_NAME = "fs"
        const val COLUMN_ID = "id"
        const val COLUMN_ELEMENT_NAME = "element_name"
        const val COLUMN_IS_FILE = "is_file"
        const val COLUMN_PARENT_DIR = "parent_dir"
        const val COLUMN_TEXT_CONTENT = "text_content"
        const val COLUMN_FILE_EXTENSION = "extension"
        const val COLUMN_MODIFIED_TIME = "m_time"
        const val COLUMN_CREATION_TIME = "c_time"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " +
                TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_ELEMENT_NAME + " TEXT NOT NULL," +
                COLUMN_IS_FILE + " INTEGER NOT NULL," +
                COLUMN_PARENT_DIR + " TEXT NOT NULL," +
                COLUMN_TEXT_CONTENT + " TEXT," +
                COLUMN_FILE_EXTENSION + " TEXT," +
                COLUMN_MODIFIED_TIME + " TEXT NOT NULL," +
                COLUMN_CREATION_TIME + " TEXT NOT NULL" +
                ")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    /**
     * To drop the table
     */
    fun dropTable() {
        this.writableDatabase.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    /**
     * To scan the entire directory without scanning sub directory
     * @param directoryPath     Path of the directory
     * @return                  List of items in the given file
     */
    fun scan(directoryPath: String): ArrayList<String> {
        val list = arrayListOf<String>()
        val db = this.readableDatabase

        //SELECT element_name from guru WHERE parent_dir = '/US';
        val cursor: Cursor = db.rawQuery(
            "SELECT $COLUMN_ELEMENT_NAME FROM $TABLE_NAME WHERE $COLUMN_PARENT_DIR = '${directoryPath.uppercase()}'",
            null
        )
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val range = cursor.getColumnIndex(COLUMN_ELEMENT_NAME)
            list.add(cursor.getString(range))
            cursor.moveToNext()
        }
        cursor.close()
        return list
    }

    /**
     * To create an item in the file system
     * @param elementPath   Path of the item to be created with name at end of the path
     * @param elementType   Type of the item to be created, like file or folder
     * @return              Whether the creation is success or not
     */
    fun create(elementPath: String, elementType: String): Boolean {
        val name = getElementNameFromPath(elementPath)
        val isFile = if (elementType == FILE_TYPE) 1 else 0
        val parentDirectory = getParentDirectoryFromPath(elementPath)
        val parentName = getElementNameFromPath(parentDirectory)
        val fileExtension = getFileExtensionFromPath(elementPath)
        var isParentFound = false
        val contentValues = ContentValues()
        val db = this.writableDatabase

        contentValues.put(COLUMN_ELEMENT_NAME, name)
        contentValues.put(COLUMN_IS_FILE, isFile)
        contentValues.put(COLUMN_PARENT_DIR, parentDirectory.ifEmpty { "/" })
        contentValues.put(COLUMN_MODIFIED_TIME, System.currentTimeMillis() / 1000L)
        contentValues.put(COLUMN_CREATION_TIME, System.currentTimeMillis() / 1000L)
        if (fileExtension.isNotEmpty() && elementType == FILE_TYPE)
            contentValues.put(COLUMN_FILE_EXTENSION, fileExtension)

        if (parentName.isEmpty() || parentName == "/") isParentFound = true

        val cur: Cursor = db.rawQuery(
            "SELECT $COLUMN_ELEMENT_NAME FROM $TABLE_NAME WHERE $COLUMN_PARENT_DIR = '$parentDirectory'",
            null
        )
        cur.moveToFirst()
        while (!cur.isAfterLast) {
            val range = cur.getColumnIndex(COLUMN_ELEMENT_NAME)
            if (cur.getString(range) == name) return false
            cur.moveToNext()
        }
        cur.close()

        if (isParentFound) {

            return db.insert(TABLE_NAME, null, contentValues) >= 1
        } else {
            //SELECT * from fs Where element_name = 'UK'
            val cursor: Cursor = db.rawQuery(
                "SELECT $COLUMN_ELEMENT_NAME FROM $TABLE_NAME WHERE $COLUMN_ELEMENT_NAME = '$parentName'",
                null
            )
            cursor.moveToFirst()
            if (!cursor.isAfterLast) {
                val range = cursor.getColumnIndex(COLUMN_ELEMENT_NAME)
                val name = cursor.getString(range)
                if (name == parentName || name.isEmpty() || name == "/")
                    return db.insert(TABLE_NAME, null, contentValues) >= 1
            }
            cursor.close()
        }
        return false
    }

    /**
     * To read the content of the text file
     * @param filePath   Path of the item to be read
     * @return           Content of the text file
     */
    fun read(filePath: String): String? {
        val fileName = getElementNameFromPath(filePath)
        val db = this.readableDatabase

        //SELECT string_txt from guru where element_name = 'address'
        val cursor: Cursor = db.rawQuery(
            "SELECT $COLUMN_TEXT_CONTENT FROM $TABLE_NAME WHERE $COLUMN_ELEMENT_NAME = '$fileName'",
            null
        )
        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            val range = cursor.getColumnIndex(COLUMN_TEXT_CONTENT)
            val text = cursor.getString(range)
            return if (text != null && text.isNotEmpty() && text != "NULL") text else null
        }
        cursor.close()
        return null
    }

    /**
     * To write the text content in the given text file
     * @param filePath  Path of the item to be edited
     * @param content   Content to be added
     * @return          Whether the write is success or not
     */
    fun write(filePath: String, content: String): Boolean {
        val fileName = getElementNameFromPath(filePath)
        val fileExtension = getFileExtensionFromPath(filePath)

        if (fileExtension != "TXT") return false

        //UPDATE guru SET string_txt = 'this is a example text' where element_name = 'address'
        val contentValues = ContentValues()
        val db = this.writableDatabase

        contentValues.put(COLUMN_TEXT_CONTENT, content)
        contentValues.put(COLUMN_MODIFIED_TIME, System.currentTimeMillis() / 1000L)

        return db.update(TABLE_NAME, contentValues, "element_name = ?", arrayOf(fileName)) > 0
    }

    /**
     * To move the file or folder recursively to the given destination
     * @param elementPath     Path of the item to be moved with file name at the end of the path
     * @param directoryPath   Destination directory
     * @return                Whether the move is success or not
     */
    fun move(elementPath: String, directoryPath: String): Boolean {
        if (directoryPath.contains(elementPath)) return false

        val fileName = getElementNameFromPath(elementPath)
        val newParentDirectory = getParentDirectoryFromPath(directoryPath)
        val parentName = getElementNameFromPath(directoryPath)
        val parentDir = "$newParentDirectory/$parentName"

        //UPDATE FROM guru WHERE ((parent_dir like '/IN%') | (element_name = 'IN'));
        val db = this.writableDatabase
        val contentValues1 = ContentValues()
        val contentValues2 = ContentValues()
        contentValues1.put(COLUMN_PARENT_DIR, parentDir.ifEmpty { "/" })
        contentValues1.put(COLUMN_MODIFIED_TIME, System.currentTimeMillis() / 1000L)

        val checkCur: Cursor = db.rawQuery(
            "SELECT $COLUMN_ELEMENT_NAME, $COLUMN_IS_FILE, $COLUMN_PARENT_DIR FROM $TABLE_NAME WHERE $COLUMN_ELEMENT_NAME = '${fileName}'",
            null
        )
        checkCur.moveToFirst()
        if (!checkCur.isAfterLast) {
            val rangeA = checkCur.getColumnIndex(COLUMN_IS_FILE)
            val isFile = checkCur.getInt(rangeA)
            val rangeB = checkCur.getColumnIndex(COLUMN_PARENT_DIR)
            val curPar = checkCur.getString(rangeB)

            if (curPar == parentDir) return false
            if (isFile == 0) {
                //UPDATE guru SET parent_dir = '/US/CA' WHERE parent_dir = '/IN';
                val cur: Cursor = db.rawQuery(
                    "SELECT $COLUMN_ELEMENT_NAME, $COLUMN_PARENT_DIR FROM $TABLE_NAME WHERE $COLUMN_PARENT_DIR like '${elementPath.uppercase()}%'",
                    null
                )
                cur.moveToFirst()
                while (!cur.isAfterLast) {
                    val range1 = cur.getColumnIndex(COLUMN_ELEMENT_NAME)
                    val range2 = cur.getColumnIndex(COLUMN_PARENT_DIR)
                    val curName = cur.getString(range1)
                    val curParent = cur.getString(range2)

                    contentValues2.put(
                        COLUMN_PARENT_DIR,
                        (parentDir.ifEmpty { "/" } + "/" + fileName + curParent.substringAfterLast("/$fileName")
                            .uppercase()).replace("//", "/"))
                    contentValues2.put(COLUMN_MODIFIED_TIME, System.currentTimeMillis() / 1000L)

                    db.update(TABLE_NAME, contentValues2, "element_name = ?", arrayOf(curName))
                    cur.moveToNext()
                }
                cur.close()
            }
        }
        checkCur.close()

        return db.update(TABLE_NAME, contentValues1, "element_name = ?", arrayOf(fileName)) > 0
    }

    /**
     * To rename the given file or folder
     * @param elementPath   Path of the item to be renamed
     * @param newName       New name
     * @return              Whether the rename is success or not
     */
    fun rename(elementPath: String, newName: String): Boolean {
        if (newName.isEmpty() || newName == "." || newName == ".." || newName == "\\") return false

        //UPDATE guru SET element_name = 'adrs' WHERE element_name = 'address';
        val contentValues = ContentValues()
        val db = this.writableDatabase

        contentValues.put(COLUMN_ELEMENT_NAME, getElementNameWithoutExtension(newName.uppercase()))
        contentValues.put(COLUMN_MODIFIED_TIME, System.currentTimeMillis() / 1000L)

        return db.update(
            TABLE_NAME,
            contentValues,
            "element_name = ?",
            arrayOf(getElementNameFromPath(elementPath))
        ) > 0
    }

    /**
     * To delete the given file or folder recursively
     * @param elementPath   Path of the item to be deleted
     * @return              Whether the delete is success or not
     */
    fun delete(elementPath: String): Boolean {
        val fileName = getElementNameFromPath(elementPath)

        //DELETE FROM guru WHERE ((parent_dir like '/IN%') | (element_name = 'IN'));
        val db = this.writableDatabase

        db.delete(TABLE_NAME, "parent_dir like '${elementPath.uppercase()}%'", arrayOf())
        return db.delete(TABLE_NAME, "element_name = ?", arrayOf(fileName)) > 0
    }

    /**
     * To get the latest modification time of the given item or folder in unix format
     * @param elementPath   Path of the item to get the modification time
     * @return              Latest modified time
     */
    fun mTime(elementPath: String): Long {
        val fileName = getElementNameFromPath(elementPath)
        val db = this.readableDatabase

        //SELECT m_time from guru where element_name = 'address'
        val cursor: Cursor = db.rawQuery(
            "SELECT $COLUMN_MODIFIED_TIME FROM $TABLE_NAME WHERE $COLUMN_ELEMENT_NAME = '$fileName'",
            null
        )
        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            val range = cursor.getColumnIndex(COLUMN_MODIFIED_TIME)
            val mTime = cursor.getString(range)
            return if (mTime != null && mTime.isNotEmpty() && mTime != "NULL") mTime.toLong() else -1
        }
        cursor.close()
        return -1
    }

    /**
     * To get the newly added time of the given item or folder in unix format
     * @param elementPath   Path of the item to get the newly added time
     * @return              Newly added time
     */
    fun cTime(elementPath: String): Long {
        val fileName = getElementNameFromPath(elementPath)
        val db = this.readableDatabase

        //SELECT c_time from guru where element_name = 'address'
        val cursor: Cursor = db.rawQuery(
            "SELECT $COLUMN_CREATION_TIME FROM $TABLE_NAME WHERE $COLUMN_ELEMENT_NAME = '$fileName'",
            null
        )
        cursor.moveToFirst()
        if (!cursor.isAfterLast) {
            val range = cursor.getColumnIndex(COLUMN_CREATION_TIME)
            val cTime = cursor.getString(range)
            return if (cTime != null && cTime.isNotEmpty() && cTime != "NULL") cTime.toLong() else -1
        }
        cursor.close()
        return -1
    }

    /**
     * To get the file name form the given path
     */
    private fun getFileNameFromPath(elementPath: String) =
        elementPath.substring(elementPath.lastIndexOf("/") + 1).uppercase()

    /**
     * To get the element name form the given path
     */
    private fun getElementNameFromPath(elementPath: String) =
        getElementNameWithoutExtension(getFileNameFromPath(elementPath)).uppercase()

    /**
     * To get the element name without extension
     */
    private fun getElementNameWithoutExtension(elementName: String) =
        elementName.replace("\\.\\w+$".toRegex(), "").uppercase()

    /**
     * To get the parent directory from the path
     */
    private fun getParentDirectoryFromPath(elementPath: String) =
        elementPath.substringBeforeLast("/").uppercase()

    /**
     * To get the file extension from the given path
     */
    private fun getFileExtensionFromPath(elementPath: String) =
        getFileNameFromPath(elementPath).substringAfterLast(".").uppercase()
}