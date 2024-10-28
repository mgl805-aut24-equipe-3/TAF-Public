import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PerformanceTestApiService } from './performance-test-api.service';
import { environment } from '../../environments/environment';
import { GatlingRequest } from '../performance-test-api/gatling-api/gatling-request';
import { JMeterHttpRequest } from '../performance-test-api/jmeter-api/jmeter-http-request';
import { JMeterFTPRequest } from '../performance-test-api/jmeter-api/jmeter-ftp-request';

/**
 * Suite de tests pour le service PerformanceTestApiService.
 */
describe('PerformanceTestApiService', () => {
  let service: PerformanceTestApiService;
  let httpMock: HttpTestingController;

  /**
   * Configuration du module de test avant chaque test.
   * Cette méthode est exécutée une seule fois avant tous les tests.
   * Elle configure le module de test avec les déclarations et les fournisseurs nécessaires.
   */
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PerformanceTestApiService]
    });

    service = TestBed.inject(PerformanceTestApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  /**
   * Nettoyage après chaque test.
   * Cette méthode est exécutée après chaque test individuel.
   * Elle s'assure qu'il n'y a pas de requêtes HTTP en attente.
   */
  afterEach(() => {
    httpMock.verify();
  });

  /**
   * Test pour vérifier que la méthode sendGatlingRequest envoie une requête POST à l'URL correcte avec les bonnes données.
   * Ce test simule un appel à la méthode sendGatlingRequest et vérifie que la requête HTTP est envoyée avec les bonnes données.
   */
  it('devrait envoyer une requête POST correcte lors de l\'appel de sendGatlingRequest', () => {
    const mockResponse = { success: true };
    const request: GatlingRequest = {
      testBaseUrl: '',
      testScenarioName: '',
      testRequestName: '',
      testUri: '',
      testRequestBody: '',
      testMethodType: '',
      testUsersNumber: 0
    };

    service.sendGatlingRequest(request).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/gatling/runSimulation`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(mockResponse);
  });

  /**
   * Test pour vérifier que la méthode sendHttpJMeterRequest envoie une requête POST à l'URL correcte avec les bonnes données.
   * Ce test simule un appel à la méthode sendHttpJMeterRequest et vérifie que la requête HTTP est envoyée avec les bonnes données.
   */
  it('devrait envoyer une requête POST correcte lors de l\'appel de sendHttpJMeterRequest', () => {
    const mockResponse = { success: true };
    const request: JMeterHttpRequest = {
      nbThreads: '',
      rampTime: '',
      duration: '',
      domain: '',
      port: '',
      protocol: '',
      path: '',
      method: '',
      loop: '',
      data: ''
    };

    service.sendHttpJMeterRequest(request).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/performance/jmeter/http`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(mockResponse);
  });

  /**
   * Test pour vérifier que la méthode sendFtpJMeterRequest envoie une requête POST à l'URL correcte avec les bonnes données.
   * Ce test simule un appel à la méthode sendFtpJMeterRequest et vérifie que la requête HTTP est envoyée avec les bonnes données.
   */
  it('devrait envoyer une requête POST correcte lors de l\'appel de sendFtpJMeterRequest', () => {
    const mockResponse = { success: true };
    const request: JMeterFTPRequest = {
      nbThreads: '',
      rampTime: '',
      duration: '',
      domain: '',
      port: '',
      method: '',
      remotefile: '',
      localfile: '',
      username: '',
      password: '',
      loop: ''
    };

    service.sendFtpJMeterRequest(request).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/performance/jmeter/ftp`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(mockResponse);
  });
});
