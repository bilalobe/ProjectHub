import { Component, OnInit } from '@angular/core';
import { TeamService } from '../../services/team/team.service';
import { Team } from '../../models/team.model';

@Component({
  selector: 'app-team-management',
  templateUrl: './team-management.component.html',
  styleUrls: ['./team-management.component.css']
})
export class TeamManagementComponent implements OnInit {
  teams: Team[] = [];

  constructor(private teamService: TeamService) { }

  ngOnInit(): void {
    this.loadTeams();
  }

  loadTeams(): void {
    this.teamService.getTeams().subscribe(teams => {
      this.teams = teams;
    });
  }
}