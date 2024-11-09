import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BusySpinnerComponent } from './busy-spinner.component';

describe('BusySpinnerComponent', () => {
  let component: BusySpinnerComponent;
  let fixture: ComponentFixture<BusySpinnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BusySpinnerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BusySpinnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
