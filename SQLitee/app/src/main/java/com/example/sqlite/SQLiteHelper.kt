import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.sqlite.ItemModel

class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 5
        private const val DATABASE_NAME = "Dictionary.db"
        private const val ID = "id"
        private const val NAME = "name"
        private const val TBL_ITEM = "tbl_item"
        private const val NUMBER = "phonenumber"
        private const val IMAGE = "image" // New column for storing the image
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblItem =
            "CREATE TABLE $TBL_ITEM ($ID INTEGER PRIMARY KEY, $NAME TEXT, $NUMBER INTEGER, $IMAGE BLOB)"
        db?.execSQL(createTblItem)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        //db?.execSQL("DROP TABLE IF EXISTS $TBL_ITEM")
        //onCreate(db)
    }

    fun insertItem(itm: ItemModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(ID, itm.id)
            put(NAME, itm.name)
            put(NUMBER, itm.phonenumber)
            put(IMAGE, itm.image) // Assuming 'itm.image' is a byte array containing the image data
        }
        val success = db.insert(TBL_ITEM, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllItems(): ArrayList<ItemModel> {
        val itemList: ArrayList<ItemModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_ITEM"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
            cursor?.let {
                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getInt(cursor.getColumnIndex(ID))
                        val name = cursor.getString(cursor.getColumnIndex(NAME))
                        val number = cursor.getInt(cursor.getColumnIndex(NUMBER))
                        val image = cursor.getBlob(cursor.getColumnIndex(IMAGE))
                        val itm = ItemModel(id = id, name = name, phonenumber = number, image = image)
                        itemList.add(itm)
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return itemList
    }

    fun updateItem(itm: ItemModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(ID, itm.id)
            put(NAME, itm.name)
            put(NUMBER, itm.phonenumber)
            put(IMAGE, itm.image) // Assuming 'itm.image' is a byte array containing the image data
        }
        val success = db.update(TBL_ITEM, contentValues, "id = ${itm.id}", null)
        db.close()
        return success
    }

    fun deleteItemByID(id: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TBL_ITEM, "id = $id", null)
        db.close()
        return success
    }

    fun getItemByID(id: Int): ItemModel? {
        val db = readableDatabase
        var item: ItemModel? = null
        val query = "SELECT * FROM $TBL_ITEM WHERE $ID = $id"
        val cursor = db.rawQuery(query, null)
        cursor?.let {
            if (cursor.moveToFirst()) {
                val columnIndexId = cursor.getColumnIndex(ID)
                val columnIndexWord = cursor.getColumnIndex(NAME)
                val columnIndexMean = cursor.getColumnIndex(NUMBER)
                val columnIndexImage = cursor.getColumnIndex(IMAGE)

                if (columnIndexId >= 0 && columnIndexWord >= 0 && columnIndexMean >= 0 && columnIndexImage >= 0) {
                    val itemId = cursor.getInt(columnIndexId)
                    val name = cursor.getString(columnIndexWord)
                    val number = cursor.getInt(columnIndexMean)
                    val image = cursor.getBlob(columnIndexImage)

                    item = ItemModel(itemId, name, number, image)
                }
            }
        }
        cursor?.close()
        return item
    }



}
