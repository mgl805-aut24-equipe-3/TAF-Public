# Plan de test

## Fichier : `frontend\src\app\performance-test-api\performance-test-api.component.spec.ts`

### Test de création du composant
- Vérifier que le composant est créé correctement.

### Test de la méthode `ngOnInit`
- Vérifier que `isLoggedIn` est défini correctement en fonction de la présence du token.
- Vérifier que les rôles de l'utilisateur sont récupérés correctement.
- Vérifier que `showAdminBoard` est défini correctement en fonction des rôles de l'utilisateur.
- Vérifier que `fullName` est défini correctement.

### Test de la méthode `logout`
- Vérifier que la méthode `signOut` du service `TokenStorageService` est appelée et que la page est rechargée.

# Code pour `performance-test-api.component.spec.ts`


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

## Accès aux propriétés privées
Pour accéder à une propriété privée dans un test, vous pouvez utiliser `(component as any).roles`. Cela contourne la restriction d'accès aux propriétés privées.

## Spies et mocks
Utilisation de `jasmine.createSpyObj` pour créer des mocks des méthodes du service `TokenStorageService`.

## Initialisation des tests
Configuration du module de test avec `TestBed.configureTestingModule` et initialisation des composants et services nécessaires.

## Tests unitaires

### Création du composant
Vérifie que le composant est créé correctement.

### Initialisation des propriétés
Vérifie que `ngOnInit` initialise correctement les propriétés du composant.

### Déconnexion
Vérifie que la méthode `logout` appelle `signOut` et recharge la page.
