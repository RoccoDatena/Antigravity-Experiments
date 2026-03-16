import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserInfo {
    id: number;
    username: string;
    email: string;
    role: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
    private baseUrl = 'http://localhost:8080/api/users';

    constructor(private http: HttpClient) { }

    getAllUsers(): Observable<UserInfo[]> {
        return this.http.get<UserInfo[]>(this.baseUrl);
    }
}
