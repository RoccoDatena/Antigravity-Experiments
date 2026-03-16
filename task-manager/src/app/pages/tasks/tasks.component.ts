import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TaskService, Task } from '../../services/task.service';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-tasks',
    templateUrl: './tasks.component.html',
    styleUrls: ['./tasks.component.css']
})
export class TasksComponent implements OnInit {
    tasks: Task[] = [];
    loading = true;
    error = '';

    // Filters
    filterStatus = '';
    filterPriority = '';
    filterSearch = '';

    // Modal
    showModal = false;
    editingTask: Task | null = null;

    constructor(
        public authService: AuthService,
        private taskService: TaskService,
        private route: ActivatedRoute
    ) { }

    ngOnInit(): void {
        // Apply query params as initial filter (e.g. from dashboard quick actions)
        this.route.queryParams.subscribe(params => {
            if (params['status']) this.filterStatus = params['status'];
            if (params['priority']) this.filterPriority = params['priority'];
            this.loadTasks();
        });
    }

    loadTasks(): void {
        this.loading = true;
        this.taskService.getTasks({
            status: this.filterStatus || undefined,
            priority: this.filterPriority || undefined,
            search: this.filterSearch || undefined
        }).subscribe({
            next: (tasks) => { this.tasks = tasks; this.loading = false; },
            error: () => { this.error = 'Failed to load tasks'; this.loading = false; }
        });
    }

    openCreate(): void {
        this.editingTask = null;
        this.showModal = true;
    }

    openEdit(task: Task): void {
        this.editingTask = task;
        this.showModal = true;
    }

    onModalSave(): void {
        this.showModal = false;
        this.loadTasks();
    }

    deleteTask(id: number): void {
        if (!confirm('Delete this task?')) return;
        this.taskService.deleteTask(id).subscribe({
            next: () => this.loadTasks(),
            error: () => alert('Error deleting task')
        });
    }

    quickStatus(task: Task, status: string): void {
        this.taskService.updateTask(task.id, { title: task.title, status }).subscribe({
            next: () => this.loadTasks()
        });
    }

    clearFilters(): void {
        this.filterStatus = '';
        this.filterPriority = '';
        this.filterSearch = '';
        this.loadTasks();
    }

    formatDate(d?: string): string {
        if (!d) return '—';
        return new Date(d).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
    }

    isOverdue(task: Task): boolean {
        return task.status !== 'DONE' && !!task.dueDate && new Date(task.dueDate) < new Date();
    }
}
