import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GatlingApiComponent } from './gatling-api.component';
import { PerformanceTestApiService } from '../../_services/performance-test-api.service';
import { of } from 'rxjs';

/**
 * Suite de tests pour le composant GatlingApiComponent.
 */
describe('GatlingApiComponent', () => {
  let component: GatlingApiComponent;
  let fixture: ComponentFixture<GatlingApiComponent>;
  let performanceTestApiService: jasmine.SpyObj<PerformanceTestApiService>;

  /**
   * Configuration du module de test avant chaque test.
   */
  beforeEach(async () => {
    const spy = jasmine.createSpyObj('PerformanceTestApiService', ['sendGatlingRequest']);

    await TestBed.configureTestingModule({
      declarations: [ GatlingApiComponent ],
      providers: [
        { provide: PerformanceTestApiService, useValue: spy }
      ]
    })
    .compileComponents();

    performanceTestApiService = TestBed.inject(PerformanceTestApiService) as jasmine.SpyObj<PerformanceTestApiService>;
  });

  /**
   * Initialisation du composant et du fixture avant chaque test.
   */
  beforeEach(() => {
    fixture = TestBed.createComponent(GatlingApiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /**
   * Test pour vérifier que le composant est créé correctement.
   */
  it('devrait créer le composant', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Test pour vérifier que la méthode onSubmit appelle sendGatlingRequest.
   */
  it('devrait appeler sendGatlingRequest lors de l\'appel de onSubmit', () => {
    const mockResponse = { message: 'OK' };
    performanceTestApiService.sendGatlingRequest.and.returnValue(of(mockResponse));

    component.onSubmit();

    expect(performanceTestApiService.sendGatlingRequest).toHaveBeenCalled();
  });

  /**
   * Test pour vérifier que la méthode closeModal ferme la modal.
   */
  it('devrait fermer la modal lors de l\'appel de closeModal', () => {
    const modalElement = { style: { display: 'block' } };
    component.modal = modalElement as any;

    component.closeModal();

    if (component.modal) {
      expect(component.modal.style.display).toBe('none');
    } else {
      fail('Modal is null');
    }
  });
});
