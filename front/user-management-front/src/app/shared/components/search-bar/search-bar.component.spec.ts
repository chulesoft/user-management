import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SearchBarComponent } from './search-bar.component';

describe('SearchBarComponent', () => {
  let fixture: ComponentFixture<SearchBarComponent>;
  let component: SearchBarComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [SearchBarComponent] });
    fixture = TestBed.createComponent(SearchBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should emit search value on submit', () => {
    spyOn(component.search, 'emit');
    component.value = 'omar';
    component.submit();
    expect(component.search.emit).toHaveBeenCalledWith('omar');
  });

  it('should clear value and emit empty string', () => {
    spyOn(component.search, 'emit');
    component.value = 'x';
    component.clear();
    expect(component.value).toBe('');
    expect(component.search.emit).toHaveBeenCalledWith('');
  });

  it('should update value on input', () => {
    const input: HTMLInputElement = fixture.nativeElement.querySelector('input');
    input.value = 'abc';
    input.dispatchEvent(new Event('input'));
    expect(component.value).toBe('abc');
  });
});
