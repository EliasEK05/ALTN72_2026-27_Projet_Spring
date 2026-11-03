package altn72.tpfilrouge.dto;

import java.util.List;

public record IndicateursDto(long nombreIncidentsActifs, long nombreRouges, long nombreOranges, long nombreVerts,
                             long nombreAffectes, List<IncidentsParStationDto> incidentsParStation) {
}
