import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserManagementComponent } from './components/user-management/user-management.component';
import { TeamManagementComponent } from './components/team-management/team-management.component';
import { ProjectManagementComponent } from './components/project-management/project-management.component';
import { SubmissionManagementComponent } from './components/submission-management/submission-management.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';

export const routes: Routes = [
  { path: 'users', component: UserManagementComponent },
  { path: 'teams', component: TeamManagementComponent },
  { path: 'projects', component: ProjectManagementComponent },
  { path: 'submissions', component: SubmissionManagementComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }