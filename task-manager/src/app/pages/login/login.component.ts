import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent {
    form: FormGroup;
    loading = false;
    error = '';

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {
        if (this.authService.isLoggedIn()) this.router.navigate(['/dashboard']);
        this.form = this.fb.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        });
    }

    submit(): void {
        if (this.form.invalid) return;
        this.loading = true;
        this.error = '';
        const { username, password } = this.form.value;
        this.authService.login(username, password).subscribe({
            next: () => this.router.navigate(['/dashboard']),
            error: (err) => {
                this.error = err.error?.message || 'Invalid credentials';
                this.loading = false;
            }
        });
    }
}
