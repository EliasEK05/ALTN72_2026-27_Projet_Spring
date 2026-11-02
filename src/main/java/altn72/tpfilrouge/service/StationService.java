package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.StationDto;
import altn72.tpfilrouge.modele.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<StationDto> getStations() {
        return stationRepository.findAllByOrderByNomAsc()
                .stream()
                .map(station -> new StationDto(station.getId(), station.getNom()))
                .toList();
    }
}
