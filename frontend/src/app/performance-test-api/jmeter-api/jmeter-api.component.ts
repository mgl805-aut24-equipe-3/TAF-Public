import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { JMeterHttpRequest } from './jmeter-http-request';
import { JMeterFTPRequest } from './jmeter-ftp-request';
import { Subscription } from 'rxjs';
import Swal from 'sweetalert2';
import { PerformanceTestApiService } from 'src/app/_services/performance-test-api.service';

import jsPDF from 'jspdf';
import 'jspdf-autotable';

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

  selectedTest: any = null;

  selectedTest: any = null;

  constructor(
    private fb: FormBuilder,
    private performanceTestApiService: PerformanceTestApiService) {}

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

    

  

  onHttpSubmit() {
    

    if (!this.validateHttpForm()) {
      Swal.fire({
        icon: 'error',
        title: 'Erreur',
        text: 'Veuillez remplir tous les champs qui sont obligatoires',
      });
      return;
    }

    this.busy = this.performanceTestApiService
      .sendHttpJMeterRequest(this.http_request)
      .subscribe((response: any[]) => {
        this.testResults = response.map((result) => ({
          allThreads: result.allThreads,
          grpThreads: result.grpThreads,
          idleTime: result.IdleTime,
          dataType: result.dataType,
          connect: result.Connect,
          label: result.label,
          threadName: result.threadName,
          url: result.URL,
          responseCode: result.responseCode,
          latency: result.Latency,
          timestamp: result.timeStamp,
          elapsed: result.elapsed,
          success: result.success,
          bytes: result.bytes,
          responseMessage: result.responseMessage,
          failureMessage: result.failureMessage,
          sentBytes: result.sentBytes,
        }));
        if(response.length != 0){
          this.modal!.style.display = 'block';
        }else{
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: "Le test a échoué, révisez votre configuration de test",
          })
        }
      }, (error: any) =>{
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: "Le test a échoué, révisez votre configuration de test",
        })
      });
  }

  onFtpSubmit() {
    if (!this.validateFtpForm()) {
      Swal.fire({
        icon: 'error',
        title: 'Erreur',
        text: 'Veuillez remplir tous les champs qui sont obligatoires',
      });
      return;
    }

    this.busy = this.performanceTestApiService
      .sendFtpJMeterRequest(this.ftp_request)
      .subscribe((response: any[]) => {
        this.testResults = response.map((result) => ({
          allThreads: result.allThreads,
          grpThreads: result.grpThreads,
          idleTime: result.IdleTime,
          dataType: result.dataType,
          connect: result.Connect,
          label: result.label,
          threadName: result.threadName,
          url: result.URL,
          responseCode: result.responseCode,
          latency: result.Latency,
          timestamp: result.timeStamp,
          elapsed: result.elapsed,
          success: result.success,
          bytes: result.bytes,
          responseMessage: result.responseMessage,
          failureMessage: result.failureMessage,
          sentBytes: result.sentBytes,
        }));
        if(response.length != 0){
          this.modal!.style.display = 'block';
        }else{
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: "Le test a échoué, révisez votre configuration de test",
          })
        }

      }, (error: any) =>{
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

  exportToPDF() {
    const doc = new jsPDF();
    const col = ["Data Type", "Connect", "Label", "Thread Name", "URL", "Response Code", "Latency", "Timestamp", "Elapsed", "Status", "Bytes", "Response Message", "Failure Message", "Sent Bytes"];
    const rows = this.testResults.map(result => [
      result.dataType,
      result.connect,
      result.label,
      result.threadName,
      result.url,
      result.responseCode,
      result.latency,
      result.timestamp,
      result.elapsed,
      result.success === 'true' ? 'Success' : 'Failed',
      result.bytes,
      result.responseMessage,
      result.failureMessage,
      result.sentBytes
    ]);

    (doc as any).autoTable({
      head: [col],
      body: rows,
      startY: 10,
      columnStyles: {
        0: { cellWidth: 10 },
        1: { cellWidth: 10 },
        2: { cellWidth: 15 },
        3: { cellWidth: 20 },
        4: { cellWidth: 20 },
        5: { cellWidth: 10 },
        6: { cellWidth: 10 },
        7: { cellWidth: 15 },
        8: { cellWidth: 10 },
        9: { cellWidth: 10 },
        10: { cellWidth: 10 },
        11: { cellWidth: 20 },
        12: { cellWidth: 20 },
        13: { cellWidth: 10 }
      }
    });
    doc.save('test-results.pdf');
  }
}