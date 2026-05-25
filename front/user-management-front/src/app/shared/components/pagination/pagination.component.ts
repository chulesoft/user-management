import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  standalone: true,
  template: `
    <div class="row" style="margin-top:12px">
      <button class="btn" type="button" (click)="prev()" [disabled]="page<=0">Prev</button>
      <span class="badge">Page {{page+1}} / {{totalPages || 1}}</span>
      <button class="btn" type="button" (click)="next()" [disabled]="page>=totalPages-1">Next</button>
      <span class="spacer"></span>
      <span class="badge">Total: {{totalElements}}</span>
    </div>
  `
})
export class PaginationComponent {
  @Input() page = 0;
  @Input() totalPages = 0;
  @Input() totalElements = 0;
  @Output() pageChange = new EventEmitter<number>();

  prev(): void {
    if (this.page > 0) this.pageChange.emit(this.page - 1);
  }

  next(): void {
    if (this.page < this.totalPages - 1) this.pageChange.emit(this.page + 1);
  }
}
