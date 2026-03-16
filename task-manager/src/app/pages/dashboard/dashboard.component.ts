import { Component, OnInit } from '@angular/core';
import { TaskService, TaskStats } from '../../services/task.service';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
    stats: TaskStats = { total: 0, todo: 0, inProgress: 0, done: 0 };
    loading = true;

    constructor(
        public authService: AuthService,
        private taskService: TaskService
    ) { }

    ngOnInit(): void {
        this.taskService.getStats().subscribe({
            next: (s) => { this.stats = s; this.loading = false; },
            error: () => { this.loading = false; }
        });
    }

    get todoPercent(): number {
        return this.stats.total ? Math.round((this.stats.todo / this.stats.total) * 100) : 0;
    }
    get inProgressPercent(): number {
        return this.stats.total ? Math.round((this.stats.inProgress / this.stats.total) * 100) : 0;
    }
    get donePercent(): number {
        return this.stats.total ? Math.round((this.stats.done / this.stats.total) * 100) : 0;
    }
}
