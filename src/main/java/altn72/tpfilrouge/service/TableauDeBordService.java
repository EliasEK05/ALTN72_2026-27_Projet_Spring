package altn72.tpfilrouge.service;

import altn72.tpfilrouge.config.ReserveAuResponsable;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.dto.IncidentsParStationDto;
import altn72.tpfilrouge.dto.IndicateursDto;
import altn72.tpfilrouge.dto.TableauDeBordDto;
import altn72.tpfilrouge.modele.NiveauAttention;
import altn72.tpfilrouge.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableauDeBordService {
    private final IncidentService incidentService;
    private final IncidentRepository incidentRepository;

    public TableauDeBordService(IncidentService incidentService, IncidentRepository incidentRepository) {
        this.incidentService = incidentService;
        this.incidentRepository = incidentRepository;
    }

    // F11 Incidents actifs filtrés et indicateurs sur l'ensemble du réseau
    @ReserveAuResponsable
    public TableauDeBordDto getTableauDeBord(Long stationId, String typeEquipement, NiveauAttention niveauAttention) {
        List<IncidentDto> incidents = incidentService.getIncidentsActifs(stationId, typeEquipement)
                .stream()
                .filter(incident -> niveauAttention == null || incident.niveauAttention() == niveauAttention)
                .toList();

        return new TableauDeBordDto(incidents, calculerIndicateurs());
    }

    private IndicateursDto calculerIndicateurs() {
        List<IncidentDto> incidentsActifs = incidentService.getIncidentsActifs(null, null);

        return new IndicateursDto(
                incidentsActifs.size(),
                compter(incidentsActifs, NiveauAttention.ROUGE),
                compter(incidentsActifs, NiveauAttention.ORANGE),
                compter(incidentsActifs, NiveauAttention.VERT),
                compter(incidentsActifs, NiveauAttention.AFFECTE),
                getIncidentsParStation());
    }

    private long compter(List<IncidentDto> incidents, NiveauAttention niveauAttention) {
        return incidents.stream()
                .filter(incident -> incident.niveauAttention() == niveauAttention)
                .count();
    }

    private List<IncidentsParStationDto> getIncidentsParStation() {
        return incidentRepository.compterIncidentsActifsParStation()
                .stream()
                .map(ligne -> new IncidentsParStationDto(ligne.getStationNom(), ligne.getNombreIncidents()))
                .toList();
    }
}
