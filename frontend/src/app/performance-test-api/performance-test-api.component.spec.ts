import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { PerformanceTestApiComponent } from './performance-test-api.component';
import { RouterTestingModule } from '@angular/router/testing';

/**
 * Suite de tests pour le composant PerformanceTestApiComponent.
 */
describe('PerformanceTestApiComponent', () => {
  let component: PerformanceTestApiComponent;
  let fixture: ComponentFixture<PerformanceTestApiComponent>;
  let router: Router;

  /**
   * Configuration du module de test avant chaque test.
   * Cette méthode est exécutée une seule fois avant tous les tests.
   * Elle configure le module de test avec les déclarations et les fournisseurs nécessaires.
   */
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PerformanceTestApiComponent ],
      imports: [ RouterTestingModule ]
    })
    .compileComponents();

    router = TestBed.inject(Router);
  });

  /**
   * Initialisation du composant et du fixture avant chaque test.
   * Cette méthode est exécutée avant chaque test individuel.
   * Elle crée une instance du composant et déclenche la détection des changements.
   */
  beforeEach(() => {
    fixture = TestBed.createComponent(PerformanceTestApiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /**
   * Test pour vérifier que le composant est créé correctement.
   * Ce test vérifie que l'instance du composant est créée et qu'elle est définie.
   */
  it('devrait créer le composant', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Test pour vérifier que la méthode runGatlingTest navigue vers le composant de test Gatling.
   * Ce test simule un appel à la méthode runGatlingTest et vérifie que la navigation vers '/gatling-test' est effectuée.
   */
  it('devrait naviguer vers le composant de test Gatling lors de l\'appel de runGatlingTest', () => {
    const navigateSpy = spyOn(router, 'navigate');

    component.runGatlingTest();

    expect(navigateSpy).toHaveBeenCalledWith(['/gatling-test']);
  });

  /**
   * Test pour vérifier que la méthode runJMeterTest navigue vers le composant de test JMeter.
   * Ce test simule un appel à la méthode runJMeterTest et vérifie que la navigation vers '/jmeter-test' est effectuée.
   */
  it('devrait naviguer vers le composant de test JMeter lors de l\'appel de runJMeterTest', () => {
    const navigateSpy = spyOn(router, 'navigate');

    component.runJMeterTest();

    expect(navigateSpy).toHaveBeenCalledWith(['/jmeter-test']);
  });
});
