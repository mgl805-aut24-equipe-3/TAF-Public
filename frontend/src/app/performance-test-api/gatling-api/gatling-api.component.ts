import { Component, OnInit } from '@angular/core';
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
  span: HTMLElement | null = null;
  testResult: any;
  testLog: String = "";

  busy: Subscription | undefined;

  request: GatlingRequest = new GatlingRequest();

  constructor(private performanceTestApiService: PerformanceTestApiService) { }

  ngOnInit(): void {
    this.modal = document.getElementById("myModal");
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

  closeModal() {
    this.modal!.style.display = "none";
  }
}