import { Component, OnInit } from '@angular/core';
import { ProjectStatusDTO } from '../../models/project-status.dto';
import { RecentActivityDTO } from '../../models/recent-activity.dto';
import { DashboardService } from '../../services/dashboard/dashboard.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  totalUsers: number = 0;
  totalProjects: number = 0;
  totalTeams: number = 0;
  projectStatusDistribution: ProjectStatusDTO[] = [];
  recentActivities: RecentActivityDTO[] = [];

  constructor(private dashboardService: DashboardService) { }

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.dashboardService.getTotalUsers().subscribe(totalUsers => this.totalUsers = totalUsers);
    this.dashboardService.getTotalProjects().subscribe(totalProjects => this.totalProjects = totalProjects);
    this.dashboardService.getTotalTeams().subscribe(totalTeams => this.totalTeams = totalTeams);
    this.dashboardService.getProjectStatusDistribution().subscribe(data => this.projectStatusDistribution = data);
    this.dashboardService.getRecentActivities().subscribe(data => this.recentActivities = data);
  }
}