import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { UsersService } from '../../services/users.service';
import { UserRequest } from '../../models/user.model';

@Component({
  selector: 'app-user-edit',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './user-edit.component.html',
  styleUrl: './user-edit.component.css'
})
export class UserEditComponent {
  loading = false;
  error: string | null = null;

  id: number | null = null;

  form = this.fb.nonNullable.group({
    firstName: ['', [Validators.required, Validators.maxLength(80)]],
    lastName: ['', [Validators.required, Validators.maxLength(80)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(160)]],
    phone: [''],
    active: [true]
  });

  constructor(
    private fb: FormBuilder,
    private users: UsersService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;

    if (this.id) {
      void this.load();
    }
  }

  get isEdit(): boolean {
    return this.id !== null;
  }

  async load(): Promise<void> {
    if (!this.id) return;

    this.loading = true;
    this.error = null;

    try {
      const user = await this.users.get(this.id);
      this.form.patchValue({
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        phone: user.phone ?? '',
        active: user.active
      });
    } catch (e: any) {
      this.error = e?.message ?? 'Failed to load user';
    } finally {
      this.loading = false;
    }
  }

  async save(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const payload: UserRequest = {
      firstName: value.firstName.trim(),
      lastName: value.lastName.trim(),
      email: value.email.trim(),
      phone: value.phone.trim() ? value.phone.trim() : null,
      active: value.active
    };

    this.loading = true;
    this.error = null;

    try {
      if (this.id) {
        await this.users.update(this.id, payload);
      } else {
        await this.users.create(payload);
      }
      await this.router.navigateByUrl('/users');
    } catch (e: any) {
      this.error = e?.message ?? 'Failed to save user';
      this.loading = false;
    }
  }
}
