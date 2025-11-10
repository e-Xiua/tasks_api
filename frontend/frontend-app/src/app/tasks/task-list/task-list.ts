import { Component, effect, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../task.service';

/**
 * Task interface (keeps field names compatible with the backend service used by TaskService).
 * UI labels are shown in Spanish while underlying values remain compatible with the API.
 */
export interface Task {
  id?: number | string;
  name: string; // Nombre de la tarea (UI shows "Nombre")
  responsible?: string; // Responsable
  dueDate?: string; // Fecha de entrega (ISO)
  status?: string; // status text as expected by backend (we show Spanish labels in the template)
  category?: string; // Categoría / Curso
  priority?: string; // Low/Medium/High/Urgent - mapped to UI labels
  icon?: string; // emoji/icon
}

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-list.html',
  styleUrls: ['./task-list.css'],
})
export class TaskListComponent {
  constructor(private taskService: TaskService) {}

  // Signals for reactive state
  tasks = signal<Task[]>([]);
  visibleTasks = signal<Task[]>([]);
  query = signal('');
  filterStatus = signal<string>('All');
  filterPriority = signal<string>('All');
  filterResponsible = signal<string>('All');
  sortBy = signal<'dueDate' | 'priority' | ''>('');
  sortDir = signal<'asc' | 'desc'>('asc');

  categories = signal<string[]>([]);
  responsibles = signal<string[]>([]);

  // form/edit model (keeps backend-friendly keys)
  editing = signal<Task | null>(null);
  form = signal<Task>({ name: '', responsible: '', dueDate: '', status: 'To Do', category: '', priority: 'Medium', icon: '📝' });

  // UI options (label + underlying value) — we show Spanish labels but keep values API-friendly
  statusOptions = [
    { label: 'Sin empezar', value: 'To Do' },
    { label: 'En progreso', value: 'In Progress' },
    { label: 'Completada', value: 'Done' },
  ];

  priorityOptions = [
    { label: 'Baja', value: 'Low' },
    { label: 'Media', value: 'Medium' },
    { label: 'Alta', value: 'High' },
    { label: 'Urgente', value: 'Urgent' },
  ];

  priorityColor: Record<string, string> = {
    Low: '#60a5fa',
    Medium: '#60a5fa',
    High: '#f59e0b',
    Urgent: '#ef4444',
  };

  // Keep visibleTasks in sync
  private updateVisible = effect(() => {
    const q = this.query().toLowerCase().trim();
    let list = this.tasks().slice();

    const status = this.filterStatus();
    if (status && status !== 'All') {
      list = list.filter((t) => (t.status ?? '') === status);
    }
    const pr = this.filterPriority();
    if (pr && pr !== 'All') {
      list = list.filter((t) => (t.priority ?? '') === pr);
    }
    const resp = this.filterResponsible();
    if (resp && resp !== 'All') {
      list = list.filter((t) => (t.responsible ?? '') === resp);
    }

    if (q) list = list.filter((t) => (t.name ?? '').toLowerCase().includes(q));

    const sortBy = this.sortBy();
    const dir = this.sortDir() === 'asc' ? 1 : -1;
    if (sortBy === 'dueDate') {
      list.sort((a, b) => ((a.dueDate ? new Date(a.dueDate).getTime() : 0) - (b.dueDate ? new Date(b.dueDate).getTime() : 0)) * dir);
    } else if (sortBy === 'priority') {
      const order: any = { Low: 1, Medium: 2, High: 3, Urgent: 4 };
      list.sort((a, b) => (order[a.priority ?? 'Medium'] - order[b.priority ?? 'Medium']) * dir);
    }

    this.visibleTasks.set(list);
  });

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.taskService.getTasks().subscribe({
      next: (data) => {
        this.tasks.set(data || []);
        const cats = Array.from(new Set((data || []).map((t) => t.category).filter(Boolean) as string[]));
        this.categories.set(cats);
        const resp = Array.from(new Set((data || []).map((t) => t.responsible).filter(Boolean) as string[]));
        this.responsibles.set(resp);
      },
      error: (err) => console.error('No fue posible cargar tareas', err),
    });
  }

  saveTask(): void {
    const payload = { ...this.form() } as Task;
    if (this.editing()) {
      const id = this.editing()!.id!;
      this.taskService.updateTask(id, payload).subscribe({ next: () => { this.loadTasks(); this.cancelEdit(); }, error: (e) => console.error('Failed to update', e) });
    } else {
      this.taskService.createTask(payload).subscribe({ next: () => { this.loadTasks(); this.resetForm(); }, error: (e) => console.error('Failed to create', e) });
    }
  }

  editTask(t: Task): void {
    this.editing.set(t);
    this.form.set({ ...t });
  }

  cancelEdit(): void {
    this.editing.set(null);
    this.resetForm();
  }

  resetForm(): void {
    this.form.set({ name: '', responsible: '', dueDate: '', status: 'To Do', category: '', priority: 'Medium', icon: '📝' });
  }

  deleteTask(t: Task): void {
    if (!t.id) return;
    if (!confirm(`Eliminar tarea "${t.name}"?`)) return;
    this.taskService.deleteTask(t.id!).subscribe({ next: () => this.loadTasks(), error: (e) => console.error('Failed to delete', e) });
  }

  addCategory(name: string): void {
    const n = (name || '').trim();
    if (!n) return;
    if (!this.categories().includes(n)) this.categories.update((c) => [n, ...c]);
  }

  updateFormField<K extends keyof Task>(field: K, value: any) {
    const cur = this.form();
    this.form.set({ ...cur, [field]: value } as Task);
  }

  statusClass(status?: string) {
    if (!status) return '';
    return String(status).toLowerCase().replace(/[^a-z0-9]+/g, '-');
  }

  setSort(by: 'dueDate' | 'priority' | '') {
    if (this.sortBy() === by) this.sortDir.set(this.sortDir() === 'asc' ? 'desc' : 'asc');
    else { this.sortBy.set(by); this.sortDir.set('asc'); }
  }

  getPriorityColor(p: string) {
    return this.priorityColor[p] || '#94a3b8';
  }
}
