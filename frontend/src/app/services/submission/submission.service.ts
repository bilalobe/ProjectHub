import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Submission } from '../../models/submission.model';

@Injectable({
  providedIn: 'root'
})
export class SubmissionService {
  private apiUrl = 'http://localhost:8080/api/submissions';

  constructor(private http: HttpClient) { }

  getSubmissions(): Observable<Submission[]> {
    return this.http.get<Submission[]>(this.apiUrl);
  }

  getSubmission(id: string): Observable<Submission> {
    return this.http.get<Submission>(`${this.apiUrl}/${id}`);
  }

  createSubmission(submission: Submission): Observable<Submission> {
    return this.http.post<Submission>(this.apiUrl, submission);
  }

  updateSubmission(id: string, submission: Submission): Observable<Submission> {
    return this.http.put<Submission>(`${this.apiUrl}/${id}`, submission);
  }

  deleteSubmission(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}