export interface Submission {
  id: string;
  projectId: string;
  userId: string;
  content: string;
  grade?: number; // Optional field
  timestamp: Date;
}