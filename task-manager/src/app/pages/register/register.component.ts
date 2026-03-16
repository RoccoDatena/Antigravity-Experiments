import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css']
})
export class RegisterComponent {
    form: FormGroup;
    loading = false;
    error = '';
    success = '';

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {
        this.form = this.fb.group({
            username: ['', [Validators.required, Validators.minLength(3)]],
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });
    }

    submit(): void {
        if (this.form.invalid) return;
        this.loading = true;
        this.error = '';
        const { username, email, password } = this.form.value;
        this.authService.signup(username, email, password).subscribe({
            next: () => {
                this.success = 'Account created! Redirecting to login...';
                setTimeout(() => this.router.navigate(['/login']), 1800);
            },
            error: (err) => {
                this.error = err.error?.error || 'Registration failed';
                this.loading = false;
            }
        });
    }
}
