import { Routes } from '@angular/router';

// Add a route for the standalone TaskList component. We lazy-load the component
// so the module graph stays small and to avoid direct static imports in this file.
export const routes: Routes = [
	{
		path: 'tasks',
		loadComponent: () =>
			import('./tasks/task-list/task-list').then((m: any) => m.TaskListComponent ?? m.default ?? m.TaskList ?? m.TaskListComponent),
	},
	{ path: '', redirectTo: 'tasks', pathMatch: 'full' },
];
