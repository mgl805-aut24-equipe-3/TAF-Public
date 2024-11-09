# Plan de test `frontend\src\app\performance-test-api\gatling-api\gatling-api.component.spec.ts`

## Test de création du composant
- Vérifier que le composant est créé correctement.

## Test de la méthode `onSubmit`
- Vérifier que la méthode `sendGatlingRequest` du service est appelée.
- Vérifier que la réponse est traitée correctement.

## Test de la méthode `closeModal`
- Vérifier que la modal est fermée correctement.

# Explications

## Test de création du composant
Vérifie que le composant `GatlingApiComponent` est créé correctement. Ce test s'assure que l'instance du composant est initialisée sans erreurs.

## Test de `onSubmit`
Vérifie que la méthode `sendGatlingRequest` du service `PerformanceTestApiService` est appelée lorsque `onSubmit` est exécutée. Ce test s'assure que la méthode du service est invoquée et que la réponse est traitée correctement.

## Test de `closeModal`
Vérifie que la méthode `closeModal` ferme la modal en définissant son style d'affichage à "none". Ce test s'assure que la modal est fermée correctement lorsque la méthode est appelée.

# Code `gatling-api.component.spec.ts`

```ts
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
    const mockResponse = { success: true };
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
```

# Explications du code

## Importations
Le code commence par importer les modules et services nécessaires pour les tests :
- `ComponentFixture` et `TestBed` de `@angular/core/testing` pour configurer et créer le composant à tester.
- `GatlingApiComponent` qui est le composant à tester.
- `PerformanceTestApiService` qui est le service utilisé par le composant.
- `of` de `rxjs` pour créer des Observables simulées.

## Suite de tests pour le composant `GatlingApiComponent`
La suite de tests est définie à l'aide de `describe`, qui regroupe plusieurs tests pour le composant `GatlingApiComponent`.

### Variables globales
- `component`: Instance du composant à tester.
- `fixture`: Instance de `ComponentFixture` pour le composant.
- `performanceTestApiService`: SpyObj pour le service `PerformanceTestApiService`.

### Configuration du module de test
La première `beforeEach` configure le module de test :
- Crée un spy pour `PerformanceTestApiService` avec une méthode simulée `sendGatlingRequest`.
- Configure le module de test avec `TestBed.configureTestingModule`, en déclarant le composant et en fournissant le spy pour le service.
- Injecte le spy du service dans la variable `performanceTestApiService`.

### Initialisation du composant et du fixture
La seconde `beforeEach` crée une instance du composant et initialise le fixture avant chaque test :
- `fixture = TestBed.createComponent(GatlingApiComponent)`: Crée une instance du composant.
- `component = fixture.componentInstance`: Initialise l'instance du composant.
- `fixture.detectChanges()`: Détecte les changements pour initialiser le composant.

### Test de création du composant
Le test `it('devrait créer le composant')` vérifie que le composant est créé correctement :
- `expect(component).toBeTruthy()`: Vérifie que l'instance du composant est définie.

### Test de la méthode `onSubmit`
Le test `it('devrait appeler sendGatlingRequest lors de l'appel de onSubmit')` vérifie que la méthode `sendGatlingRequest` du service est appelée lors de l'appel de `onSubmit` :
- Crée une réponse simulée `mockResponse`.
- Configure le spy pour retourner cette réponse simulée.
- Appelle la méthode `onSubmit` du composant.
- Vérifie que `sendGatlingRequest` a été appelée.

### Test de la méthode `closeModal`
Le test `it('devrait fermer la modal lors de l'appel de closeModal')` vérifie que la méthode `closeModal` ferme la modal :
- Définit un élément modal simulé avec un style d'affichage initial à `block`.
- Appelle la méthode `closeModal` du composant.
- Vérifie que le style d'affichage de la modal est défini à `none`.
- Si la modal est `null`, le test échoue avec `fail('Modal is null')`.
