import { Component, OnInit } from '@angular/core';
import { UserService, UserInfo } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-users',
    templateUrl: './users.component.html',
    styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
    users: UserInfo[] = [];
    loading = true;
    error = '';

    constructor(
        public authService: AuthService,
        private userService: UserService,
        private router: Router
    ) { }

    ngOnInit(): void {
        if (!this.authService.isAdmin()) {
            this.router.navigate(['/dashboard']);
            return;
        }
        this.userService.getAllUsers().subscribe({
            next: (users) => { this.users = users; this.loading = false; },
            error: () => { this.error = 'Failed to load users'; this.loading = false; }
        });
    }
}
