import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JmeterApiComponent } from './jmeter-api.component';
import { FormBuilder } from '@angular/forms';
import { PerformanceTestApiService } from 'src/app/_services/performance-test-api.service';
import { of, throwError } from 'rxjs';
import Swal from 'sweetalert2';

/**
 * Suite de tests pour le composant JmeterApiComponent.
 */
describe('JmeterApiComponent', () => {
  let component: JmeterApiComponent;
  let fixture: ComponentFixture<JmeterApiComponent>;
  let mockService: jasmine.SpyObj<PerformanceTestApiService>;

  /**
   * Configuration du module de test avant chaque test.
   */
  beforeEach(async () => {
    const serviceSpy = jasmine.createSpyObj('PerformanceTestApiService', ['sendHttpJMeterRequest', 'sendFtpJMeterRequest']);

    await TestBed.configureTestingModule({
      declarations: [JmeterApiComponent],
      providers: [
        FormBuilder,
        { provide: PerformanceTestApiService, useValue: serviceSpy }
      ]
    }).compileComponents();

    mockService = TestBed.inject(PerformanceTestApiService) as jasmine.SpyObj<PerformanceTestApiService>;
    fixture = TestBed.createComponent(JmeterApiComponent);
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
   * Test pour vérifier que la visibilité de la barre latérale HTTP est basculée.
   */
  it('devrait basculer la visibilité de la barre latérale HTTP', () => {
    component.toggleHttpSidebar();
    expect(component.isHttpSidebarVisible).toBeTrue();
    component.toggleHttpSidebar();
    expect(component.isHttpSidebarVisible).toBeFalse();
  });

  /**
   * Test pour vérifier que la visibilité de la barre latérale FTP est basculée.
   */
  it('devrait basculer la visibilité de la barre latérale FTP', () => {
    component.toggleFtpSidebar();
    expect(component.isFtpSidebarVisible).toBeTrue();
    component.toggleFtpSidebar();
    expect(component.isFtpSidebarVisible).toBeFalse();
  });

  /**
   * Test pour vérifier que la visibilité des boutons est mise à jour en fonction de switchCheckbox.
   */
  it('devrait mettre à jour la visibilité des boutons en fonction de switchCheckbox', () => {
    component.switchCheckbox = document.createElement('input');
    component.switchCheckbox.type = 'checkbox';

    component.switchCheckbox.checked = true;
    component.updateButtonVisibility();
    expect(component.showHttpButton).toBeFalse();
    expect(component.showFtpButton).toBeTrue();

    component.switchCheckbox.checked = false;
    component.updateButtonVisibility();
    expect(component.showHttpButton).toBeTrue();
    expect(component.showFtpButton).toBeFalse();
  });

  /**
   * Test pour vérifier que PerformanceTestApiService est appelé pour une requête HTTP lors de la soumission d'un formulaire valide.
   */
  it('devrait appeler PerformanceTestApiService pour une requête HTTP lors de la soumission d\'un formulaire valide', () => {
    mockService.sendHttpJMeterRequest.and.returnValue(of([]));
    spyOn(component, 'validateHttpForm').and.returnValue(true);

    component.onHttpSubmit();
    expect(mockService.sendHttpJMeterRequest).toHaveBeenCalled();
  });

  /**
   * Test pour vérifier qu'une alerte d'erreur est affichée en cas d'échec de la requête HTTP.
   */
  it('devrait afficher une alerte d\'erreur en cas d\'échec de la requête HTTP', () => {
    mockService.sendHttpJMeterRequest.and.returnValue(throwError(() => new Error('Test error')));
    spyOn(Swal, 'fire');
    spyOn(component, 'validateHttpForm').and.returnValue(true);

    component.onHttpSubmit();
    expect(Swal.fire).toHaveBeenCalledWith(jasmine.objectContaining({
      icon: 'error',
      title: 'Erreur',
      text: "Le test a échoué, révisez votre configuration de test"
    }));
  });

  /**
   * Test pour vérifier que PerformanceTestApiService est appelé pour une requête FTP lors de la soumission d'un formulaire valide.
   */
  it('devrait appeler PerformanceTestApiService pour une requête FTP lors de la soumission d\'un formulaire valide', () => {
    mockService.sendFtpJMeterRequest.and.returnValue(of([]));
    spyOn(component, 'validateFtpForm').and.returnValue(true);

    component.onFtpSubmit();
    expect(mockService.sendFtpJMeterRequest).toHaveBeenCalled();
  });

  /**
   * Test pour vérifier qu'une alerte d'erreur est affichée en cas d'échec de la requête FTP.
   */
  it('devrait afficher une alerte d\'erreur en cas d\'échec de la requête FTP', () => {
    mockService.sendFtpJMeterRequest.and.returnValue(throwError(() => new Error('Test error')));
    spyOn(Swal, 'fire');
    spyOn(component, 'validateFtpForm').and.returnValue(true);

    component.onFtpSubmit();
    expect(Swal.fire).toHaveBeenCalledWith(jasmine.objectContaining({
      icon: 'error',
      title: 'Erreur',
      text: "Le test a échoué, révisez votre configuration de test"
    }));
  });

  // /**
  //  * Test pour vérifier que les formulaires sont réinitialisés lors de l'appel de resetForms.
  //  */
  // it('devrait réinitialiser les formulaires lors de l\'appel de resetForms', () => {
  //   const httpForm = document.createElement('form');
  //   const ftpForm = document.createElement('form');
  //   spyOn(httpForm, 'reset');
  //   spyOn(ftpForm, 'reset');

  //   component.httpForm = httpForm;
  //   component.ftpForm = ftpForm;
  //   component.resetForms();

  //   // expect(httpForm.reset).toHaveBeenCalled();
  //   // expect(ftpForm.reset).toHaveBeenCalled();
  // });

  /**
   * Test pour vérifier que la modal est fermée lors de l'appel de closeModal.
   */
  it('devrait fermer la modal lors de l\'appel de closeModal', () => {
    component.modal = document.createElement('div');
    component.modal.style.display = 'block';

    component.closeModal();
    expect(component.modal.style.display).toBe('none');
  });

  /**
   * Test pour vérifier qu'une alerte d'erreur est affichée s'il n'y a pas de rapport disponible.
   */
  it('devrait afficher une alerte d\'erreur s\'il n\'y a pas de rapport disponible', () => {
    spyOn(Swal, 'fire');

    component.testResult = [];
    component.showLatestReport();
    expect(Swal.fire).toHaveBeenCalledWith(jasmine.objectContaining({
      icon: 'error',
      title: 'Erreur',
      text: "Aucun rapport disponible"
    }));
  });
});
