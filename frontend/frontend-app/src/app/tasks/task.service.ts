import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * TaskService - centralises all HTTP calls to the Spring Boot backend.
 *
 * - Provided in root so any component can inject it.
 * - Uses RxJS Observables to represent asynchronous HTTP operations.
 * - Keeps the component logic thin: components subscribe to the Observables
 *   and update their local state (signals, stores, etc.).
 *
 * Backend endpoints expected:
 *  GET    /api/tasks
 *  POST   /api/tasks
 *  PUT    /api/tasks/{id}
 *  DELETE /api/tasks/{id}
 */
@Injectable({ providedIn: 'root' })
export class TaskService {
  private base = '/api/tasks';

  constructor(private http: HttpClient) {}

  // Get the list of tasks from backend. Returns an Observable that emits the array once.
  getTasks(): Observable<any[]> {
    return this.http.get<any[]>(this.base);
  }

  // Create a task on the backend. Returns the created task from the server.
  createTask(payload: any): Observable<any> {
    return this.http.post<any>(this.base, payload);
  }

  // Update an existing task by id. Returns the updated task.
  updateTask(id: string | number, payload: any): Observable<any> {
    return this.http.put<any>(`${this.base}/${id}`, payload);
  }

  // Delete a task by id. Returns an empty response (or server response).
  deleteTask(id: string | number): Observable<any> {
    return this.http.delete(`${this.base}/${id}`);
  }
}
