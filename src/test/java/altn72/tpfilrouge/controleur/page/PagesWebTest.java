package altn72.tpfilrouge.controleur.page;

import altn72.tpfilrouge.config.SecuriteConfig;
import altn72.tpfilrouge.dto.DeclarerIncidentRequestDto;
import altn72.tpfilrouge.dto.FicheEquipement;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.dto.IndicateursDto;
import altn72.tpfilrouge.dto.TableauDeBordDto;
import altn72.tpfilrouge.exception.IncidentIntrouvableException;
import altn72.tpfilrouge.exception.TransitionIncidentInterditeException;
import altn72.tpfilrouge.modele.NiveauAttention;
import altn72.tpfilrouge.modele.ProfilUtilisateur;
import altn72.tpfilrouge.modele.StatutIncident;
import altn72.tpfilrouge.modele.Utilisateur;
import altn72.tpfilrouge.service.EquipementService;
import altn72.tpfilrouge.service.EvaluationService;
import altn72.tpfilrouge.service.IncidentService;
import altn72.tpfilrouge.service.JournalService;
import altn72.tpfilrouge.service.PrestataireService;
import altn72.tpfilrouge.service.RecommandationService;
import altn72.tpfilrouge.service.StationService;
import altn72.tpfilrouge.service.TableauDeBordService;
import altn72.tpfilrouge.service.UtilisateurConnecte;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest({SupervisionPageControleur.class, IncidentPageControleur.class, ReseauPageControleur.class})
@Import(SecuriteConfig.class)
class PagesWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TableauDeBordService tableauDeBordService;
    @MockitoBean
    private StationService stationService;
    @MockitoBean
    private EquipementService equipementService;
    @MockitoBean
    private IncidentService incidentService;
    @MockitoBean
    private EvaluationService evaluationService;
    @MockitoBean
    private PrestataireService prestataireService;
    @MockitoBean
    private RecommandationService recommandationService;
    @MockitoBean
    private JournalService journalService;

    @Test
    void leTableauDeBordAfficheLesIncidentsActifsAvecLeurNiveauDAttention() throws Exception {
        when(tableauDeBordService.getTableauDeBord(null, null, null)).thenReturn(new TableauDeBordDto(
                List.of(incident(StatutIncident.NON_AFFECTE, NiveauAttention.ROUGE)),
                new IndicateursDto(1, 1, 0, 0, 0, List.of())));

        mockMvc.perform(get("/supervision").with(user(utilisateur(ProfilUtilisateur.RESPONSABLE_EXPLOITATION))))
                .andExpect(status().isOk())
                .andExpect(view().name("supervision"))
                .andExpect(content().string(containsString("Ascenseur bloqué")))
                .andExpect(content().string(containsString("text-bg-danger")));
    }

    @Test
    void desFiltresLaissesSurTousNeFiltrentRien() throws Exception {
        when(tableauDeBordService.getTableauDeBord(null, null, null)).thenReturn(new TableauDeBordDto(
                List.of(incident(StatutIncident.NON_AFFECTE, NiveauAttention.ROUGE)),
                new IndicateursDto(1, 1, 0, 0, 0, List.of())));

        mockMvc.perform(get("/supervision").param("stationId", "").param("typeEquipement", "").param("niveauAttention", "")
                        .with(user(utilisateur(ProfilUtilisateur.RESPONSABLE_EXPLOITATION))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ascenseur bloqué")));
        verify(tableauDeBordService).getTableauDeBord(null, null, null);
    }

    @Test
    void unAgentQuiOuvreLeTableauDeBordObtientUnePageDAccesRefuse() throws Exception {
        when(tableauDeBordService.getTableauDeBord(null, null, null)).thenThrow(new AccessDeniedException("refusé"));

        mockMvc.perform(get("/supervision").with(user(utilisateur(ProfilUtilisateur.AGENT_TERRAIN))))
                .andExpect(status().isForbidden())
                .andExpect(view().name("erreur"))
                .andExpect(content().string(containsString("Votre profil ne vous permet pas")));
    }

    @Test
    void unIncidentInexistantAfficheUnePageIntrouvableSansTraceTechnique() throws Exception {
        when(incidentService.getIncidentById(99L)).thenThrow(new IncidentIntrouvableException(99L));

        mockMvc.perform(get("/incidents/99").with(user(utilisateur(ProfilUtilisateur.AGENT_TERRAIN))))
                .andExpect(status().isNotFound())
                .andExpect(view().name("erreur"))
                .andExpect(content().string(containsString("n°99 est introuvable")));
    }

    @Test
    void uneActionRefuseeRameneSurLaPageDeLIncidentAvecLeMessage() throws Exception {
        when(incidentService.resoudre(1L)).thenThrow(new TransitionIncidentInterditeException("L'incident n°1 est résolu et ne peut plus être modifié"));

        mockMvc.perform(post("/incidents/1/resolution").with(user(utilisateur(ProfilUtilisateur.RESPONSABLE_EXPLOITATION))))
                .andExpect(redirectedUrl("/incidents/1"))
                .andExpect(flash().attribute("erreur", "L'incident n°1 est résolu et ne peut plus être modifié"));
    }

    @Test
    void uneDeclarationSansDescriptionReafficheLeFormulaireAvecUnMessage() throws Exception {
        when(equipementService.getFicheEquipement(10L)).thenReturn(
                new FicheEquipement(10L, "ASC-01", "Ascenseur quai A", "ASCENSEUR", "EN_PANNE", "Gare Centrale", false, List.of()));

        mockMvc.perform(post("/incidents").param("equipementId", "10").param("description", " ")
                        .with(user(utilisateur(ProfilUtilisateur.AGENT_TERRAIN))))
                .andExpect(status().isOk())
                .andExpect(view().name("declaration"))
                .andExpect(content().string(containsString("La description est obligatoire")));
        verify(incidentService, never()).creerIncident(any());
    }

    @Test
    void laFicheDUnEquipementFragileAfficheLeBadge() throws Exception {
        when(equipementService.getFicheEquipement(10L)).thenReturn(fiche(true));

        mockMvc.perform(get("/reseau/equipements/10").with(user(utilisateur(ProfilUtilisateur.AGENT_TERRAIN))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Équipement fragile")));
    }

    @Test
    void laFicheDUnEquipementNonFragileNAffichePasLeBadge() throws Exception {
        when(equipementService.getFicheEquipement(10L)).thenReturn(fiche(false));

        mockMvc.perform(get("/reseau/equipements/10").with(user(utilisateur(ProfilUtilisateur.AGENT_TERRAIN))))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Équipement fragile"))));
    }

    @Test
    void leTableauDeBordAfficheLeBadgeSurLesIncidentsDUnEquipementFragile() throws Exception {
        when(tableauDeBordService.getTableauDeBord(null, null, null)).thenReturn(new TableauDeBordDto(
                List.of(incident(StatutIncident.NON_AFFECTE, NiveauAttention.ROUGE, true)),
                new IndicateursDto(1, 1, 0, 0, 0, List.of())));

        mockMvc.perform(get("/supervision").with(user(utilisateur(ProfilUtilisateur.RESPONSABLE_EXPLOITATION))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Équipement fragile")));
    }

    @Test
    void leTableauDeBordNAffichePasLeBadgeSiLEquipementNEstPasFragile() throws Exception {
        when(tableauDeBordService.getTableauDeBord(null, null, null)).thenReturn(new TableauDeBordDto(
                List.of(incident(StatutIncident.NON_AFFECTE, NiveauAttention.ROUGE)),
                new IndicateursDto(1, 1, 0, 0, 0, List.of())));

        mockMvc.perform(get("/supervision").with(user(utilisateur(ProfilUtilisateur.RESPONSABLE_EXPLOITATION))))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Équipement fragile"))));
    }

    @Test
    void laConfirmationDeDeclarationSignaleUnEquipementDevenuFragile() throws Exception {
        when(incidentService.creerIncident(new DeclarerIncidentRequestDto(10L, "Ascenseur bloqué")))
                .thenReturn(incident(StatutIncident.NON_AFFECTE, NiveauAttention.VERT, true));

        mockMvc.perform(post("/incidents").param("equipementId", "10").param("description", "Ascenseur bloqué")
                        .with(user(utilisateur(ProfilUtilisateur.AGENT_TERRAIN))))
                .andExpect(redirectedUrl("/incidents/1"))
                .andExpect(flash().attribute("succes", containsString("fragile")));
    }

    @Test
    void laConfirmationDeDeclarationNeParlePasDeFragiliteSinon() throws Exception {
        when(incidentService.creerIncident(new DeclarerIncidentRequestDto(10L, "Ascenseur bloqué")))
                .thenReturn(incident(StatutIncident.NON_AFFECTE, NiveauAttention.VERT));

        mockMvc.perform(post("/incidents").param("equipementId", "10").param("description", "Ascenseur bloqué")
                        .with(user(utilisateur(ProfilUtilisateur.AGENT_TERRAIN))))
                .andExpect(redirectedUrl("/incidents/1"))
                .andExpect(flash().attribute("succes", not(containsString("fragile"))));
    }

    private FicheEquipement fiche(boolean fragile) {
        return new FicheEquipement(10L, "ASC-01", "Ascenseur quai A", "ASCENSEUR", "EN_PANNE", "Gare Centrale", fragile, List.of());
    }

    private IncidentDto incident(StatutIncident statut, NiveauAttention niveauAttention) {
        return incident(statut, niveauAttention, false);
    }

    private IncidentDto incident(StatutIncident statut, NiveauAttention niveauAttention, boolean equipementFragile) {
        return new IncidentDto(1L, "Ascenseur bloqué", Instant.parse("2026-11-01T12:00:00Z"), statut, null,
                10L, "ASC-01", "Ascenseur quai A", "Gare Centrale", null, null, null, null,
                Duration.ofHours(80), niveauAttention, equipementFragile);
    }

    private UtilisateurConnecte utilisateur(ProfilUtilisateur profil) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin("test");
        utilisateur.setMotDePasse("inutile");
        utilisateur.setPrenom("Camille");
        utilisateur.setProfil(profil);
        return new UtilisateurConnecte(utilisateur);
    }
}
