package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.dto.EvenementIncidentDto;
import altn72.tpfilrouge.service.JournalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JournalControleur {
    private final JournalService journalService;

    public JournalControleur(JournalService journalService) {
        this.journalService = journalService;
    }

    @GetMapping("/incident/{id}/journal")
    public List<EvenementIncidentDto> getJournal(@PathVariable Long id) {
        return journalService.getJournal(id);
    }
}
