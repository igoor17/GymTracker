package es.eduardo.gymtracker.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import es.eduardo.gymtracker.R;

public class ProductDisplayFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_display, container, false);

        String category = getArguments().getString("category");
        String product = getArguments().getString("product");

        // Inicializa los RecyclerViews
        RecyclerView hsnRecyclerView = view.findViewById(R.id.hsnRecyclerView);
        RecyclerView myProteinRecyclerView = view.findViewById(R.id.myProteinRecyclerView);
        RecyclerView prozisRecyclerView = view.findViewById(R.id.prozisRecyclerView);

        // Inicializa los adaptadores con listas vacías
        List<Product> emptyList = new ArrayList<>();
        ProductAdapter hsnAdapter = new ProductAdapter(emptyList, getContext());
        ProductAdapter myProteinAdapter = new ProductAdapter(emptyList, getContext());
        ProductAdapter prozisAdapter = new ProductAdapter(emptyList, getContext());

        // Establece los adaptadores en los RecyclerViews
        hsnRecyclerView.setAdapter(hsnAdapter);
        myProteinRecyclerView.setAdapter(myProteinAdapter);
        prozisRecyclerView.setAdapter(prozisAdapter);

        // Realiza las consultas y actualiza los adaptadores
        updateAdapterWithStoreProducts(category, product, "HSN", hsnAdapter);
        updateAdapterWithStoreProducts(category, product, "MyProtein", myProteinAdapter);
        updateAdapterWithStoreProducts(category, product, "Prozis", prozisAdapter);

        return view;
    }

    private void updateAdapterWithStoreProducts(String category, String product, String store, ProductAdapter adapter) {
        db.collection("store").document(category).collection(product).document("stores").collection(store)
                .get()
                .addOnSuccessListener(storeQueryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot storeDocument : storeQueryDocumentSnapshots) {
                        Product productItem = storeDocument.toObject(Product.class);
                        products.add(productItem);
                    }
                    // Actualiza la lista en el adaptador y notifica al adaptador que los datos han cambiado
                    adapter.updateList(products);
                });
    }
}