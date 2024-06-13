package es.eduardo.gymtracker.store;

/**
 * Represents a product available in a store.
 */
public class Product {
    private String name;
    private String price;
    private String link;
    private String imageUrl;
    private String options;
    private String store;

    /**
     * Constructs a product with required parameters.
     *
     * @param name     The name of the product.
     * @param price    The price of the product.
     * @param link     The link to purchase the product.
     * @param imageUrl The URL of the product image.
     * @param options  Additional options or details about the product.
     */
    public Product(String name, String price, String link, String imageUrl, String options) {
        this.name = name;
        this.price = price;
        this.link = link;
        this.imageUrl = imageUrl;
        this.options = options;
    }

    /**
     * Constructs a product with all parameters, including the store.
     *
     * @param name     The name of the product.
     * @param price    The price of the product.
     * @param link     The link to purchase the product.
     * @param imageUrl The URL of the product image.
     * @param options  Additional options or details about the product.
     * @param store    The name of the store where the product is available.
     */
    public Product(String name, String price, String link, String imageUrl, String options, String store) {
        this.name = name;
        this.price = price;
        this.link = link;
        this.imageUrl = imageUrl;
        this.options = options;
        this.store = store;
    }

    /**
     * Default constructor required for Firebase or other services.
     */
    public Product() {
    }

    /**
     * Retrieves the name of the product.
     *
     * @return The name of the product.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the price of the product.
     *
     * @return The price of the product.
     */
    public String getPrice() {
        return price;
    }

    /**
     * Retrieves the link to purchase the product.
     *
     * @return The link to purchase the product.
     */
    public String getLink() {
        return link;
    }

    /**
     * Retrieves the URL of the product image.
     *
     * @return The URL of the product image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Retrieves additional options or details about the product.
     *
     * @return Additional options or details about the product.
     */
    public String getOptions() {
        return options;
    }

    /**
     * Retrieves the name of the store where the product is available.
     *
     * @return The name of the store where the product is available.
     */
    public String getStore() {
        return store;
    }
}
