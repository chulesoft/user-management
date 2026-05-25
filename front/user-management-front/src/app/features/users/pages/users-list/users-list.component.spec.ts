import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UsersListComponent } from './users-list.component';
import { UsersService } from '../../services/users.service';
import { RouterTestingModule } from '@angular/router/testing';

function page(content: any[] = [], pageNum = 0, totalPages = 1, totalElements = content.length) {
  return { content, page: pageNum, size: 10, totalElements, totalPages, first: pageNum === 0, last: pageNum === totalPages - 1 };
}

describe('UsersListComponent', () => {
  let fixture: ComponentFixture<UsersListComponent>;
  let component: UsersListComponent;
  let service: jasmine.SpyObj<UsersService>;

  beforeEach(async () => {
    service = jasmine.createSpyObj<UsersService>('UsersService', ['list', 'delete']);
    service.list.and.resolveTo(page([{ id: 1, firstName: 'O', lastName: 'G', email: 'e', phone: null, active: true, deleted: false, version: 0, createdAt: '', updatedAt: '', createdBy: 'x', updatedBy: 'y' }]));
    service.delete.and.resolveTo();

    await TestBed.configureTestingModule({
      imports: [UsersListComponent, RouterTestingModule],
      providers: [{ provide: UsersService, useValue: service }]
    }).compileComponents();

    fixture = TestBed.createComponent(UsersListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load users on init', async () => {
    expect(component).toBeTruthy();
    await fixture.whenStable();
    expect(service.list).toHaveBeenCalled();
    expect(component.data.content.length).toBe(1);
  });

  it('should reset page and reload on search', async () => {
    component.page = 2;
    service.list.calls.reset();

    await component.onSearch('omar');

    expect(component.page).toBe(0);
    expect(component.search).toBe('omar');
    expect(service.list).toHaveBeenCalledWith(jasmine.objectContaining({ search: 'omar', page: 0 }));
  });

  it('should change page and reload', async () => {
    service.list.calls.reset();

    await component.onPageChange(3);

    expect(component.page).toBe(3);
    expect(service.list).toHaveBeenCalledWith(jasmine.objectContaining({ page: 3 }));
  });

  it('should delete user then reload and adjust page if last item', async () => {
    component.data = page([{ id: 9, firstName: 'A', lastName: 'B', email: 'x', phone: null, active: true, deleted: false, version: 0, createdAt: '', updatedAt: '', createdBy: 'x', updatedBy: 'y' }], 1, 3, 21);
    component.page = 1;

    service.list.calls.reset();

    await component.onDelete(component.data.content[0]);

    expect(service.delete).toHaveBeenCalledWith(9);
    expect(service.list).toHaveBeenCalled();
    expect(component.page).toBe(0);
  });

  it('should set error on load failure', async () => {
    service.list.and.rejectWith(new Error('boom'));

    await component.load();

    expect(component.error).toBe('boom');
    expect(component.loading).toBeFalse();
  });

  it('trackById should return id', () => {
    expect(component.trackById(0, { id: 5 } as any)).toBe(5);
  });
});
