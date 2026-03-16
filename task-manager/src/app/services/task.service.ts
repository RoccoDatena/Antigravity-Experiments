import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Task {
    id: number;
    title: string;
    description: string;
    status: 'TODO' | 'IN_PROGRESS' | 'DONE';
    priority: 'LOW' | 'MEDIUM' | 'HIGH';
    dueDate: string;
    createdAt: string;
    ownerId: number;
    ownerUsername: string;
    assigneeId: number | null;
    assigneeUsername: string | null;
}

export interface TaskRequest {
    title: string;
    description?: string;
    status?: string;
    priority?: string;
    dueDate?: string;
    assigneeId?: number | null;
}

export interface TaskStats {
    total: number;
    todo: number;
    inProgress: number;
    done: number;
}

@Injectable({ providedIn: 'root' })
export class TaskService {
    private baseUrl = 'http://localhost:8080/api/tasks';

    constructor(private http: HttpClient) { }

    getTasks(filters?: { status?: string; priority?: string; search?: string }): Observable<Task[]> {
        let params = new HttpParams();
        if (filters?.status) params = params.set('status', filters.status);
        if (filters?.priority) params = params.set('priority', filters.priority);
        if (filters?.search) params = params.set('search', filters.search);
        return this.http.get<Task[]>(this.baseUrl, { params });
    }

    createTask(task: TaskRequest): Observable<Task> {
        return this.http.post<Task>(this.baseUrl, task);
    }

    updateTask(id: number, task: TaskRequest): Observable<Task> {
        return this.http.put<Task>(`${this.baseUrl}/${id}`, task);
    }

    deleteTask(id: number): Observable<any> {
        return this.http.delete(`${this.baseUrl}/${id}`);
    }

    getStats(): Observable<TaskStats> {
        return this.http.get<TaskStats>(`${this.baseUrl}/stats`);
    }
}
