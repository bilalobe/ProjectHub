sequenceDiagram
    participant User
    participant UI
    participant ComponentDetailsController
    participant ComponentViewModel
    participant ComponentService
    participant ComponentRepository

    User->>UI: Create/Update/Delete Component
    UI->>ComponentDetailsController: Handle User Input
    ComponentDetailsController->>ComponentViewModel: Validate and Prepare Data
    ComponentViewModel->>ComponentService: Perform CRUD Operation
    ComponentService->>ComponentRepository: Save/Update/Delete Component
    ComponentRepository->>ComponentService: Return Result
    ComponentService->>ComponentViewModel: Return Result
    ComponentViewModel->>ComponentDetailsController: Update View
    ComponentDetailsController->>UI: Display Result

sequenceDiagram
    participant User
    participant UI
    participant TreeViewController
    participant TreeItemWrapper
    participant LoaderFactory
    participant TreeItemLoader

    User->>UI: Expand Tree Item
    UI->>TreeViewController: Handle Expand Event
    TreeViewController->>TreeItemWrapper: Get Data
    TreeViewController->>LoaderFactory: Get Loader
    LoaderFactory->>TreeItemLoader: Return Loader
    TreeItemLoader->>TreeViewController: Load Children
    TreeViewController->>UI: Update Tree View

classDiagram
    class ProjectService {
        +createProject(ProjectDTO)
        +updateProject(UUID, ProjectDTO)
        +deleteProject(UUID)
        +getProjectById(UUID)
        +getAllProjects()
    }

    class ProjectDetailsController {
        +displayProjectDetails(ProjectDTO)
        +loadComponents(UUID)
        +saveProject(ActionEvent)
        +deleteProject(ActionEvent)
    }

    class ProjectHubViewModel {
        +loadProjects()
        +searchProjects()
        +saveProject(ProjectDTO)
        +deleteProject(UUID)
        +getProjects()
        +getComponents()
    }

    ProjectService --> ProjectRepository
    ProjectDetailsController --> ProjectHubViewModel
    ProjectHubViewModel --> ProjectService
    ProjectHubViewModel --> ComponentService

stateDiagram-v2
    [*] --> Idle
    Idle --> Creating : Create Component
    Creating --> Validating : Validate Data
    Validating --> Saving : Save Component
    Saving --> Idle : Success
    Saving --> Error : Failure
    Error --> Idle : Retry
    Idle --> Updating : Update Component
    Updating --> Validating : Validate Data
    Validating --> Saving : Save Component
    Idle --> Deleting : Delete Component
    Deleting --> Idle : Success
    Deleting --> Error : Failure

erDiagram
    USER {
        UUID id
        String username
        String password
        String email
    }
    PROJECT {
        UUID id
        String name
        String description
        UUID teamId
    }
    COMPONENT {
        UUID id
        String name
        String description
        UUID projectId
    }
    TEAM {
        UUID id
        String name
        UUID cohortId
    }
    COHORT {
        UUID id
        String name
        UUID schoolId
    }
    SCHOOL {
        UUID id
        String name
    }

    USER ||--o{ PROJECT : "manages"
    PROJECT ||--o{ COMPONENT : "contains"
    TEAM ||--o{ PROJECT : "works on"
    COHORT ||--o{ TEAM : "has"
    SCHOOL ||--o{ COHORT : "includes"

requirementDiagram
    requirement user_management {
        id: 1
        text: "The system shall allow users to manage user accounts."
    }
    requirement project_management {
        id: 2
        text: "The system shall allow users to manage projects."
    }
    requirement component_management {
        id: 3
        text: "The system shall allow users to manage components."
    }
    requirement team_management {
        id: 4
        text: "The system shall allow users to manage teams."
    }
    requirement cohort_management {
        id: 5
        text: "The system shall allow users to manage cohorts."
    }
    requirement school_management {
        id: 6
        text: "The system shall allow users to manage schools."
    }

    user_management --> project_management
    project_management --> component_management
    team_management --> project_management
    cohort_management --> team_management
    school_management --> cohort_management

graph TD
    A[User Interface] -->|Interacts with| B[Controllers]
    B -->|Updates| C[ViewModels]
    C -->|Calls| D[Services]
    D -->|Accesses| E[Repositories]
    E -->|Persists| F[Database]
    F -->|Returns Data to| E
    E -->|Returns Data to| D
    D -->|Returns Data to| C
    C -->|Updates| B
    B -->|Updates| A

sequenceDiagram
    participant User
    participant Controller
    participant Service
    participant JPARepository
    participant CSVRepository

    User->>Controller: Create/Update/Delete Student
    Controller->>Service: Handle Request
    Service->>JPARepository: Save/Delete Student
    Service->>CSVRepository: Save/Delete Student
    JPARepository->>Service: Return Result
    CSVRepository->>Service: Return Result
    Service->>Controller: Return Response
    Controller->>User: Display Result

graph TD
    subgraph User Interface
        A[User] -->|Interacts with| B[Dashboard]
        A -->|Interacts with| C[Project Management]
        A -->|Interacts with| D[Component Management]
        A -->|Interacts with| E[Team Management]
        A -->|Interacts with| F[Cohort Management]
        A -->|Interacts with| G[School Management]
    end

    subgraph Controllers
        B -->|Updates| H[DashboardController]
        C -->|Updates| I[ProjectDetailsController]
        D -->|Updates| J[ComponentDetailsController]
        E -->|Updates| K[TeamDetailsController]
        F -->|Updates| L[CohortDetailsController]
        G -->|Updates| M[SchoolDetailsController]
    end

    subgraph ViewModels
        H -->|Calls| N[DashboardViewModel]
        I -->|Calls| O[ProjectDetailsViewModel]
        J -->|Calls| P[ComponentDetailsViewModel]
        K -->|Calls| Q[TeamDetailsViewModel]
        L -->|Calls| R[CohortDetailsViewModel]
        M -->|Calls| S[SchoolDetailsViewModel]
    end

    subgraph Services
        N -->|Calls| T[DashboardService]
        O -->|Calls| U[ProjectService]
        P -->|Calls| V[ComponentService]
        Q -->|Calls| W[TeamService]
        R -->|Calls| X[CohortService]
        S -->|Calls| Y[SchoolService]
    end

    subgraph Repositories
        T -->|Accesses| Z[DashboardRepository]
        U -->|Accesses| AA[ProjectRepository]
        V -->|Accesses| AB[ComponentRepository]
        W -->|Accesses| AC[TeamRepository]
        X -->|Accesses| AD[CohortRepository]
        Y -->|Accesses| AE[SchoolRepository]
    end

    subgraph Database
        Z -->|Persists| AF[DashboardTable]
        AA -->|Persists| AG[ProjectTable]
        AB -->|Persists| AH[ComponentTable]
        AC -->|Persists| AI[TeamTable]
        AD -->|Persists| AJ[CohortTable]
        AE -->|Persists| AK[SchoolTable]
    end

    AF -->|Returns Data to| Z
    AG -->|Returns Data to| AA
    AH -->|Returns Data to| AB
    AI -->|Returns Data to| AC
    AJ -->|Returns Data to| AD
    AK -->|Returns Data to| AE

    Z -->|Returns Data to| T
    AA -->|Returns Data to| U
    AB -->|Returns Data to| V
    AC -->|Returns Data to| W
    AD -->|Returns Data to| X
    AE -->|Returns Data to| Y

    T -->|Returns Data to| N
    U -->|Returns Data to| O
    V -->|Returns Data to| P
    W -->|Returns Data to| Q
    X -->|Returns Data to| R
    Y -->|Returns Data to| S

    N -->|Updates| H
    O -->|Updates| I
    P -->|Updates| J
    Q -->|Updates| K
    R -->|Updates| L
    S -->|Updates| M

    H -->|Updates| B
    I -->|Updates| C
    J -->|Updates| D
    K -->|Updates| E
    L -->|Updates| F
    M -->|Updates| G

sequenceDiagram
    participant User
    participant AngularApp
    participant AuthService
    participant BackendAPI
    participant AuthServer
    participant Database

    User->>+AngularApp: Enters Credentials
    AngularApp->>+AuthService: Authenticate User
    AuthService->>+BackendAPI: POST /login
    BackendAPI->>+AuthServer: Authenticate Credentials
    AuthServer->>+Database: Verify User Credentials
    Database-->>-AuthServer: User Details
    AuthServer-->>-BackendAPI: JWT Token
    BackendAPI-->>-AuthService: JWT Token
    AuthService-->>-AngularApp: Authentication Success
    AngularApp-->>-User: Access Granted

sequenceDiagram
    participant User
    participant AngularApp
    participant ProjectService (Frontend)
    participant BackendAPI
    participant ProjectController
    participant ProjectService (Backend)
    participant JPARepository
    participant Database

    User->>+AngularApp: Create New Project
    AngularApp->>+ProjectService (Frontend): Submit Project Data
    ProjectService (Frontend)->>+BackendAPI: POST /projects
    BackendAPI->>+ProjectController: Handle Request
    ProjectController->>+ProjectService (Backend): Save Project
    ProjectService (Backend)->>+JPARepository: Save Project Entity
    JPARepository->>+Database: Insert Record
    Database-->>-JPARepository: Confirmation
    JPARepository-->>-ProjectService (Backend): Entity Saved
    ProjectService (Backend)-->>-ProjectController: Success
    ProjectController-->>-BackendAPI: HTTP 201 Created
    BackendAPI-->>-ProjectService (Frontend): Response
    ProjectService (Frontend)-->>-AngularApp: Project Created
    AngularApp-->>-User: Project Successfully Created

graph TD
    %% User Interfaces
    subgraph User Interfaces
        A[User]
        A -->|Interacts with| B[Angular Web App]
        A -->|Interacts with| C[JavaFX Desktop App]
    end

    %% Angular Frontend Components
    subgraph Angular Frontend
        B -->|Uses| D[Components]
        B -->|Uses| E[Services]
        D -->|Displays| F[Views]
        E -->|Calls| G[REST API]
    end

    %% JavaFX Desktop Frontend Components
    subgraph JavaFX Desktop Frontend
        C -->|Uses| H[Controllers]
        C -->|Uses| I[ViewModels]
        H -->|Updates| J[Views (FXML)]
        I -->|Calls| G[REST API]
    end

    %% Backend REST API
    subgraph Backend REST API
        G -->|Routes Requests| K[Spring Boot Application]
    end

    %% Controllers and Services
    subgraph Backend Controllers & Services
        K -->|Handles Requests| L[REST Controllers]
        L -->|Calls| M[Services]
    end

    %% Business Logic and Repositories
    subgraph Business Logic
        M -->|Implements| N[Business Logic]
        N -->|Uses| O[Repositories]
    end

    %% Data Persistence
    subgraph Data Persistence Layer
        O -->|CRUD Operations| P[JPA Repositories]
        O -->|Optionally Uses| Q[CSV Repositories]
        P -->|Interacts with| R[Database]
        Q -->|Interacts with| S[CSV Files]
    end

    %% Database
    subgraph Database
        R[PostgreSQL Database]
    end

    %% Additional Interactions
    %% Real-time Updates and Security
    subgraph Real-time & Security
        B -->|Receives Updates from| T[WebSocket Server]
        K -->|Broadcasts Updates via| T
        B -->|Authenticates via| U[Auth Server (Spring Security)]
        C -->|Authenticates via| U
        U -->|Manages| V[User Sessions]
    end

    %% External Services
    subgraph External Services
        M -->|Integrates with| W[External APIs]
    end

    %% Relations and Data Flow
    %% Frontend to Backend
    style B fill:#D5E8D4,stroke:#82B366
    style C fill:#FFF2CC,stroke:#D6B656
    style K fill:#E1D5E7,stroke:#9673A6
    style R fill:#F8CECC,stroke:#B85450
    style U fill:#DAE8FC,stroke:#6C8EBF

    %% Annotations
    classDef frontend fill:#D5E8D4,stroke:#82B366
    classDef desktop fill:#FFF2CC,stroke:#D6B656
    classDef backend fill:#E1D5E7,stroke:#9673A6
    classDef database fill:#F8CECC,stroke:#B85450
    classDef auth fill:#DAE8FC,stroke:#6C8EBF

    %% Applying Classes
    class B,E,F,D frontend
    class C,H,I,J desktop
    class K,L,M,N,O,P,Q backend
    class R database
    class U,V auth