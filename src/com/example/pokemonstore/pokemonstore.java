package com.example.pokemonstore;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import androidx.room.*;
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

@Dao
interface ProductDao {
    @Insert
    void insert(Product product);

    @Query("SELECT * FROM products")
    List<Product> getAllProducts();
}

@Database(entities = {Product.class}, version = 1)
abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
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

public class MainApp extends Application {
    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        ListView<Product> listView = new ListView<>(productList);
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText(null);
                } else {
                    setText(product.name + " - $" + product.price + " (Stock: " + product.quantity + ")");
                }
            }
        });

        Button addButton = new Button("Add Product");
        addButton.setOnAction(e -> {
            Product newProduct = new Product("Pokeball", 5.99, 10);
            productList.add(newProduct);
        });

        VBox vbox = new VBox(10, listView, addButton);
        Scene scene = new Scene(vbox, 400, 300);

        primaryStage.setTitle("Pokemon Store");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
