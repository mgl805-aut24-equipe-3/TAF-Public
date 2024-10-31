import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { PerformanceTestApiService } from 'src/app/_services/performance-test-api.service';
import Swal from 'sweetalert2';
import { GatlingRequest } from './gatling-request';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'app-gatling-api',
  templateUrl: './gatling-api.component.html',
  styleUrls: ['./gatling-api.component.css']
})
export class GatlingApiComponent implements OnInit {

  modal: HTMLElement | null = null;
  reportModal: HTMLElement | null = null; 
  span: HTMLElement | null = null;
  testResult: any;
  testLog: String = "";
  latestReportContent: SafeHtml | null = null; // Contenu du dernier rapport de test

  busy: Subscription | undefined;

  request: GatlingRequest = new GatlingRequest();

  constructor(
    private performanceTestApiService: PerformanceTestApiService,
    private sanitizer: DomSanitizer
  ) { }

  ngOnInit(): void {
    this.modal = document.getElementById("myModal");
    this.reportModal = document.getElementById("reportModal"); // Initialisation de la modal du rapport
    this.span = document.getElementsByClassName("close")[0] as HTMLElement;
  }

  onSubmit() {
    this.busy = this.performanceTestApiService.sendGatlingRequest(this.request)
      .subscribe((response: any) => {
        this.modal!.style.display = "block";
  
        // Extraction des messages de la réponse de l'API
        const pattern = /(.+)\n?/g;
        const matches: String[] = Array.from(response.message.matchAll(pattern));
        const arrayOfStrings = matches.map(match => match[0]);
  
        // Détermination du succès ou de l'échec pour chaque message
        this.testResult = arrayOfStrings.map(message => ({
          message,
          success: message.includes('OK') // Utilisation de 'OK' pour indiquer le succès (On check la présence de 'OK' dans le message)
        }));
  
      }, (error: any) => {
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: "Le test a échoué, révisez votre configuration de test",
        })
      });
  }

  // Nouvelle méthode pour afficher le dernier rapport
  showLatestReport() {
    this.performanceTestApiService.getLatestReport().subscribe(
      (reportContent: string) => {
        this.latestReportContent = this.sanitizer.bypassSecurityTrustHtml(reportContent);
        console.log("Latest report content:", this.latestReportContent); // Ajout de log pour vérifier le contenu
        this.openReportModal();
      },
      (error: any) => {
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: "Impossible de récupérer le dernier rapport.",
        });
      }
    );
  }

  openReportModal() {
    if (this.reportModal) {
      this.reportModal.style.display = "block"; // Affiche la modal du rapport
      console.log("Report modal opened"); // Ajout de log pour vérifier l'ouverture du modal
    } else {
      console.error("reportModal is not initialized");
    }
  }

  closeReportModal() {
    if (this.reportModal) {
      this.reportModal.style.display = "none"; // Ferme la modal du rapport
      this.latestReportContent = null;
      console.log("Report modal closed"); // Ajout de log pour vérifier la fermeture du modal
    } else {
      console.error("reportModal is not initialized");
    }
  }

  closeModal() {
    if (this.modal) {
      this.modal.style.display = "none";
      this.latestReportContent = null;
      console.log("Modal closed"); // Ajout de log pour vérifier la fermeture du modal
    } else {
      console.error("modal is not initialized");
    }
  }
}