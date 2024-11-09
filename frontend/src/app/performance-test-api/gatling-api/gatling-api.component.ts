import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { Subscription } from 'rxjs';
import { PerformanceTestApiService } from 'src/app/_services/performance-test-api.service';
import Swal from 'sweetalert2';
import { GatlingRequest } from './gatling-request';

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
  testLog: string = "";
  latestReportContent: SafeHtml | null = null; // Contenu du dernier rapport de test

  busy: Subscription | undefined;

  request: GatlingRequest = new GatlingRequest();

  constructor(
    private readonly performanceTestApiService: PerformanceTestApiService,
    private sanitizer: DomSanitizer
  ) { }

  ngOnInit(): void {
    this.modal = document.getElementById("myModal");
    this.reportModal = document.getElementById("reportModal"); // Initialisation de la modal du rapport
    this.span = document.getElementsByClassName("close")[0] as HTMLElement;
  }

  validateForm(): boolean {
    let isValid = true;
    const requiredFields = [
      { element: 'testScenarioName', errorMessage: 'Veuillez entrer une valeur' },
      { element: 'testBaseUrl', errorMessage: 'Veuillez entrer une valeur' },
      { element: 'testUri', errorMessage: 'Veuillez entrer une valeur' },
      { element: 'testMethodType', errorMessage: 'Veuillez sélectionner un type de requête' },
      { element: 'userNumber', errorMessage: 'Veuillez entrer ou sélectionner une valeur' }
    ];

    requiredFields.forEach(field => {
      const inputElement = document.getElementsByName(field.element)[0] as HTMLInputElement | null;
      const errorDiv = document.createElement('div');
      errorDiv.className = 'text-danger';

      if (inputElement?.nextElementSibling) {
        inputElement.nextElementSibling.remove();
      }
      if (inputElement && inputElement.value.trim() === '') {
        isValid = false;
        inputElement.classList.add('is-invalid');
        errorDiv.innerText = field.errorMessage;
        inputElement.insertAdjacentElement('afterend', errorDiv);
      } else {
        inputElement?.classList.remove('is-invalid');
      }
    });

    return isValid;
  }

  onSubmit() {

    if (!this.validateForm()) {
      return;
    }

    this.busy = this.performanceTestApiService.sendGatlingRequest(this.request)
      .subscribe((response: any) => {
        this.modal!.style.display = "block";

        // Extraction des messages de la réponse de l'API
        const pattern = /(.+)\n?/g;
        const matches: string[] = Array.from(response.message?.matchAll(pattern));
        const arrayOfStrings = matches.map(match => match[0]);

        // Filtrer les messages pour ne conserver que ceux indiquant le succès ou l'échec global
        const successMessage = arrayOfStrings.find(message => message.includes('request count'));
        const reportGeneratedMessage = arrayOfStrings.find(message => message.includes('Generated Report'));

        // Détermination du succès ou de l'échec global
        this.testResult = [{
          success: !!successMessage
        }];

        // Ajouter un message indiquant que le rapport a été généré
        if (reportGeneratedMessage) {
          this.testResult.push({
            message: 'Le rapport a été généré avec succès.',
            success: true
          });
        }

      }, (error: any) => {
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: "Le test a échoué, révisez votre configuration de test",
        })
      });
  }

  //  Afficher le dernier rapport
  showLatestReport() {
    const reportWindow = window.open(this.performanceTestApiService.getLatestReportUrl().toString(), '_blank');
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

  newTest() {
    this.request = new GatlingRequest();
    this.closeModal();
  }
}