package es.eduardo.gymtracker.store;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import es.eduardo.gymtracker.R;

/**
 * Adapter class for displaying a list of products in a RecyclerView.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products; // List of products to display
    private Context context; // Context reference for launching intents

    /**
     * Constructor to initialize the ProductAdapter with a list of products and a context.
     *
     * @param products List of products to display.
     * @param context  Context reference.
     */
    public ProductAdapter(List<Product> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and create a new ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Bind data to the ViewHolder
        Product product = products.get(position);
        holder.productName.setText(product.getName());
        holder.productOptions.setText(product.getOptions());
        holder.productPrice.setText(product.getPrice());
        Glide.with(context).load(product.getImageUrl()).into(holder.productImage);

        // Set click listener to open the product link in a browser
        holder.itemView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.getLink()));
            context.startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    /**
     * Updates the list of products with new data and notifies the adapter of the change.
     *
     * @param newProducts New list of products to display.
     */
    public void updateList(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class to hold views for individual product items in the RecyclerView.
     */
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage; // Image view for product image
        TextView productName; // Text view for product name
        TextView productOptions; // Text view for product options
        TextView productPrice; // Text view for product price

        /**
         * Constructor to initialize the ViewHolder with views.
         *
         * @param itemView View representing the item layout.
         */
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image_view);
            productName = itemView.findViewById(R.id.productName_text_view);
            productOptions = itemView.findViewById(R.id.productOptions_text_view);
            productPrice = itemView.findViewById(R.id.productPrice_text_view);
        }
    }
}
