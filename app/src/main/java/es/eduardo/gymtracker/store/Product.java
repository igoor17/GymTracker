package es.eduardo.gymtracker.store;

public class Product {
    private String name;
    private String price;
    private String link;
    private String imageUrl;
    private String options;
    private String flavor;

    public Product(String name, String price, String link, String imageUrl, String options) {
        this.name = name;
        this.price = price;
        this.link = link;
        this.imageUrl = imageUrl;
        this.options = options;
    }



    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getLink() {
        return link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOptions() {
        return options;
    }
}