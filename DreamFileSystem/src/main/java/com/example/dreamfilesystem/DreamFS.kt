package com.example.dreamfilesystem

import android.content.Context

/**
 * DreamFS is a imaginary database file system which can add/remove/modify the imaginary files in Sqlite database
 * @param context Application context
 */
class DreamFS(private val context: Context) {

    private var dbHandler: DBHelper = DBHelper(context, null)

    /**
     * To scan the entire directory without scanning sub directory
     * @param directoryPath     Path of the directory
     * @return                  List of items in the given file
     */
    fun scan(directoryPath: String): ArrayList<String> = dbHandler.scan(directoryPath)

    /**
     * To create an item in the file system
     * @param elementPath   Path of the item to be created with name at end of the path
     * @param elementType   Type of the item to be created, like file or folder
     * @return              Whether the creation is success or not
     */
    fun create(elementPath: String, elementType: String): Boolean =
        dbHandler.create(elementPath, elementType)

    /**
     * To read the content of the text file
     * @param filePath   Path of the item to be read
     * @return           Content of the text file
     */
    fun read(filePath: String): String? = dbHandler.read(filePath)

    /**
     * To write the text content in the given text file
     * @param filePath  Path of the item to be edited
     * @param content   Content to be added
     * @return          Whether the write is success or not
     */
    fun write(filePath: String, content: String): Boolean = dbHandler.write(filePath, content)

    /**
     * To move the file or folder recursively to the given destination
     * @param elementPath     Path of the item to be moved with file name at the end of the path
     * @param directoryPath   Destination directory
     * @return                Whether the move is success or not
     */
    fun move(elementPath: String, directoryPath: String): Boolean =
        dbHandler.move(elementPath, directoryPath)

    /**
     * To rename the given file or folder
     * @param elementPath   Path of the item to be renamed
     * @param newName       New name
     * @return              Whether the rename is success or not
     */
    fun rename(elementPath: String, newName: String): Boolean =
        dbHandler.rename(elementPath, newName)

    /**
     * To delete the given file or folder recursively
     * @param elementPath   Path of the item to be deleted
     * @return              Whether the delete is success or not
     */
    fun delete(elementPath: String): Boolean = dbHandler.delete(elementPath)

    /**
     * To get the latest modification time of the given item or folder in unix format
     * @param elementPath   Path of the item to get the modification time
     * @return              Latest modified time
     */
    fun mTime(elementPath: String): Long = dbHandler.mTime(elementPath)

    /**
     * To get the newly added time of the given item or folder in unix format
     * @param elementPath   Path of the item to get the newly added time
     * @return              Newly added time
     */
    fun cTime(elementPath: String): Long = dbHandler.cTime(elementPath)
}