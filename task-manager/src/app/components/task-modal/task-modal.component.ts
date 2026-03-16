import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Task, TaskService } from '../../services/task.service';
import { UserService, UserInfo } from '../../services/user.service';

@Component({
    selector: 'app-task-modal',
    templateUrl: './task-modal.component.html',
    styleUrls: ['./task-modal.component.css']
})
export class TaskModalComponent implements OnInit {
    @Input() task: Task | null = null;
    @Output() saved = new EventEmitter<void>();
    @Output() cancelled = new EventEmitter<void>();

    form: FormGroup;
    users: UserInfo[] = [];
    loading = false;
    error = '';

    constructor(
        private fb: FormBuilder,
        private taskService: TaskService,
        private userService: UserService
    ) {
        this.form = this.fb.group({
            title: ['', Validators.required],
            description: [''],
            status: ['TODO'],
            priority: ['MEDIUM'],
            dueDate: [''],
            assigneeId: [null]
        });
    }

    ngOnInit(): void {
        if (this.task) {
            this.form.patchValue({
                title: this.task.title,
                description: this.task.description || '',
                status: this.task.status,
                priority: this.task.priority,
                dueDate: this.task.dueDate || '',
                assigneeId: this.task.assigneeId
            });
        }
        // Load users for assignee dropdown
        this.userService.getAllUsers().subscribe({
            next: (users) => this.users = users,
            error: () => { } // Non-admin won't have access, that's fine
        });
    }

    submit(): void {
        if (this.form.invalid) return;
        this.loading = true;
        this.error = '';
        const data = this.form.value;
        const payload = {
            title: data.title,
            description: data.description || undefined,
            status: data.status,
            priority: data.priority,
            dueDate: data.dueDate || undefined,
            assigneeId: data.assigneeId ? +data.assigneeId : null
        };

        const op = this.task
            ? this.taskService.updateTask(this.task.id, payload)
            : this.taskService.createTask(payload);

        op.subscribe({
            next: () => this.saved.emit(),
            error: (err) => {
                this.error = err.error?.message || 'Error saving task';
                this.loading = false;
            }
        });
    }

    close(): void {
        this.cancelled.emit();
    }
}
