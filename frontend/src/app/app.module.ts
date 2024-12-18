import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { UserManagementComponent } from './components/user-management/user-management.component';
import { TeamManagementComponent } from './components/team-management/team-management.component';
import { ProjectManagementComponent } from './components/project-management/project-management.component';
import { SubmissionManagementComponent } from './components/submission-management/submission-management.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';

@NgModule({
  declarations: [
    AppComponent,
    UserManagementComponent,
    TeamManagementComponent,
    ProjectManagementComponent,
    SubmissionManagementComponent,
    DashboardComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    CommonModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }