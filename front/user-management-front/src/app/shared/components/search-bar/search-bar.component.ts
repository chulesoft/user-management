import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  template: `
    <div class="row">
      <input class="input" type="search" [value]="value" (input)="onInput($event)" placeholder="Search by name or email" />
      <button class="btn" type="button" (click)="submit()">Search</button>
      <button class="btn" type="button" (click)="clear()" [disabled]="!value">Clear</button>
    </div>
  `
})
export class SearchBarComponent {
  @Output() search = new EventEmitter<string>();
  value = '';

  onInput(evt: Event): void {
    const target = evt.target as HTMLInputElement;
    this.value = target.value;
  }

  submit(): void {
    this.search.emit(this.value);
  }

  clear(): void {
    this.value = '';
    this.search.emit('');
  }
}
