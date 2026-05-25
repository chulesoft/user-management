import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserEditComponent } from './user-edit.component';
import { UsersService } from '../../services/users.service';
import { ActivatedRoute, Router } from '@angular/router';

class RouterStub {
  navigateByUrl = jasmine.createSpy('navigateByUrl').and.resolveTo(true);
}

function routeWithId(id: string | null) {
  return {
    snapshot: {
      paramMap: {
        get: (key: string) => (key === 'id' ? id : null)
      }
    }
  };
}

describe('UserEditComponent', () => {
  let fixture: ComponentFixture<UserEditComponent>;
  let component: UserEditComponent;
  let service: jasmine.SpyObj<UsersService>;
  let router: RouterStub;

  beforeEach(async () => {
    service = jasmine.createSpyObj<UsersService>('UsersService', ['get', 'create', 'update']);
    router = new RouterStub();

    await TestBed.configureTestingModule({
      imports: [UserEditComponent],
      providers: [
        { provide: UsersService, useValue: service },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: routeWithId(null) }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create (create mode)', () => {
    expect(component).toBeTruthy();
    expect(component.isEdit).toBeFalse();
  });

  it('should not save when form invalid', async () => {
    component.form.patchValue({ firstName: '', lastName: '', email: 'bad' });
    await component.save();
    expect(service.create).not.toHaveBeenCalled();
    expect(service.update).not.toHaveBeenCalled();
  });

  it('should create user when no id', async () => {
    service.create.and.resolveTo({} as any);
    component.form.patchValue({ firstName: ' Omar ', lastName: ' Garcia ', email: 'omar@example.com', phone: ' ', active: true });

    await component.save();

    expect(service.create).toHaveBeenCalledWith({ firstName: 'Omar', lastName: 'Garcia', email: 'omar@example.com', phone: null, active: true });
    expect(router.navigateByUrl).toHaveBeenCalledWith('/users');
  });

  it('should update user when id present', async () => {
    service.get.and.resolveTo({ id: 7, firstName: 'A', lastName: 'B', email: 'a@b.com', phone: null, active: true } as any);
    service.update.and.resolveTo({} as any);

    await TestBed.resetTestingModule().configureTestingModule({
      imports: [UserEditComponent],
      providers: [
        { provide: UsersService, useValue: service },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: routeWithId('7') }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    await fixture.whenStable();
    expect(component.isEdit).toBeTrue();

    component.form.patchValue({ firstName: 'N', lastName: 'N', email: 'n@n.com', phone: '1', active: false });
    await component.save();

    expect(service.update).toHaveBeenCalledWith(7, { firstName: 'N', lastName: 'N', email: 'n@n.com', phone: '1', active: false });
    expect(router.navigateByUrl).toHaveBeenCalledWith('/users');
  });

  it('should set error when load fails', async () => {
    service.get.and.rejectWith(new Error('boom'));

    await TestBed.resetTestingModule().configureTestingModule({
      imports: [UserEditComponent],
      providers: [
        { provide: UsersService, useValue: service },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: routeWithId('8') }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    await component.load();
    expect(component.error).toBe('boom');
    expect(component.loading).toBeFalse();
  });

  it('should set error when save fails', async () => {
    service.create.and.rejectWith(new Error('nope'));
    component.form.patchValue({ firstName: 'O', lastName: 'G', email: 'omar@example.com', phone: '', active: true });

    await component.save();

    expect(component.error).toBe('nope');
    expect(component.loading).toBeFalse();
  });
});
