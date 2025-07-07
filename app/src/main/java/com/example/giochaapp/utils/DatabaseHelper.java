package com.example.giochaapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.giochaapp.models.CartItem;
import com.example.giochaapp.models.Order;
import com.example.giochaapp.models.Product;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GioChaVietNam.db";
    private static final int DATABASE_VERSION = 1;

    // Tables
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_CART = "cart";
    private static final String TABLE_ORDERS = "orders";
    private static final String TABLE_ORDER_ITEMS = "order_items";

    // Product table columns
    private static final String COLUMN_PRODUCT_ID = "id";
    private static final String COLUMN_PRODUCT_NAME = "name";
    private static final String COLUMN_PRODUCT_DESCRIPTION = "description";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String COLUMN_PRODUCT_IMAGE_URL = "image_url";
    private static final String COLUMN_PRODUCT_RATING = "rating";
    private static final String COLUMN_PRODUCT_DISCOUNT = "discount";

    // Cart table columns
    private static final String COLUMN_CART_ID = "id";
    private static final String COLUMN_CART_PRODUCT_ID = "product_id";
    private static final String COLUMN_CART_QUANTITY = "quantity";
    private static final String COLUMN_CART_ADDED_DATE = "added_date";

    // Orders table columns
    private static final String COLUMN_ORDER_ID = "id";
    private static final String COLUMN_ORDER_DATE = "order_date";
    private static final String COLUMN_ORDER_STATUS = "status";
    private static final String COLUMN_ORDER_TOTAL = "total_price";
    private static final String COLUMN_ORDER_ADDRESS = "delivery_address";
    private static final String COLUMN_ORDER_PHONE = "customer_phone";
    private static final String COLUMN_ORDER_PAYMENT = "payment_method";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create products table
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_PRODUCT_ID + " TEXT PRIMARY KEY,"
                + COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
                + COLUMN_PRODUCT_DESCRIPTION + " TEXT,"
                + COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL,"
                + COLUMN_PRODUCT_IMAGE_URL + " TEXT,"
                + COLUMN_PRODUCT_RATING + " REAL DEFAULT 0,"
                + COLUMN_PRODUCT_DISCOUNT + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(createProductsTable);

        // Create cart table
        String createCartTable = "CREATE TABLE " + TABLE_CART + "("
                + COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CART_PRODUCT_ID + " TEXT NOT NULL,"
                + COLUMN_CART_QUANTITY + " INTEGER NOT NULL,"
                + COLUMN_CART_ADDED_DATE + " INTEGER NOT NULL,"
                + "FOREIGN KEY(" + COLUMN_CART_PRODUCT_ID + ") REFERENCES "
                + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + ")"
                + ")";
        db.execSQL(createCartTable);

        // Create orders table
        String createOrdersTable = "CREATE TABLE " + TABLE_ORDERS + "("
                + COLUMN_ORDER_ID + " TEXT PRIMARY KEY,"
                + COLUMN_ORDER_DATE + " INTEGER NOT NULL,"
                + COLUMN_ORDER_STATUS + " TEXT NOT NULL,"
                + COLUMN_ORDER_TOTAL + " INTEGER NOT NULL,"
                + COLUMN_ORDER_ADDRESS + " TEXT,"
                + COLUMN_ORDER_PHONE + " TEXT,"
                + COLUMN_ORDER_PAYMENT + " TEXT"
                + ")";
        db.execSQL(createOrdersTable);

        // Create order_items table (bổ sung để fix lỗi lịch sử đơn hàng)
        String createOrderItemsTable = "CREATE TABLE " + TABLE_ORDER_ITEMS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "order_id TEXT NOT NULL,"
                + "product_id TEXT NOT NULL,"
                + "quantity INTEGER NOT NULL,"
                + "total_price INTEGER NOT NULL,"
                + "FOREIGN KEY(order_id) REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "),"
                + "FOREIGN KEY(product_id) REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + ")"
                + ")";
        db.execSQL(createOrderItemsTable);

        // Insert sample data
        insertSampleProducts(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    private void insertSampleProducts(SQLiteDatabase db) {
        // Sample products data
        insertProduct(db, "1", "Giò Chả Truyền Thống",
                "Giò chả làm theo công thức truyền thống với gia vị đặc biệt",
                25000, "https://images.pexels.com/photos/4518843/pexels-photo-4518843.jpeg", 4.8f, 0);

        insertProduct(db, "2", "Combo Giò Chả",
                "Giò chả kèm rau sống và bánh tráng tươi",
                35000, "https://images.pexels.com/photos/4253312/pexels-photo-4253312.jpeg", 4.9f, 10);

        insertProduct(db, "3", "Giò Chả Cao Cấp",
                "Giò chả chất lượng cao với thảo mộc đặc biệt",
                45000, "https://images.pexels.com/photos/5419336/pexels-photo-5419336.jpeg", 4.7f, 0);

        insertProduct(db, "4", "Gói Gia Đình",
                "5 miếng giò chả các loại khác nhau",
                120000, "https://images.pexels.com/photos/6210959/pexels-photo-6210959.jpeg", 4.6f, 15);
    }

    private void insertProduct(SQLiteDatabase db, String id, String name, String description,
                               int price, String imageUrl, float rating, int discount) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_ID, id);
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRODUCT_DESCRIPTION, description);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_PRODUCT_IMAGE_URL, imageUrl);
        values.put(COLUMN_PRODUCT_RATING, rating);
        values.put(COLUMN_PRODUCT_DISCOUNT, discount);

        db.insert(TABLE_PRODUCTS, null, values);
    }

    // Cart operations
    public boolean addToCart(String productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if product already exists in cart
        Cursor cursor = db.query(TABLE_CART, null,
                COLUMN_CART_PRODUCT_ID + "=?", new String[]{productId},
                null, null, null);

        if (cursor.moveToFirst()) {
            // Update existing item
            int existingQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_QUANTITY));
            int newQuantity = existingQuantity + quantity;

            ContentValues values = new ContentValues();
            values.put(COLUMN_CART_QUANTITY, newQuantity);

            int result = db.update(TABLE_CART, values,
                    COLUMN_CART_PRODUCT_ID + "=?", new String[]{productId});
            cursor.close();
            return result > 0;
        } else {
            // Insert new item
            ContentValues values = new ContentValues();
            values.put(COLUMN_CART_PRODUCT_ID, productId);
            values.put(COLUMN_CART_QUANTITY, quantity);
            values.put(COLUMN_CART_ADDED_DATE, System.currentTimeMillis());

            long result = db.insert(TABLE_CART, null, values);
            cursor.close();
            return result != -1;
        }
    }

    public List<CartItem> getCartItems() {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT c.*, p.* FROM " + TABLE_CART + " c " +
                "INNER JOIN " + TABLE_PRODUCTS + " p ON c." + COLUMN_CART_PRODUCT_ID +
                " = p." + COLUMN_PRODUCT_ID;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                CartItem item = new CartItem(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CART_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE_URL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DISCOUNT))
                );
                cartItems.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return cartItems;
    }


    public boolean updateCartItemQuantity(String cartItemId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CART_QUANTITY, newQuantity);

        int result = db.update(TABLE_CART, values,
                COLUMN_CART_ID + "=?", new String[]{cartItemId});

        return result > 0;
    }

    public boolean removeFromCart(String cartItemId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(TABLE_CART,
                COLUMN_CART_ID + "=?", new String[]{cartItemId});

        return result > 0;
    }

    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);
    }

    // Order operations
    public boolean createOrder(List<CartItem> cartItems) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            // Tính tổng đơn hàng
            int total = 0;
            for (CartItem item : cartItems) {
                total += item.getTotalPrice();
            }

            // Tạo đơn hàng
            String orderId = "ORD" + System.currentTimeMillis();
            ContentValues orderValues = new ContentValues();
            orderValues.put(COLUMN_ORDER_ID, orderId);
            orderValues.put(COLUMN_ORDER_DATE, System.currentTimeMillis());
            orderValues.put(COLUMN_ORDER_STATUS, "Đang xử lý");
            orderValues.put(COLUMN_ORDER_TOTAL, total);
            orderValues.put(COLUMN_ORDER_ADDRESS, "123 Đường Nguyễn Trãi, Quận 1, TP. Hồ Chí Minh");
            orderValues.put(COLUMN_ORDER_PHONE, "+84 123 456 789");
            orderValues.put(COLUMN_ORDER_PAYMENT, "COD");

            long orderResult = db.insert(TABLE_ORDERS, null, orderValues);

            if (orderResult == -1) {
                return false;
            }

            // ➕ Thêm từng sản phẩm vào bảng order_items
            for (CartItem item : cartItems) {
                ContentValues itemValues = new ContentValues();
                itemValues.put("order_id", orderId);
                itemValues.put("product_id", item.getProductId());
                itemValues.put("quantity", item.getQuantity());
                itemValues.put("total_price", item.getTotalPrice());

                db.insert(TABLE_ORDER_ITEMS, null, itemValues);
            }

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE_URL)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_RATING)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DISCOUNT))
                );
                products.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return products;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ORDERS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String orderId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID));
                List<CartItem> items = getOrderItems(orderId); //  Lấy danh sách sản phẩm theo order_id

                Order order = new Order(
                        orderId,
                        null, // userId chưa có
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE))),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS)),
                        items, // Không còn là null
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_PAYMENT))
                );
                orders.add(order);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return orders;
    }

    public List<CartItem> getOrderItems(String orderId) {
        List<CartItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT oi.quantity, p.id AS product_id, p.name, p.description, p.price, p.image_url, p.discount " +
                "FROM " + TABLE_ORDER_ITEMS + " oi " +
                "JOIN " + TABLE_PRODUCTS + " p ON oi.product_id = p.id " +
                "WHERE oi.order_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{orderId});

        if (cursor.moveToFirst()) {
            do {
                CartItem item = new CartItem(
                        null, // cartId không cần
                        cursor.getString(cursor.getColumnIndexOrThrow("product_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("price")),
                        cursor.getString(cursor.getColumnIndexOrThrow("image_url")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("discount"))
                );
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return items;
    }

    public Order getOrderById(String orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Order order = null;

        Cursor cursor = db.query(TABLE_ORDERS, null,
                COLUMN_ORDER_ID + "=?", new String[]{orderId},
                null, null, null);

        if (cursor.moveToFirst()) {
            order = new Order(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID)),
                    null, // userId
                    new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE))),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS)),
                    null, // items
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ADDRESS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_PAYMENT))
            );

            //Lấy danh sách sản phẩm trong đơn hàng
            List<CartItem> orderItems = getOrderItems(orderId);
            order.setItems(orderItems); //Quan trọng: tránh null
        }
        cursor.close();
        return order;
    }

    public Product getProductById(String productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null,
                COLUMN_PRODUCT_ID + "=?", new String[]{productId},
                null, null, null);

        Product product = null;
        if (cursor.moveToFirst()) {
            product = new Product(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE_URL)),
                    cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_RATING)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DISCOUNT))
            );
        }
        cursor.close();
        return product;
    }
}