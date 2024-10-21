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

  modal = document.getElementById("myModal");
  span = document.getElementsByClassName("close")[0];
  testResult: any;
  testLog: String = "";
  reportFilePath: String = "";

  busy: Subscription | undefined;

  request: GatlingRequest = new GatlingRequest();

  constructor(private performanceTestApiService: PerformanceTestApiService) { }

  ngOnInit(): void {
    this.modal = document.getElementById("myModal");
    this.span = document.getElementsByClassName("close")[0];
  }

  onSubmit() {
    this.busy = this.performanceTestApiService.sendGatlingRequest(this.request)
      .subscribe((response: any) => {
        this.modal!.style.display = "block";

        const pattern = /(.+)\n?/g;
        const matches: String[] = Array.from(response.message.matchAll(pattern));
        const arrayOfStrings = matches.map(match => match[0]);
        this.testResult = arrayOfStrings;

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
