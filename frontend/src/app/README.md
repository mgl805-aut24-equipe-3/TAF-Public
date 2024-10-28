# Plan de test

## Test de création du composant
- Vérifier que le composant est créé correctement.

## Test de la méthode `ngOnInit`
- Vérifier que `isLoggedIn` est défini correctement en fonction de la présence du token.
- Vérifier que les rôles de l'utilisateur sont récupérés correctement.
- Vérifier que `showAdminBoard` est défini correctement en fonction des rôles de l'utilisateur.
- Vérifier que `fullName` est défini correctement.

## Test de la méthode `logout`
- Vérifier que la méthode `signOut` du service `TokenStorageService` est appelée et que la page est rechargée.

```ts
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { TokenStorageService } from './_services/token-storage.service';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let tokenStorageService: jasmine.SpyObj<TokenStorageService>;

  beforeEach(async () => {
    const tokenStorageSpy = jasmine.createSpyObj('TokenStorageService', ['getToken', 'getUser', 'signOut']);

    await TestBed.configureTestingModule({
      declarations: [AppComponent],
      providers: [{ provide: TokenStorageService, useValue: tokenStorageSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    tokenStorageService = TestBed.inject(TokenStorageService) as jasmine.SpyObj<TokenStorageService>;
  });

  it('devrait créer le composant', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Test pour vérifier que la méthode ngOnInit initialise correctement les propriétés.
   * Ce test simule un appel à la méthode ngOnInit et vérifie que les propriétés isLoggedIn, roles, showAdminBoard et fullName sont définies correctement.
   */
  it('devrait initialiser correctement les propriétés lors de l\'appel de ngOnInit', () => {
    const mockUser = { roles: ['ROLE_USER'], fullName: 'John Doe' };
    tokenStorageService.getToken.and.returnValue('fake-token');
    tokenStorageService.getUser.and.returnValue(mockUser);

    component.ngOnInit();

    expect(component.isLoggedIn).toBeTrue();
    expect((component as any).roles).toEqual(['ROLE_USER']); // Accessing private property
    expect(component.showAdminBoard).toBeFalse();
    expect(component.fullName).toBe('John Doe');
  });

  /**
   * Test pour vérifier que la méthode logout appelle signOut et recharge la page.
   * Ce test simule un appel à la méthode logout et vérifie que la méthode signOut du service TokenStorageService est appelée et que la page est rechargée.
   */
  it('devrait appeler signOut et recharger la page lors de l\'appel de logout', () => {
    spyOn(window.location, 'reload');

    component.logout();

    expect(tokenStorageService.signOut).toHaveBeenCalled();
    expect(window.location.reload).toHaveBeenCalled();
  });
});
```

# Explications

## Importations
Le code commence par importer les modules et services nécessaires pour les tests :
- `ComponentFixture` et `TestBed` de `@angular/core/testing` pour configurer et créer le composant à tester.
- `AppComponent` qui est le composant à tester.
- `TokenStorageService` qui est le service utilisé par le composant.

## Suite de tests pour le composant `AppComponent`
La suite de tests est définie à l'aide de `describe`, qui regroupe plusieurs tests pour le composant `AppComponent`.

### Variables globales
- `component`: Instance du composant à tester.
- `fixture`: Instance de `ComponentFixture` pour le composant.
- `tokenStorageService`: SpyObj pour le service `TokenStorageService`.

### Configuration du module de test
La première `beforeEach` configure le module de test :
- Crée un spy pour `TokenStorageService` avec les méthodes simulées `getToken`, `getUser`, et `signOut`.
- Configure le module de test avec `TestBed.configureTestingModule`, en déclarant le composant et en fournissant le spy pour le service.
- Injecte le spy du service dans la variable `tokenStorageService`.

### Initialisation du composant et du fixture
La seconde `beforeEach` crée une instance du composant et initialise le fixture avant chaque test :
- `fixture = TestBed.createComponent(AppComponent)`: Crée une instance du composant.
- `component = fixture.componentInstance`: Initialise l'instance du composant.
- `fixture.detectChanges()`: Détecte les changements pour initialiser le composant.

### Test de création du composant
Le test `it('devrait créer le composant')` vérifie que le composant est créé correctement :
- `expect(component).toBeTruthy()`: Vérifie que l'instance du composant est définie.

### Test de la méthode `ngOnInit`
Le test `it('devrait initialiser correctement les propriétés lors de l'appel de ngOnInit')` vérifie que la méthode `ngOnInit` initialise correctement les propriétés :
- Crée un utilisateur simulé `mockUser` avec des rôles et un nom complet.
- Configure le spy pour retourner un token simulé et l'utilisateur simulé.
- Appelle la méthode `ngOnInit` du composant.
- Vérifie que `isLoggedIn` est défini à `true`.
- Vérifie que les rôles de l'utilisateur sont définis correctement.
- Vérifie que `showAdminBoard` est défini à `false`.
- Vérifie que `fullName` est défini correctement.

### Test de la méthode `logout`
Le test `it('devrait appeler signOut et recharger la page lors de l'appel de logout')` vérifie que la méthode `logout` appelle `signOut` et recharge la page :
- Utilise `spyOn` pour espionner la méthode `reload` de `window.location`.
- Appelle la méthode `logout` du composant.
- Vérifie que `signOut` a été appelée.
- Vérifie que `window.location.reload` a été appelée.
