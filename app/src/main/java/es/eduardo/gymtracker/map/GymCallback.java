package es.eduardo.gymtracker.map;

import java.util.List;

import es.eduardo.gymtracker.gym.Gym;

public interface GymCallback {
    void onGymsReceived(List<Gym> gyms);
}