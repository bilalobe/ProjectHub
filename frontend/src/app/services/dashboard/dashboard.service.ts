import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjectStatusDTO } from '../../models/project-status.dto';
import { RecentActivityDTO } from '../../models/recent-activity.dto';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly apiUrl = 'http://localhost:8080/api/dashboard';

  constructor(private readonly http: HttpClient) { }

  getTotalUsers(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/total-users`);
  }

  getTotalProjects(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/total-projects`);
  }

  getTotalTeams(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/total-teams`);
  }

  getProjectStatusDistribution(): Observable<ProjectStatusDTO[]> {
    return this.http.get<ProjectStatusDTO[]>(`${this.apiUrl}/project-status-distribution`);
  }

  getRecentActivities(): Observable<RecentActivityDTO[]> {
    return this.http.get<RecentActivityDTO[]>(`${this.apiUrl}/recent-activities`);
  }
}