import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PaginationComponent } from './pagination.component';

describe('PaginationComponent', () => {
  let fixture: ComponentFixture<PaginationComponent>;
  let component: PaginationComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [PaginationComponent] });
    fixture = TestBed.createComponent(PaginationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should emit previous page', () => {
    spyOn(component.pageChange, 'emit');
    component.page = 2;
    component.prev();
    expect(component.pageChange.emit).toHaveBeenCalledWith(1);
  });

  it('should not emit previous page when already at 0', () => {
    spyOn(component.pageChange, 'emit');
    component.page = 0;
    component.prev();
    expect(component.pageChange.emit).not.toHaveBeenCalled();
  });

  it('should emit next page', () => {
    spyOn(component.pageChange, 'emit');
    component.page = 0;
    component.totalPages = 3;
    component.next();
    expect(component.pageChange.emit).toHaveBeenCalledWith(1);
  });

  it('should not emit next page when already at last', () => {
    spyOn(component.pageChange, 'emit');
    component.page = 2;
    component.totalPages = 3;
    component.next();
    expect(component.pageChange.emit).not.toHaveBeenCalled();
  });
});
