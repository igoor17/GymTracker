package es.eduardo.gymtracker.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import es.eduardo.gymtracker.R;

/**
 * Fragment to display products from different stores categorized by store name.
 */
public class ProductDisplayFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_display, container, false);

        String category = getArguments().getString("category");
        String product = getArguments().getString("product");

        // Initialize RecyclerViews
        RecyclerView hsnRecyclerView = view.findViewById(R.id.hsnRecyclerView);
        RecyclerView myProteinRecyclerView = view.findViewById(R.id.myProteinRecyclerView);
        RecyclerView prozisRecyclerView = view.findViewById(R.id.prozisRecyclerView);

        // Set layout managers for RecyclerViews
        hsnRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myProteinRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        prozisRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapters with empty lists
        List<Product> emptyList = new ArrayList<>();
        ProductAdapter hsnAdapter = new ProductAdapter(emptyList, getContext());
        ProductAdapter myProteinAdapter = new ProductAdapter(emptyList, getContext());
        ProductAdapter prozisAdapter = new ProductAdapter(emptyList, getContext());

        // Set adapters to RecyclerViews
        hsnRecyclerView.setAdapter(hsnAdapter);
        myProteinRecyclerView.setAdapter(myProteinAdapter);
        prozisRecyclerView.setAdapter(prozisAdapter);

        // Query and update adapters with store products
        updateAdapterWithStoreProducts(category, product, "HSN", hsnAdapter);
        updateAdapterWithStoreProducts(category, product, "MyProtein", myProteinAdapter);
        updateAdapterWithStoreProducts(category, product, "Prozis", prozisAdapter);

        return view;
    }

    /**
     * Queries Firestore for products from a specific store and updates the provided adapter with the results.
     *
     * @param category Category of the product.
     * @param product  Type of the product.
     * @param store    Name of the store to query.
     * @param adapter  Adapter to update with queried products.
     */
    private void updateAdapterWithStoreProducts(String category, String product, String store, ProductAdapter adapter) {
        db.collection("store").document(category).collection(product).document("stores").collection(store)
                .get()
                .addOnSuccessListener(storeQueryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot storeDocument : storeQueryDocumentSnapshots) {
                        Product productItem = storeDocument.toObject(Product.class);
                        products.add(productItem);
                    }
                    // Update the adapter's list and notify that the data set has changed
                    adapter.updateList(products);
                });
    }
}
