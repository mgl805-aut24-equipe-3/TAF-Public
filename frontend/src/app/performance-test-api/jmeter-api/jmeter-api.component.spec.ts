import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JmeterApiComponent } from './jmeter-api.component';
import { PerformanceTestApiService } from 'src/app/_services/performance-test-api.service';
import { of, throwError } from 'rxjs';
import Swal from 'sweetalert2';

/**
 * Suite de tests pour le composant JmeterApiComponent.
 */
describe('JmeterApiComponent', () => {
  let component: JmeterApiComponent;
  let fixture: ComponentFixture<JmeterApiComponent>;
  let performanceTestApiService: jasmine.SpyObj<PerformanceTestApiService>;

  /**
   * Configuration du module de test avant chaque test.
   * Cette méthode est exécutée une seule fois avant tous les tests.
   * Elle configure  le module de test avec les déclarations et les fournisseurs nécessaires.
   */
  beforeEach(async () => {
    const spy = jasmine.createSpyObj('PerformanceTestApiService', ['sendHttpJMeterRequest', 'sendFtpJMeterRequest']);

    await TestBed.configureTestingModule({
      declarations: [ JmeterApiComponent ],
      providers: [
        { provide: PerformanceTestApiService, useValue: spy }
      ]
    })
    .compileComponents();
  });

  /**
   * Initialisation du composant et du fixture avant chaque test.
   * Cette méthode est exécutée avant chaque test individuel.
   * Elle crée une instance du composant et déclenche la détection des changements.
   */
  beforeEach(() => {
    fixture = TestBed.createComponent(JmeterApiComponent);
    component = fixture.componentInstance;
    performanceTestApiService = TestBed.inject(PerformanceTestApiService) as jasmine.SpyObj<PerformanceTestApiService>;
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
   * Test pour vérifier que la méthode onHttpSubmit appelle sendHttpJMeterRequest.
   * Ce test simule un appel à la méthode onHttpSubmit et vérifie que la méthode sendHttpJMeterRequest du service est appelée.
   */
  it('devrait appeler sendHttpJMeterRequest lors de l\'appel de onHttpSubmit', () => {
    spyOn(component, 'validateHttpForm').and.returnValue(true);
    const mockResponse = [{ success: true }];
    performanceTestApiService.sendHttpJMeterRequest.and.returnValue(of(mockResponse));

    component.onHttpSubmit();

    expect(performanceTestApiService.sendHttpJMeterRequest).toHaveBeenCalled();
    expect(component.modal!.style.display).toBe('block');
  });

  /**
   * Test pour vérifier que SweetAlert s'affiche en cas d'erreur ou si la réponse est vide dans onHttpSubmit.
   */
  it('devrait afficher SweetAlert en cas d\'erreur ou si la réponse est vide dans onHttpSubmit', () => {
    spyOn(component, 'validateHttpForm').and.returnValue(true);
    performanceTestApiService.sendHttpJMeterRequest.and.returnValue(of([]));

    component.onHttpSubmit();

    expect(Swal.isVisible()).toBeTruthy();

    performanceTestApiService.sendHttpJMeterRequest.and.returnValue(throwError('error'));

    component.onHttpSubmit();

    expect(Swal.isVisible()).toBeTruthy();
  });

  /**
   * Test pour vérifier que la méthode onFtpSubmit appelle sendFtpJMeterRequest.
   * Ce test simule un appel à la méthode onFtpSubmit et vérifie que la méthode sendFtpJMeterRequest du service est appelée.
   */
  it('devrait appeler sendFtpJMeterRequest lors de l\'appel de onFtpSubmit', () => {
    spyOn(component, 'validateFtpForm').and.returnValue(true);

    const mockResponse = [{ success: true }];
    performanceTestApiService.sendFtpJMeterRequest.and.returnValue(of(mockResponse));

    component.onFtpSubmit();

    expect(performanceTestApiService.sendFtpJMeterRequest).toHaveBeenCalled();
    expect(component.modal!.style.display).toBe('block');
  });

  /**
   * Test pour vérifier que SweetAlert s'affiche en cas d'erreur ou si la réponse est vide dans onFtpSubmit.
   */
  it('devrait afficher SweetAlert en cas d\'erreur ou si la réponse est vide dans onFtpSubmit', () => {
    spyOn(component, 'validateFtpForm').and.returnValue(true);

    performanceTestApiService.sendFtpJMeterRequest.and.returnValue(of([]));

    component.onFtpSubmit();

    expect(Swal.isVisible()).toBeTruthy();

    performanceTestApiService.sendFtpJMeterRequest.and.returnValue(throwError('error'));

    component.onFtpSubmit();

    expect(Swal.isVisible()).toBeTruthy();
  });

  /**
   * Test pour vérifier que la méthode closeModal ferme la modal.
   * Ce test simule un appel à la méthode closeModal et vérifie que le style d'affichage de la modal est défini à "none".
   */
  it('devrait fermer la modal lors de l\'appel de closeModal', () => {
    component.modal = { style: { display: 'block' } } as HTMLElement;

    component.closeModal();

    expect(component.modal.style.display).toBe('none');
  });

  /**
   * Test pour vérifier que la méthode toggleForms affiche/masque correctement les formulaires HTTP et FTP.
   * Ce test simule un appel à la méthode toggleForms et vérifie que les formulaires sont affichés/masqués correctement en fonction de l'état du switch.
   */
  it('devrait afficher/masquer correctement les formulaires HTTP et FTP lors de l\'appel de toggleForms', () => {
    component.switchCheckbox = { checked: true } as HTMLInputElement;
    component.httpForm = { style: { display: 'block' } } as HTMLElement;
    component.ftpForm = { style: { display: 'none' } } as HTMLElement;
    component.switchLabel = { innerText: 'HTTP' } as HTMLElement;
    component.ftp_description = { style: { display: 'none' } } as HTMLElement;
    component.http_description = { style: { display: 'block' } } as HTMLElement;

    component.toggleForms();

    expect(component.httpForm.style.display).toBe('none');
    expect(component.ftpForm.style.display).toBe('block');
    expect(component.ftp_description.style.display).toBe('block');
    expect(component.http_description.style.display).toBe('none');
    expect(component.switchLabel.innerText).toBe('FTP');

    component.switchCheckbox.checked = false;

    component.toggleForms();

    expect(component.httpForm.style.display).toBe('block');
    expect(component.ftpForm.style.display).toBe('none');
    expect(component.ftp_description.style.display).toBe('none');
    expect(component.http_description.style.display).toBe('block');
    expect(component.switchLabel.innerText).toBe('HTTP');
  });
});
