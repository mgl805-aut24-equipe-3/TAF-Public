import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-busy-spinner',
  templateUrl: './busy-spinner.component.html',
  styleUrls: ['./busy-spinner.component.less']
})
export class BusySpinnerComponent implements OnInit {

  @Input() message: string = 'Veuillez patienter';

  constructor() { }

  ngOnInit(): void {
  }

}