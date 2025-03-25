package pokemonstore;

import android.content.Context;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.room.;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Entity(tableName = "products")
class Product {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public double price;
    public int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}

@Entity(tableName = "users")
class User {
    @PrimaryKey
    public String username;
    public String password;
    public boolean isAdmin;

    public User(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }
}

@Dao
interface ProductDao {
    @Insert
    void insert(Product product);

    @Query("SELECT FROM products")
    List<Product> getAllProducts();

    @Query("UPDATE products SET quantity = quantity - 1 WHERE id = :productId")
    void sellProduct(int productId);
}

@Dao
interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User authenticate(String username, String password);
}

@Database(entities = {Product.class, User.class}, version = 1)
abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
    public abstract UserDao userDao();

    private static volatile AppDatabase instance;
    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "pokemon_store_db")
                            .build();
                }
            }
        }
        return instance;
    }
}

public class MainActivity extends ComponentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = AppDatabase.getDatabase(this);
    }
}