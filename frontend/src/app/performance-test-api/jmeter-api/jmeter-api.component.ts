import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { JMeterHttpRequest } from './jmeter-http-request';
import { JMeterFTPRequest } from './jmeter-ftp-request';
import { Subscription } from 'rxjs';
import Swal from 'sweetalert2';
import { PerformanceTestApiService } from 'src/app/_services/performance-test-api.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-jmeter-api',
  templateUrl: './jmeter-api.component.html',
  styleUrls: [
    '../gatling-api/gatling-api.component.css',
    './jmeter-api.component.css',
  ],
})
export class JmeterApiComponent implements OnInit {
  modal: HTMLElement | null = document.getElementById('myModal');
  span: Element | null = document.getElementsByClassName('close')[0];
  testResult: any;
  testLog: String = '';
  reportFilePath: String = '';
  busy: Subscription | undefined;

  http_request: JMeterHttpRequest = new JMeterHttpRequest();
  ftp_request: JMeterFTPRequest = new JMeterFTPRequest();
  http_description = document.getElementById('http-description');
  ftp_description = document.getElementById('ftp-description');

  testResults: any[] = [];
  result_table: HTMLElement | null = document.getElementById('result_table');
  httpForm: HTMLElement | null = document.getElementById('http-form');
  ftpForm: HTMLElement | null = document.getElementById('ftp-form');
  switchLabel: HTMLElement | null = document.getElementById('switchLabel');
  switchCheckbox: HTMLInputElement | null = document.getElementById(
    'formSwitch'
  ) as HTMLInputElement;

  selectedTest: any = null;

  constructor(
    private fb: FormBuilder,
    private performanceTestApiService: PerformanceTestApiService) { }

  ngOnInit(): void {
    this.modal = document.getElementById('myModal');
    this.span = document.getElementsByClassName('close')[0];
    this.result_table = document.getElementById('result_table');
    this.httpForm = document.getElementById('http-form');
    this.ftpForm = document.getElementById('ftp-form');
    this.switchLabel = document.getElementById('switchLabel');
    this.http_description = document.getElementById('http-description');
    this.ftp_description = document.getElementById('ftp-description');

    this.switchCheckbox = document.getElementById(
      'formSwitch'
    ) as HTMLInputElement;
  }

  validateHttpForm(): boolean {
    let isValid = true;
    const requiredFields = [
      { element: 'loop', errorMessage: 'Veuillez entrer une valeur' },
      { element: 'nbThreads', errorMessage: 'Veuillez entrer une valeur' },
      { element: 'domain', errorMessage: 'Veuillez entrer une valeur' },
      { element: 'path', errorMessage: 'Veuillez entrer une valeur' },
      { element: 'methodType', errorMessage: 'Veuillez sélectionner un type de requête' }
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

  validateFtpForm(): boolean {
    let isValid = true;

    const requiredFields = [
      { element: 'loopFtp', errorMessage: 'Veuillez entrer une valeur' },
      { element: 'nbThreadsFtp', errorMessage: 'Veuillez entrer une valeur' },
      { element: 'domainFtp', errorMessage: 'Veuillez entrer une valeur' },
      { element: 'methodTypeFtp', errorMessage: 'Veuillez sélectionner un type de requête' }
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

  onHttpSubmit(showAlert: boolean = false) {
    if (!this.validateHttpForm()) {
      return;
    }

    this.busy = this.performanceTestApiService
      .sendHttpJMeterRequest(this.http_request)
      .subscribe((response: any) => {
        this.testResults = response;

        // Transformation de la réponse pour inclure des informations sur le succès ou l'échec global
        const successMessage = response.length != 0;
        this.testResult = [{
          success: successMessage,
          details: response.details // Assurez-vous que les détails sont inclus dans la réponse
        }];

        // Ajouter un message indiquant que le rapport a été généré
        if (successMessage) {
          this.testResult.push({
            message: 'Le rapport a été généré avec succès.',
            success: true
          });
        }

        if (successMessage) {
          this.modal!.style.display = 'block';
        } else {
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: "Le test a échoué, révisez votre configuration de test",
          })
        }
      }, (error: any) => {
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: "Le test a échoué, révisez votre configuration de test",
        })
      });
  }

  onFtpSubmit() {
    if (!this.validateFtpForm()) {
      return;
    }

    this.busy = this.performanceTestApiService
      .sendFtpJMeterRequest(this.ftp_request)
      .subscribe((response: any) => {
        this.testResults = response;
        this.testResult = response; // Ajoutez cette ligne
        if (response.length != 0) {
          this.modal!.style.display = 'block';
        } else {
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: "Le test a échoué, révisez votre configuration de test",
          })
        }

      }, (error: any) => {
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: "Le test a échoué, révisez votre configuration de test",
        })
      });
  }

  closeModal() {
    this.modal!.style.display = 'none';
  }

  showTestDetails(test: any) {
    console.log("Test details:", test);
    this.selectedTest = test;
  }

  closeTestDetails() {
    this.selectedTest = null;
  }

  closeModalOnOutsideClick(event: MouseEvent) {
    if ((event.target as HTMLElement).id === 'detailModal') {
      this.closeTestDetails();
    }
  }

  toggleForms() {
    if (
      this.switchCheckbox?.checked &&
      this.httpForm &&
      this.ftpForm &&
      this.switchLabel &&
      this.ftp_description &&
      this.http_description
    ) {
      // Show FTP form
      this.httpForm.style.display = 'none';
      this.ftpForm.style.display = 'block';
      this.ftp_description.style.display = 'block';
      this.http_description.style.display = 'none';
      this.switchLabel.innerText = 'FTP';
    } else if (
      !this.switchCheckbox?.checked &&
      this.httpForm &&
      this.ftpForm &&
      this.switchLabel &&
      this.ftp_description &&
      this.http_description
    ) {
      // Show HTTP form
      this.httpForm.style.display = 'block';
      this.ftpForm.style.display = 'none';
      this.ftp_description.style.display = 'none';
      this.http_description.style.display = 'block';
      this.switchLabel.innerText = 'HTTP';
    }
  }

  newTest() {
    this.testResults = [];
    this.selectedTest = null;
    this.modal!.style.display = 'none';
    if (this.httpForm) {
      (this.httpForm as HTMLFormElement).reset();
    }
    if (this.ftpForm) {
      (this.ftpForm as HTMLFormElement).reset();
    }
  }

  //  Afficher le dernier rapport
  showLatestReport() {
    if (this.testResult && this.testResult.length > 0) {
      const reportUrl = `${environment.apiUrl}${this.testResult[0].details['location-url']}`;
      window.open(reportUrl, '_blank');
    } else {
      Swal.fire({
        icon: 'error',
        title: 'Erreur',
        text: "Aucun rapport disponible",
      });
    }
  }
}