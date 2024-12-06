import { Component, OnInit } from '@angular/core';
import { SubmissionService } from '../../services/submission/submission.service';
import { Submission } from '../../models/submission.model';

@Component({
  selector: 'app-submission-management',
  templateUrl: './submission-management.component.html',
  styleUrls: ['./submission-management.component.css']
})
export class SubmissionManagementComponent implements OnInit {
  submissions: Submission[] = [];

  constructor(private submissionService: SubmissionService) { }

  ngOnInit(): void {
    this.loadSubmissions();
  }

  loadSubmissions(): void {
    this.submissionService.getSubmissions().subscribe(submissions => {
      this.submissions = submissions;
    });
  }
}