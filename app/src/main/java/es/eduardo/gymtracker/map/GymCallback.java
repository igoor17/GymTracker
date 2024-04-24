package es.eduardo.gymtracker.map;

import java.util.List;

public interface GymCallback {
    void onGymsReceived(List<Gimnasio> gimnasios);
}