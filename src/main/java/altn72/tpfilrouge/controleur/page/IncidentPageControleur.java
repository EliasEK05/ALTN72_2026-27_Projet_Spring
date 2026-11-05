package altn72.tpfilrouge.controleur.page;

import altn72.tpfilrouge.dto.DeclarerIncidentRequestDto;
import altn72.tpfilrouge.dto.EvaluerPrestataireRequestDto;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.dto.ModifierDescriptionRequestDto;
import altn72.tpfilrouge.modele.StatutIncident;
import altn72.tpfilrouge.service.EquipementService;
import altn72.tpfilrouge.service.EvaluationService;
import altn72.tpfilrouge.service.IncidentService;
import altn72.tpfilrouge.service.JournalService;
import altn72.tpfilrouge.service.PrestataireService;
import altn72.tpfilrouge.service.RecommandationService;
import altn72.tpfilrouge.service.UtilisateurConnecte;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/incidents")
public class IncidentPageControleur {
    private final IncidentService incidentService;
    private final EquipementService equipementService;
    private final EvaluationService evaluationService;
    private final PrestataireService prestataireService;
    private final RecommandationService recommandationService;
    private final JournalService journalService;

    public IncidentPageControleur(IncidentService incidentService, EquipementService equipementService, EvaluationService evaluationService,
                                 PrestataireService prestataireService, RecommandationService recommandationService, JournalService journalService) {
        this.incidentService = incidentService;
        this.equipementService = equipementService;
        this.evaluationService = evaluationService;
        this.prestataireService = prestataireService;
        this.recommandationService = recommandationService;
        this.journalService = journalService;
    }

    @GetMapping("/historique")
    public String afficherHistorique(Model model) {
        model.addAttribute("incidents", incidentService.getHistoriqueIncidentsResolus());
        return "historique";
    }

    @GetMapping("/nouveau")
    public String afficherFormulaireDeclaration(@RequestParam Long equipementId, Model model) {
        model.addAttribute("fiche", equipementService.getFicheEquipement(equipementId));
        return "declaration";
    }

    @PostMapping
    public String declarer(@Valid DeclarerIncidentRequestDto requete, BindingResult validation, Model model, RedirectAttributes redirection) {
        if (validation.hasErrors()) {
            model.addAttribute("fiche", equipementService.getFicheEquipement(requete.equipementId()));
            model.addAttribute("erreur", messagesDErreur(validation));
            return "declaration";
        }
        IncidentDto incident = incidentService.creerIncident(requete);
        redirection.addFlashAttribute("succes", incident.equipementFragile()
                ? "L'incident a été déclaré. Attention : cet équipement est désormais fragile."
                : "L'incident a été déclaré.");
        return "redirect:/incidents/" + incident.id();
    }

    @GetMapping("/{id}")
    public String afficherIncident(@PathVariable Long id, @AuthenticationPrincipal UtilisateurConnecte utilisateur, Model model) {
        IncidentDto incident = incidentService.getIncidentById(id);
        model.addAttribute("incident", incident);
        model.addAttribute("journal", journalService.getJournal(id));
        // Les listes réservées au responsable ne sont chargées que si elles peuvent servir : le service les refuserait sinon
        if (utilisateur != null && utilisateur.estResponsable() && incident.statut() != StatutIncident.RESOLU) {
            model.addAttribute("prestataires", prestataireService.getPrestataires());
            model.addAttribute("recommandations", recommandationService.recommanderPrestataires(id));
        }
        return "incident";
    }

    @PostMapping("/{id}/description")
    public String modifierDescription(@PathVariable Long id, @Valid ModifierDescriptionRequestDto requete, BindingResult validation, RedirectAttributes redirection) {
        if (validation.hasErrors()) {
            redirection.addFlashAttribute("erreur", messagesDErreur(validation));
        } else {
            incidentService.modifierDescription(id, requete.description());
            redirection.addFlashAttribute("succes", "La description a été modifiée.");
        }
        return "redirect:/incidents/" + id;
    }

    @PostMapping("/{id}/affectation")
    public String affecter(@PathVariable Long id, @RequestParam Long prestataireId, RedirectAttributes redirection) {
        IncidentDto incident = incidentService.affecterPrestataire(id, prestataireId);
        redirection.addFlashAttribute("succes", "L'incident est affecté à " + incident.prestataireNom() + ".");
        return "redirect:/incidents/" + id;
    }

    @PostMapping("/{id}/resolution")
    public String resoudre(@PathVariable Long id, RedirectAttributes redirection) {
        incidentService.resoudre(id);
        redirection.addFlashAttribute("succes", "L'incident est résolu.");
        return "redirect:/incidents/" + id;
    }

    @PostMapping("/{id}/evaluation")
    public String evaluer(@PathVariable Long id, @Valid EvaluerPrestataireRequestDto requete, BindingResult validation, RedirectAttributes redirection) {
        if (validation.hasErrors()) {
            redirection.addFlashAttribute("erreur", messagesDErreur(validation));
        } else {
            evaluationService.evaluer(id, requete.note(), requete.commentaire());
            redirection.addFlashAttribute("succes", "L'évaluation a été enregistrée.");
        }
        return "redirect:/incidents/" + id;
    }

    private String messagesDErreur(BindingResult validation) {
        return validation.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" "));
    }
}
