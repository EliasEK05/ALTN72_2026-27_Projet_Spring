package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.StationRepository;
import org.springframework.stereotype.Service;

@Service
public class StationService {
    //Injection de dépendance via constructeur
    private final StationRepository stationRepository;
    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }




}
