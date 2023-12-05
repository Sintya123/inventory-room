import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventory.data.InventoryDatabase
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class ItemDaoTest {
    //mendklatasikan item pada  las itemDAOtest
    private var item1 = Item(1, "apples",10.0,20)
    private var item2 = Item (2, "Bananas", 15.0, 97)

    private lateinit var itemDao: ItemDao
    private lateinit var inventoryDatabase: InventoryDatabase
    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        //Menggunakan database dalam memori karena informasi yang disimpan di sini hilang ketika
        // proses dimatikan.
        inventoryDatabase = Room.inMemoryDatabaseBuilder(context, InventoryDatabase::class.java)
            // mengizinkan main therad melakukkan testing
            .allowMainThreadQueries()
            .build()
        itemDao = inventoryDatabase.itemDao()
    }
    @After
    @Throws(IOException::class)
    fun closeDb() {
        inventoryDatabase.close()
    }
    //fungsi utilitas untuk menambahkan satu item, lalu dua item, ke database.
    private suspend fun addOneItemToDb(){
        itemDao.insert(item1)
    }
    private suspend fun addTwoItemsToDb(){
        itemDao.insert(item1)
        itemDao.insert(item2)

    }
    // pengujian untuk menyisipkan satu item ke dalam database, insert(). Beri nama pengujian daoInsert_insertsItemIntoDB dan anotasikan dengan @Test.
    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsItemIntoDB() = runBlocking {
        addOneItemToDb()
        val allItems = itemDao.getAllItems().first()
        assertEquals(allItems[0], item1)
    }
    @Test
    @Throws(Exception::class)
    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        val allItems = itemDao.getAllItems().first()
        assertEquals(allItems[0], item1)
        assertEquals(allItems[1], item2)

    }
    @Test
    @Throws(Exception::class)
    fun daoUpdateItems_updatesItemsInDB() = runBlocking {
        addTwoItemsToDb()
        itemDao.update(Item(1, "apples", 15.0, 25))
        itemDao.update(Item(2, "bananas", 5.0, 50))

        val allItems = itemDao.getAllItems().first()
        assertEquals(allItems[0], Item(1, "apples", 15.0, 25))
        assertEquals(allItems[1], Item(2, "bananas", 5.0, 50))
    }
    @Test
    @Throws(Exception::class)
    fun daoDeleteItems_deletesAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        itemDao.delete(item1)
        itemDao.delete(item2)
        val allItems = itemDao.getAllItems().first()
        assertTrue(allItems.isEmpty())
    }

    //menambhakan entity item
    @Test
    @Throws(Exception::class)
    fun daoGetItem_returnsItemFromDB()= runBlocking {
        addOneItemToDb()
        val item= itemDao.getItem(1)
        assertEquals(item.first(), item1)
    }
}