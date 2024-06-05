package es.eduardo.gymtracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.eduardo.gymtracker.exercises.ExercisesFragment;
import es.eduardo.gymtracker.routines.Routine;
import es.eduardo.gymtracker.routines.RoutineAdapter;
import es.eduardo.gymtracker.routines.RoutineDisplayFragment;
import es.eduardo.gymtracker.store.Product;


public class HomeFragment extends Fragment {

    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Muscle Groups
    ImageView chestImageView;
    ImageView backImageView;
    ImageView armsImageView;
    ImageView legsImageView;

    // User Routines
    ViewPager2 viewPager;
    TextView noRoutinesTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        chestImageView = view.findViewById(R.id.chest);
        backImageView = view.findViewById(R.id.back);
        armsImageView = view.findViewById(R.id.arms);
        legsImageView = view.findViewById(R.id.legs);

        viewPager = view.findViewById(R.id.viewPager);
        noRoutinesTextView = view.findViewById(R.id.noRoutinesTextView);

        loadRoutines();

        View.OnClickListener imageViewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                Bundle bundle = new Bundle();
                int id = v.getId();
                if (id == R.id.chest) {
                    bundle.putString("muscleGroup", "chest");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.back) {
                    bundle.putString("muscleGroup", "back");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.arms) {
                    bundle.putString("muscleGroup", "arms");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.legs) {
                    bundle.putString("muscleGroup", "legs");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.abs) {
                    bundle.putString("muscleGroup", "abs");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.shoulders) {
                    bundle.putString("muscleGroup", "shoulders");
                    fragment = new ExercisesFragment();
                } else {
                    throw new IllegalStateException("Unexpected value: " + id);
                }

                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        };

        chestImageView.setOnClickListener(imageViewClickListener);
        backImageView.setOnClickListener(imageViewClickListener);
        armsImageView.setOnClickListener(imageViewClickListener);
        legsImageView.setOnClickListener(imageViewClickListener);

        Button a= view.findViewById(R.id.button);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Product> straps_en = new ArrayList<>();
                straps_en.add(new Product("Lifting Straps (1 pair)", "13.90€", "https://www.hsnstore.com/marcas/hsn-accessories/correas-de-agarre-hsn-2-unidades", "", "Black", "HSN"));
                straps_en.add(new Product("Lifting Straps", "5.99€", "https://www.myprotein.es/sports-accessories/correas-para-levantamiento-acolchadas-de-myprotein-negro/14704737.html", "", "Black", "MyProtein"));
                straps_en.add(new Product("Cotton Lifting Straps", "14.44€", "https://www.prozis.com/es/es/prozis/-cotton-lifting-straps-black", "", "Black, Mint, Olive Green / Beige, Off White / Olive Green, Light Pink / Taupe / Beige, Light Purple", "Prozis"));

                List<Product> wristbands_en = new ArrayList<>();
                wristbands_en.add(new Product("HSN Wristbands (1 pair)", "10.99€", "https://www.hsnstore.com/marcas/hsn-accessories/munequera-hsn-x2", "", "Black", "HSN"));
                wristbands_en.add(new Product("Myprotein Wristbands", "19.99€", "https://www.myprotein.es/sports-accessories/munequeras-de-myprotein-negro/14704730.html", "", "Black", "MyProtein"));
                wristbands_en.add(new Product("Wrist Wraps 2.0", "14.44€", "https://www.prozis.com/es/es/prozis/-wrist-wraps-2.0-black", "", "Black", "Prozis"));

                List<Product> shaker_en = new ArrayList<>();
                shaker_en.add(new Product("Shaker (400ml)", "7.90€", "https://www.hsnstore.com/marcas/hsn-accessories/shaker-hsn-we-are-nutrition-bola-mezcladora-hsn", "", "400ml, 500ml, 600ml", "HSN"));
                shaker_en.add(new Product("Myprotein Plastic Shaker", "5.99€", "https://www.myprotein.es/sports-accessories/shaker-de-plastico-de-myprotein-transparente-negro/14704710.html", "", "400ml", "MyProtein"));
                shaker_en.add(new Product("Fusion Shaker Bottle", "8.49€", "https://www.prozis.com/es/es/prozis/fusion-shaker-bottle-all-black", "", "Black, Black / Desert, Black circle, Black Trek, Violet, Crystal / Black, Pink, Crystal / White", "Prozis"));

                List<Product> towel_en = new ArrayList<>();
                towel_en.add(new Product("HSN Polyester Sports Towel", "6.90€", "https://www.hsnstore.com/marcas/hsn-accessories/toalla-deportiva-hsn-poliester", "", "30x82cm","HSN"));
                towel_en.add(new Product("MP Hand Towel", "9.99€", "https://www.myprotein.es/sports-clothing/toalla-de-mano-de-mp-negro/14872002.html", "", "35x60cm","MyProtein"));
                towel_en.add(new Product("Workout Microfibre Towel", "14.44€", "https://www.prozis.com/es/es/prozis/toalla-workout-microfibre-gray", "", "Gray","Prozis"));


                List<Product> straps_es = new ArrayList<>();
                straps_es.add(new Product("Correas de Agarre (1 par)", "13,90€","https://www.hsnstore.com/marcas/hsn-accessories/correas-de-agarre-hsn-2-unidades", "","Negro","HSN"));
                straps_es.add(new Product("Correas para levantamiento","5,99€","https://www.myprotein.es/sports-accessories/correas-para-levantamiento-acolchadas-de-myprotein-negro/14704737.html","","Negro","MyProtein"));
                straps_es.add(new Product("Correas de algodón para levantamiento de peso","14,44€","https://www.prozis.com/es/es/prozis/-cotton-lifting-straps-black","","Negro,Menta,Verde oliva / Beis,Off White / Verde oliva,Rosa claro/Marrón topo / Beis,Lila claro","Prozis"));

                List<Product> munequeras_es = new ArrayList<>();
                munequeras_es.add(new Product("Muñequera HSN (1 par)", "10,99€","https://www.hsnstore.com/marcas/hsn-accessories/munequera-hsn-x2", "","Negro","HSN"));
                munequeras_es.add(new Product("Muñequeras de Myprotein","19,99€","https://www.myprotein.es/sports-accessories/munequeras-de-myprotein-negro/14704730.html","","Negro","MyProtein"));
                munequeras_es.add(new Product("Muñequeras 2.0","14,44€","https://www.prozis.com/es/es/prozis/-wrist-wraps-2.0-black","","Negro","Prozis"));

                List<Product> shaker_es = new ArrayList<>();
                shaker_es.add(new Product("Shaker (400ml)","7,90€","https://www.hsnstore.com/marcas/hsn-accessories/shaker-hsn-we-are-nutrition-bola-mezcladora-hsn", "","400ml,500ml,600ml","HSN"));
                shaker_es.add(new Product("Shaker de plástico de Myprotein", "5,99€","https://www.myprotein.es/sports-accessories/shaker-de-plastico-de-myprotein-transparente-negro/14704710.html","","400ml","MyProtein"));
                shaker_es.add(new Product("Fusion Shaker Bottle","8,49€","https://www.prozis.com/es/es/prozis/fusion-shaker-bottle-all-black","","Negro,Negro / Desierto,Negro circle,Negro Trek,Violeta,Cristal / Negro,Rosa,Cristal / Blanco","Prozis"));

                List<Product> toalla_es = new ArrayList<>();
                toalla_es.add(new Product("Toalla Deportiva HSN Poliéster","6,90€","https://www.hsnstore.com/marcas/hsn-accessories/toalla-deportiva-hsn-poliester", "","30x82cm","HSN"));
                toalla_es.add(new Product("Toalla de mano de MP","9,99€","https://www.myprotein.es/sports-clothing/toalla-de-mano-de-mp-negro/14872002.html","","35x60cm","MyProtein"));
                toalla_es.add(new Product("Toalla Workout Microfibre","14,44€","https://www.prozis.com/es/es/prozis/toalla-workout-microfibre-gray","","Gris","Prozis"));

                List<Product> proteina_es_hsn = new ArrayList<>();
                proteina_es_hsn.add(new Product("EVOWHEY PROTEIN (500g)", "9,49€","https://www.hsnstore.com/marcas/sport-series/evowhey-protein-2-0", "","Monodosis(30g),500g,2Kg,4kg,Pack(5x500g)", "HSN"));
                proteina_es_hsn.add(new Product("EVOLATE 2.0 (WHEY ISOLATE CFM) (500g)", "14,25€","https://www.hsnstore.com/marcas/sport-series/evolate-2-0-whey-isolate-cfm", "","Monodosis(30g),500g,2Kg,4kg,Pack(5x500g)", "HSN"));
                proteina_es_hsn.add(new Product("EVOHYDRO 2.0 (HYDRO WHEY) (500g)", "17,00€","https://www.hsnstore.com/marcas/sport-series/evohydro-2-0-hydro-whey", "","500g,2Kg,Pack(5x500g)", "HSN"));

                List<Product> proteina_es_myprotein = new ArrayList<>();
                proteina_es_myprotein.add(new Product("Impact Whey Protein (1Kg)", "22,99€","https://www.myprotein.es/nutricion-deportiva/impact-whey-protein/10530943.html", "","250g,1kg,2.5kg,5kg", "MyProtein"));
                proteina_es_myprotein.add(new Product("Impact Whey Isolate (1Kg)", "31,99€","https://www.myprotein.es/nutricion-deportiva/impact-whey-isolate/10530911.html", "","500g,1Kg,2.5Kg", "MyProtein"));
                proteina_es_myprotein.add(new Product("Hydrolysed Whey Protein (1Kg)", "26,99€","https://www.myprotein.es/nutricion-deportiva/proteina-de-suero-hidrolizada/10529805.html", "","1Kg,2.5Kg", "MyProtein"));

                List<Product> proteina_es_prozis = new ArrayList<>();
                proteina_es_prozis.add(new Product("100% Real Whey Protein (1Kg)", "25,49€","https://www.prozis.com/es/es/prozis/100-real-whey-protein-1000-g", "","400g,1Kg,2Kg", "Prozis"));
                proteina_es_prozis.add(new Product("100% Real Whey Isolate (1Kg)", "33,99€","https://www.prozis.com/es/es/prozis/100-real-whey-isolate-1000-g", "","1Kg,2Kg", "Prozis"));
                proteina_es_prozis.add(new Product("100% Whey Hydro Isolate (900g)", "33,14€","https://www.prozis.com/es/es/prozis/100-whey-hydro-isolate-900-g", "","900g,2Kg", "Prozis"));

                List<Product> creatina_es_hsn = new ArrayList<>();
                creatina_es_hsn.add(new Product("Creatina Monohidrato (150g)", "3,25€","https://www.hsnstore.com/marcas/raw-series/creatina-monohidrato-en-polvo", "","150g,500g,1Kg", "HSN"));
                creatina_es_hsn.add(new Product("Creatina Excell (100% creapure) (150g)", "7,65€","https://www.hsnstore.com/marcas/raw-series/creatina-excell-100-creapure-en-polvo", "","150g,500g,1Kg", "HSN"));

                List<Product> creatina_es_myprotein = new ArrayList<>();
                creatina_es_myprotein.add(new Product("Creatina Monohidrato (100g)", "8,99€","https://www.myprotein.es/nutricion-deportiva/creatina-monohidrato-en-polvo/10530050.html", "","100g,250g", "MyProtein"));
                creatina_es_myprotein.add(new Product("Creatina Creapure (250g)", "23,99€","https://www.myprotein.es/nutricion-deportiva/creatina-creapure-en-polvo/10529740.html", "","250g,500g,1Kg", "MyProtein"));

                List<Product> creatina_es_prozis = new ArrayList<>();
                creatina_es_prozis.add(new Product("Creatina Monohidrato (300g)", "26,09€","https://www.prozis.com/es/es/prozis/monohidrato-de-creatina-300-g", "","150g,300g,700g", "Prozis"));
                creatina_es_prozis.add(new Product("Creatina Creapure (300g)", "35,09€","https://www.prozis.com/es/es/prozis/creatina-creapure-300-g", "","300g", "Prozis"));

                List<Product> preentreno_es_hsn = new ArrayList<>();
                preentreno_es_hsn.add(new Product("Evordx 2.0 (150g)", "9,49€","https://www.hsnstore.com/marcas/sport-series/evordx-2-0", "","Monodosis(21g),150g,500g,1Kg,Pack(5x100g)", "HSN"));
                preentreno_es_hsn.add(new Product("Evobomb (150g)", "5,21€","https://www.hsnstore.com/marcas/sport-series/evobomb", "","150g,500g,1Kg", "HSN"));

                List<Product> preentreno_es_myprotein = new ArrayList<>();
                preentreno_es_myprotein.add(new Product("Cafeína Pura Comprimidos (100)", "4,49€","https://www.myprotein.es/nutricion-deportiva/cafeina-pura-comprimidos/10529801.html", "","100 Tabletas,200 Tabletas", "MyProtein"));
                preentreno_es_myprotein.add(new Product("Original Pre-Workout (600g)", "20,99€","https://www.myprotein.es/nutricion-deportiva/origin-pre-workout/12941037.html", "","600g", "MyProtein"));

                List<Product> preentreno_es_prozis = new ArrayList<>();
                preentreno_es_prozis.add(new Product("Big Shot - Pre-Workout (46 servings)", "19,19€","https://www.prozis.com/es/es/prozis/big-shot-pre-workout-46-servings", "","23 Servings,46 Servings", "Prozis"));
                preentreno_es_prozis.add(new Product("Powa 2.0 (300g)", "22,49€","https://www.prozis.com/es/es/prozis/powa-2.0-300-g", "","300g,600g", "Prozis"));

                List<Product> proteina_en_hsn = new ArrayList<>();
                proteina_en_hsn.add(new Product("EVOWHEY PROTEIN (500g)", "9.49€", "https://www.hsnstore.com/marcas/sport-series/evowhey-protein-2-0", "", "Single dose (30g), 500g, 2Kg, 4kg, Pack (5x500g)", "HSN"));
                proteina_en_hsn.add(new Product("EVOLATE 2.0 (WHEY ISOLATE CFM) (500g)", "14.25€", "https://www.hsnstore.com/marcas/sport-series/evolate-2-0-whey-isolate-cfm", "", "Single dose (30g), 500g, 2Kg, 4kg, Pack (5x500g)", "HSN"));
                proteina_en_hsn.add(new Product("EVOHYDRO 2.0 (HYDRO WHEY) (500g)", "17.00€", "https://www.hsnstore.com/marcas/sport-series/evohydro-2-0-hydro-whey", "", "500g, 2Kg, Pack (5x500g)", "HSN"));

                List<Product> proteina_en_myprotein = new ArrayList<>();
                proteina_en_myprotein.add(new Product("Impact Whey Protein (1Kg)", "22.99€", "https://www.myprotein.es/nutricion-deportiva/impact-whey-protein/10530943.html", "", "250g, 1kg, 2.5kg, 5kg", "MyProtein"));
                proteina_en_myprotein.add(new Product("Impact Whey Isolate (1Kg)", "31.99€", "https://www.myprotein.es/nutricion-deportiva/impact-whey-isolate/10530911.html", "", "500g, 1Kg, 2.5Kg", "MyProtein"));
                proteina_en_myprotein.add(new Product("Hydrolysed Whey Protein (1Kg)", "26.99€", "https://www.myprotein.es/nutricion-deportiva/proteina-de-suero-hidrolizada/10529805.html", "", "1Kg, 2.5Kg", "MyProtein"));

                List<Product> proteina_en_prozis = new ArrayList<>();
                proteina_en_prozis.add(new Product("100% Real Whey Protein (1Kg)", "25.49€", "https://www.prozis.com/es/es/prozis/100-real-whey-protein-1000-g", "", "400g, 1Kg, 2Kg", "Prozis"));
                proteina_en_prozis.add(new Product("100% Real Whey Isolate (1Kg)", "33.99€", "https://www.prozis.com/es/es/prozis/100-real-whey-isolate-1000-g", "", "1Kg, 2Kg", "Prozis"));
                proteina_en_prozis.add(new Product("100% Whey Hydro Isolate (900g)", "33.14€", "https://www.prozis.com/es/es/prozis/100-whey-hydro-isolate-900-g", "", "900g, 2Kg", "Prozis"));

                List<Product> creatina_en_hsn = new ArrayList<>();
                creatina_en_hsn.add(new Product("Creatine Monohydrate (150g)", "3.25€", "https://www.hsnstore.com/marcas/raw-series/creatina-monohidrato-en-polvo", "", "150g, 500g, 1Kg", "HSN"));
                creatina_en_hsn.add(new Product("Creatine Excell (100% Creapure) (150g)", "7.65€", "https://www.hsnstore.com/marcas/raw-series/creatina-excell-100-creapure-en-polvo", "", "150g, 500g, 1Kg", "HSN"));

                List<Product> creatina_en_myprotein = new ArrayList<>();
                creatina_en_myprotein.add(new Product("Creatine Monohydrate (100g)", "8.99€", "https://www.myprotein.es/nutricion-deportiva/creatina-monohidrato-en-polvo/10530050.html", "", "100g, 250g", "MyProtein"));
                creatina_en_myprotein.add(new Product("Creatine Creapure (250g)", "23.99€", "https://www.myprotein.es/nutricion-deportiva/creatina-creapure-en-polvo/10529740.html", "", "250g, 500g, 1Kg", "MyProtein"));

                List<Product> creatina_en_prozis = new ArrayList<>();
                creatina_en_prozis.add(new Product("Creatine Monohydrate (300g)", "26.09€", "https://www.prozis.com/es/es/prozis/monohidrato-de-creatina-300-g", "", "150g, 300g, 700g", "Prozis"));
                creatina_en_prozis.add(new Product("Creatine Creapure (300g)", "35.09€", "https://www.prozis.com/es/es/prozis/creatina-creapure-300-g", "", "300g", "Prozis"));

                List<Product> preentreno_en_hsn = new ArrayList<>();
                preentreno_en_hsn.add(new Product("Evordx 2.0 (150g)", "9.49€", "https://www.hsnstore.com/marcas/sport-series/evordx-2-0", "", "Single dose (21g), 150g, 500g, 1Kg, Pack (5x100g)", "HSN"));
                preentreno_en_hsn.add(new Product("Evobomb (150g)", "5.21€", "https://www.hsnstore.com/marcas/sport-series/evobomb", "", "150g, 500g, 1Kg", "HSN"));

                List<Product> preentreno_en_myprotein = new ArrayList<>();
                preentreno_en_myprotein.add(new Product("Pure Caffeine Tablets (100)", "4.49€", "https://www.myprotein.es/nutricion-deportiva/cafeina-pura-comprimidos/10529801.html", "", "100 Tablets, 200 Tablets", "MyProtein"));
                preentreno_en_myprotein.add(new Product("Original Pre-Workout (600g)", "20.99€", "https://www.myprotein.es/nutricion-deportiva/origin-pre-workout/12941037.html", "", "600g", "MyProtein"));

                List<Product> preentreno_en_prozis = new ArrayList<>();
                preentreno_en_prozis.add(new Product("Big Shot - Pre-Workout (46 servings)", "19.19€", "https://www.prozis.com/es/es/prozis/big-shot-pre-workout-46-servings", "", "23 Servings, 46 Servings", "Prozis"));
                preentreno_en_prozis.add(new Product("Powa 2.0 (300g)", "22.49€", "https://www.prozis.com/es/es/prozis/powa-2.0-300-g", "", "300g, 600g", "Prozis"));


                addProducts(straps_es, "accessories_es", "Straps");
                addProducts(munequeras_es,"accessories_es","Muñequeras");
                addProducts(shaker_es,"accessories_es","Shakers");
                addProducts(toalla_es,"accessories_es","Toallas");

                addProducts(straps_en, "accessories", "Straps");
                addProducts(wristbands_en,"accessories","Wristbands");
                addProducts(shaker_en,"accessories","Shakers");
                addProducts(towel_en,"accessories","Towels");

                addProducts(proteina_es_hsn, "nutrition_es", "Proteina");
                addProducts(proteina_es_myprotein, "nutrition_es", "Proteina");
                addProducts(proteina_es_prozis, "nutrition_es", "Proteina");

                addProducts(creatina_es_hsn, "nutrition_es", "Creatina");
                addProducts(creatina_es_myprotein, "nutrition_es", "Creatina");
                addProducts(creatina_es_prozis, "nutrition_es", "Creatina");

                addProducts(preentreno_es_hsn, "nutrition_es", "Pre-Entreno");
                addProducts(preentreno_es_myprotein, "nutrition_es", "Pre-Entreno");
                addProducts(preentreno_es_prozis, "nutrition_es", "Pre-Entreno");

                addProducts(proteina_en_hsn, "nutrition", "Protein");
                addProducts(proteina_en_myprotein, "nutrition", "Protein");
                addProducts(proteina_en_prozis, "nutrition", "Protein");

                addProducts(creatina_en_hsn, "nutrition", "Creatine");
                addProducts(creatina_en_myprotein, "nutrition", "Creatine");
                addProducts(creatina_en_prozis, "nutrition", "Creatine");

                addProducts(preentreno_en_hsn, "nutrition", "Pre-Workout");
                addProducts(preentreno_en_myprotein, "nutrition", "Pre-Workout");
                addProducts(preentreno_en_prozis, "nutrition", "Pre-Workout");

            }
        });


        return view;
    }

    public void addProducts(List<Product> products, String category, String subcategory) {
        for (Product product : products) {
            Map<String, Object> productData = new HashMap<>();
            productData.put("price", product.getPrice());
            productData.put("link", product.getLink());
            productData.put("imageUrl", product.getImageUrl());
            productData.put("options", product.getOptions());
            String store= product.getStore();

            db.collection("store").document(category).collection(subcategory)
                    .document("stores").collection(store)
                    .document(product.getName())
                    .set(productData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // The product was successfully written to Firestore
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // There was an error writing the product to Firestore
                        }
                    });
        }
    }


    private void loadRoutines() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("users").document(userEmail).collection("routines")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Routine> routines = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getId(); // Obtener el nombre de la rutina
                                String imageUrl = document.getString("imageUrl"); // Obtener la URL de la imagenº
                                Long daysLong = document.getLong("days"); // Obtener el número de días
                                Long totalExercisesLong = document.getLong("exercises"); // Obtener el número total de ejercicios

                                int days = daysLong != null ? daysLong.intValue() : 0;
                                int totalExercises = totalExercisesLong != null ? totalExercisesLong.intValue() : 0;

                                routines.add(new Routine(name, imageUrl, days, totalExercises, new ArrayList<>()));

                                if (routines.isEmpty()) {
                                    viewPager.setVisibility(View.GONE);
                                    noRoutinesTextView.setVisibility(View.VISIBLE);
                                } else {
                                    viewPager.setVisibility(View.VISIBLE);
                                    noRoutinesTextView.setVisibility(View.GONE);
                                    if (isAdded()) {
                                        RoutineAdapter routineAdapter = new RoutineAdapter(getActivity(), routines, this::onRoutineSelected);
                                        viewPager.setAdapter(routineAdapter);
                                    }
                                }
                            }
                        } else {
                            Log.w("HomeFragment", "Error getting documents.", task.getException());
                        }
                    }

                    private void onRoutineSelected(Routine routine) {
                        RoutineDisplayFragment routineDisplayFragment = new RoutineDisplayFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("routineName", routine.getName());
                        routineDisplayFragment.setArguments(bundle);

                        FragmentManager fragmentManager = getFragmentManager();
                        if (fragmentManager != null) {
                            fragmentManager.beginTransaction()
                                    .replace(R.id.frame_layout, routineDisplayFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                });
    }




}