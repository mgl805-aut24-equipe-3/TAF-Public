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
    const reloadSpy = spyOn(window.location, 'reload').and.callFake(() => {});

    component.logout();

    expect(tokenStorageService.signOut).toHaveBeenCalled();
    expect(reloadSpy).toHaveBeenCalled();
  });
});
