%% 
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

%% Tree View Expansion Sequence
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

%% Project Service Class Diagram
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

%% Component State Diagram
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

%% Entity Relationship Diagram
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

%% Requirements Diagram
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

%% System Architecture Diagram
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

%% Student Management Sequence
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

%% User Interface Diagram
graph TD
    subgraph User Interface
        A[User] -->|Interacts with| B[Dashboard]
        A -->|Interacts with| C[Project Management]
        A -->|Interacts with| D[Component Management]
        A -->|Interacts with| E[Team Management]
        A -->|Interacts with| F[Cohort Management]
        A -->|Interacts with| G[School Management]
        A -->|Interacts with| H[Login]
        A -->|Interacts with| I[Register]
    end

    subgraph Controllers
        B -->|Updates| J[DashboardController]
        C -->|Updates| K[ProjectDetailsController]
        D -->|Updates| L[ComponentDetailsController]
        E -->|Updates| M[TeamDetailsController]
        F -->|Updates| N[CohortDetailsController]
        G -->|Updates| O[SchoolDetailsController]
        H -->|Updates| P[AuthController]
        I -->|Updates| P[AuthController]
    end

    subgraph ViewModels
        J -->|Calls| Q[DashboardViewModel]
        K -->|Calls| R[ProjectDetailsViewModel]
        L -->|Calls| S[ComponentDetailsViewModel]
        M -->|Calls| T[TeamDetailsViewModel]
        N -->|Calls| U[CohortDetailsViewModel]
        O -->|Calls| V[SchoolDetailsViewModel]
        P -->|Calls| W[AuthViewModel]
    end

    subgraph Services
        Q -->|Calls| X[DashboardService]
        R -->|Calls| Y[ProjectService]
        S -->|Calls| Z[ComponentService]
        T -->|Calls| AA[TeamService]
        U -->|Calls| AB[CohortService]
        V -->|Calls| AC[SchoolService]
        W -->|Calls| AD[AuthService]
    end

    subgraph Repositories
        X -->|Accesses| AE[DashboardRepository]
        Y -->|Accesses| AF[ProjectRepository]
        Z -->|Accesses| AG[ComponentRepository]
        AA -->|Accesses| AH[TeamRepository]
        AB -->|Accesses| AI[CohortRepository]
        AC -->|Accesses| AJ[SchoolRepository]
        AD -->|Accesses| AK[AuthRepository]
    end

    subgraph Database
        AE -->|Persists| AL[DashboardTable]
        AF -->|Persists| AM[ProjectTable]
        AG -->|Persists| AN[ComponentTable]
        AH -->|Persists| AO[TeamTable]
        AI -->|Persists| AP[CohortTable]
        AJ -->|Persists| AQ[SchoolTable]
        AK -->|Persists| AR[UserTable]
    end

    AL -->|Returns Data to| AE
    AM -->|Returns Data to| AF
    AN -->|Returns Data to| AG
    AO -->|Returns Data to| AH
    AP -->|Returns Data to| AI
    AQ -->|Returns Data to| AJ
    AR -->|Returns Data to| AK

    AE -->|Returns Data to| X
    AF -->|Returns Data to| Y
    AG -->|Returns Data to| Z
    AH -->|Returns Data to| AA
    AI -->|Returns Data to| AB
    AJ -->|Returns Data to| AC
    AK -->|Returns Data to| AD

    X -->|Returns Data to| Q
    Y -->|Returns Data to| R
    Z -->|Returns Data to| S
    AA -->|Returns Data to| T
    AB -->|Returns Data to| U
    AC -->|Returns Data to| V
    AD -->|Returns Data to| W

    Q -->|Updates| J
    R -->|Updates| K
    S -->|Updates| L
    T -->|Updates| M
    U -->|Updates| N
    V -->|Updates| O
    W -->|Updates| P

    J -->|Updates| B
    K -->|Updates| C
    L -->|Updates| D
    M -->|Updates| E
    N -->|Updates| F
    O -->|Updates| G
    P -->|Updates| H
    P -->|Updates| I

%% Simplified User Interface Diagram
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

%% Authentication Sequence
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

%% Project Creation Sequence
sequenceDiagram
    participant User
    participant AngularApp
    participant ProjectService
    participant BackendAPI
    participant ProjectController
    participant ProjectService
    participant JPARepository
    participant Database

    User->>+AngularApp: Create New Project
    AngularApp->>+ProjectService: Submit Project Data
    ProjectService->>+BackendAPI: POST /projects
    BackendAPI->>+ProjectController: Handle Request
    ProjectController->>+ProjectService: Save Project
    ProjectService->>+JPARepository: Save Project Entity
    JPARepository->>+Database: Insert Record
    Database-->>-JPARepository: Confirmation
    JPARepository-->>-ProjectService Entity Saved
    ProjectService-->>-ProjectController: Success
    ProjectController-->>-BackendAPI: HTTP 201 Created
    BackendAPI-->>-ProjectService: Response
    ProjectService-->>-AngularApp: Project Created
    AngularApp-->>-User: Project Successfully Created

%% User Interfaces Diagram
graph TD
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

%% CI/CD Pipeline Sequence
sequenceDiagram
    participant Developer
    participant GitHub
    participant GitHubActions
    participant DockerHub
    participant Kubernetes

    Developer->>GitHub: Push Code
    GitHub->>GitHubActions: Trigger CI Pipeline
    GitHubActions->>GitHubActions: Checkout Code
    GitHubActions->>GitHubActions: Build Application
    GitHubActions->>GitHubActions: Run Tests
    GitHubActions->>GitHubActions: Build Docker Image
    GitHubActions->>DockerHub: Push Docker Image
    GitHubActions->>Kubernetes: Deploy to Kubernetes
    Kubernetes->>Kubernetes: Update Deployment
    Kubernetes->>Developer: Deployment Status

%% CI/CD Pipeline Diagram
flowchart TB
    subgraph Developer
        A[Developer]
    end

    subgraph VersionControl
        B[GitHub Repository]
    end

    subgraph CICD
        C[GitHub Actions]
    end

    subgraph Registry
        D[Docker Hub]
    end

    subgraph KubernetesCluster
        E[Kubernetes]
        F[Ingress Controller]
        G[Backend Service]
        H[Frontend Service]
        I[Database Service]
        J[Authentication Service]
    end

    subgraph Monitoring
        K[Prometheus]
        L[Grafana]
    end

    subgraph Logging
        M[Elasticsearch]
        N[Logstash]
        O[Kibana]
    end

    A -->|Pushes Code| B
    B -->|Triggers CI/CD| C
    C -->|Builds & Tests| C
    C -->|Builds Docker Image| D
    D -->|Stores Image| D
    C -->|Deploys to| E
    F -->|Routes Traffic| G & H
    G -->|Communicates with| I & J
    E -->|Monitors| K
    K -->|Visualizes Metrics| L
    E -->|Logs to| M
    M -->|Processes Logs| N
    N -->|Visualizes Logs| O

%% Repository Profiles Diagram
graph TD

    A[Spring Boot Application] -->|Active Profile: csv| B[AppUserCsvRepository]
    A[Spring Boot Application] -->|Active Profile: h2| C[AppUserJpaRepository]
    A[Spring Boot Application] -->|Active Profile: postgres| C[AppUserJpaRepository]

    B[AppUserCsvRepository] --> D[AppUserCsvRepositoryImpl]
    C[AppUserJpaRepository] --> E[AppUserRepository]

    subgraph CSV Profile
        B[AppUserCsvRepository]
        D[AppUserCsvRepositoryImpl]
    end

    subgraph JPA Profile
        C[AppUserJpaRepository]
        E[AppUserRepository]
    end

    subgraph Database
        F[CSV File]
        G[H2 Database]
        H[PostgreSQL Database]
    end

    D[AppUserCsvRepositoryImpl] --> F[CSV File]
    E[AppUserRepository] --> G[H2 Database]
    E[AppUserRepository] --> H[PostgreSQL Database]

%% Component Interaction Diagram
flowchart TD
    %% User Interface
    subgraph User Interface
        A[User]
        A -->|Interacts with| B[UI]
    end

    %% Controllers
    subgraph Controllers
        B -->|Handles Input| C[ComponentDetailsController]
        B -->|Handles Input| D[TreeViewController]
    end

    %% View Models
    subgraph View Models
        C -->|Validates and Prepares Data| E[ComponentViewModel]
        D -->|Gets Data| F[TreeItemWrapper]
    end

    %% Services
    subgraph Services
        E -->|Performs CRUD Operation| G[ComponentService]
        F -->|Gets Loader| H[LoaderFactory]
    end

    %% Repositories
    subgraph Repositories
        G -->|Saves/Updates/Deletes Component| I[ComponentRepository]
        H -->|Returns Loader| J[TreeItemLoader]
    end

    %% Database
    subgraph Database
        I -->|Persists Data| K[Database]
    end

    %% Data Flow
    K -->|Returns Data to| I
    I -->|Returns Result to| G
    G -->|Returns Result to| E
    E -->|Updates View| C
    C -->|Displays Result| B
    J -->|Loads Children| D
    D -->|Updates Tree View| B

%% Registration and Login Sequence
sequenceDiagram
    participant User
    participant DesktopApp
    participant AuthController
    participant AuthService
    participant UserRepository
    participant Database

    Note over User,DesktopApp: Registration Flow

    User->>+DesktopApp: Fill Registration Form
    DesktopApp->>+AuthController: POST /register
    AuthController->>+AuthService: registerUser(UserDTO)
    AuthService->>+UserRepository: save(User)
    UserRepository->>+Database: Insert User Record
    Database-->>-UserRepository: Success
    UserRepository-->>-AuthService: User Saved
    AuthService-->>-AuthController: Registration Success
    AuthController-->>-DesktopApp: HTTP 201 Created
    DesktopApp-->>-User: Registration Successful
    alt Registration Failed
        AuthService-->>AuthController: Registration Failed
        AuthController-->>DesktopApp: HTTP 400 Bad Request
        DesktopApp-->>User: Registration Failed
    end

    Note over User,DesktopApp: Login Flow

    User->>+DesktopApp: Enter Credentials
    DesktopApp->>+AuthController: POST /login
    AuthController->>+AuthService: authenticate(username, password)
    AuthService->>+UserRepository: findByUsername(username)
    UserRepository-->>-AuthService: User Details
    AuthService->>AuthService: Verify Password
    alt Password Correct
        AuthService-->>AuthController: JWT Token
        AuthController-->>DesktopApp: JWT Token
        DesktopApp-->>User: Login Successful
        DesktopApp->>DesktopApp: Store JWT Token for Authenticated Requests
    else Password Incorrect
        AuthService-->>AuthController: Authentication Failed
        AuthController-->>DesktopApp: HTTP 401 Unauthorized
        DesktopApp-->>User: Login Failed
    end

%% JavaFX Desktop App Interaction Diagram
flowchart TD
    %% User Interface
    subgraph User Interface
        A[User]
        A -->|Interacts with| B[JavaFX Desktop App]
    end

    %% JavaFX Desktop App
    subgraph JavaFX Desktop App
        B -->|Uses| C[NavigationController]
        C -->|Loads| D[TreeView]
        C -->|Loads| E[BorderPane]
    end

    %% NavigationController
    subgraph NavigationController
        C -->|Handles Selection| F[TreeItem]
        F -->|Triggers| G[LoaderFactory]
        G -->|Returns| H[FXMLLoader]
        H -->|Loads| I[View]
    end

    %% ViewModels and Controllers
    subgraph ViewModels and Controllers
        I -->|Uses| J[ViewModel]
        J -->|Calls| K[Service]
        K -->|Accesses| L[Repository]
    end

    %% Repositories
    subgraph Repositories
        L -->|Interacts with| M[Database]
        L -->|Interacts with| N[CSV Files]
    end

    %% Database
    subgraph Database
        M[H2 Database]
        M[PostgreSQL Database]
    end

    %% CSV Files
    subgraph CSV Files
        N[CSV File]
    end

    %% Data Flow
    K -->|Returns Data to| J
    J -->|Updates View| I
    I -->|Displays Result| B
    M -->|Returns Data to| L
    N -->|Returns Data to| L
    L -->|Returns Result to| K

%% AppUser Repository Class Diagram
classDiagram
    %% Interfaces
    class AppUserRepository {
        +save(user)
        +findAll()
        +findById(id)
        +findByUsername(username)
        +deleteById(id)
        +existsById(id)
    }

    class JpaRepository {
        +save(entity)
        +findById(id)
        +findAll()
        +deleteById(id)
    }

    class BaseCsvRepository {
        +save(entity)
        +findAll()
        +findById(id)
        +deleteById(id)
        +existsById(id)
    }

    class AppUserCsvRepository {
        +findByUsername(username)
    }

    %% Implementations
    class AppUserJpaRepository {
    }

    class AppUserCsvRepositoryImpl {
        +save(user)
        +findAll()
        +findById(id)
        +deleteById(id)
        +findByUsername(username)
    }

    class AppUser {
        id
        username
        password
        email
    }

    %% Relationships
    AppUserRepository <|-- AppUserJpaRepository
    AppUserRepository <|-- AppUserCsvRepository
    BaseCsvRepository <|-- AppUserCsvRepository
    JpaRepository <|-- AppUserJpaRepository
    AppUserCsvRepositoryImpl ..|> AppUserCsvRepository
    AppUserCsvRepositoryImpl --> AppUser

    %% Notes
    note for AppUserJpaRepository "Repository: jpaUserRepository, Profile: jpa"
    note for AppUserCsvRepositoryImpl "Repository: csvUserRepository, Profile: csv, Primary"

%% CSV Plugin Integration Architecture
flowchart TD
    subgraph Main Application
        A[Spring Boot App] -->|uses| B[Repository Interfaces]
        B -->|defines| C[BaseCsvRepository]
        B -->|defines| D[AppUserCsvRepository]
        B -->|defines| E[ProjectCsvRepository]
    end

    subgraph CSV Plugin
        F[CSV Implementation] -->|implements| C
        F -->|implements| D
        F -->|implements| E
        F -->|uses| G[CsvHelper]
        F -->|uses| H[CsvFileHelper]
        G -->|reads/writes| I[CSV Files]
    end

    subgraph Configuration
        J[Profile: csv] -->|activates| F
        K[Profile: jpa] -->|activates| L[JPA Implementation]
    end

%%% ProjectHub Complete Architecture
flowchart TD
    %% User Interaction Layer
    subgraph UI ["User Interaction Layer"]
        A[Desktop User] & B[Web User]
        A -->|uses| C[JavaFX Client]
        B -->|uses| D[Angular Web App]
    end

    %% Client Applications
    subgraph Desktop ["Desktop Application"]
        C -->|contains| E[FXML Views]
        C -->|uses| F[ViewModels]
        C -->|managed by| G[Controllers]
        F -->|updates| E
        G -->|coordinates| F
    end

    subgraph Web ["Web Application"]
        D -->|contains| H[Angular Components]
        D -->|uses| I[Services]
        D -->|manages| J[State]
        I -->|updates| H
        J -->|coordinates| I
    end

    %% API Gateway
    subgraph Gateway ["API Gateway Layer"]
        K[Spring Cloud Gateway]
        C & D -->|requests via| K
        K -->|routes to| L[Backend Services]
    end

    %% Core Application
    subgraph Backend ["Backend Services"]
        L -->|processes| M[REST Controllers]
        M -->|uses| N[Service Layer]
        N -->|implements| O[Business Logic]
        O -->|uses| P[Repository Layer]
    end

    %% Data Access Layer
    subgraph DataAccess ["Data Access Layer"]
        P -->|abstracts| Q[Repository Interfaces]
        Q -->|implemented by| R[JPA Repositories]
        Q -->|implemented by| S[CSV Repositories]
    end

    %% Plugin Architecture
    subgraph CSVPlugin ["CSV Plugin"]
        S -->|uses| T[CSV Config]
        S -->|managed by| U[CSV Properties]
        S -->|utilizes| V[CSV Helpers]
        V -->|reads/writes| W[CSV Files]
        T -->|configures| U
    end

    %% Database Layer
    subgraph Storage ["Storage Layer"]
        R -->|persists to| X[PostgreSQL]
        R -->|persists to| Y[H2 Database]
        S -->|persists to| W
    end

    %% Cross-Cutting Concerns
    subgraph CrossCutting ["Cross-Cutting Concerns"]
        Z[Security]
        AA[Logging]
        AB[Monitoring]
        AC[Caching]
    end

    %% Support Services
    subgraph Support ["Support Services"]
        AD[Auth Service]
        AE[Email Service]
        AF[Audit Service]
    end

    %% Relationships
    Backend -->|uses| CrossCutting
    Backend -->|integrates| Support
    Gateway -->|authenticates via| AD
    N -->|logs via| AA
    N -->|monitors via| AB
    N -->|caches via| AC
    O -->|audits via| AF
    O -->|notifies via| AE

    %% Styling
    classDef ui fill:#D5E8D4,stroke:#82B366
    classDef client fill:#DAE8FC,stroke:#6C8EBF
    classDef gateway fill:#FFE6CC,stroke:#D79B00
    classDef backend fill:#E1D5E7,stroke:#9673A6
    classDef data fill:#F8CECC,stroke:#B85450
    classDef plugin fill:#FFF2CC,stroke:#D6B656
    classDef support fill:#D4E1F5,stroke:#3A5F8B
    
    class A,B ui
    class C,D,E,F,G,H,I,J client
    class K gateway
    class L,M,N,O backend
    class P,Q,R data
    class S,T,U,V,W plugin
    class AD,AE,AF support

    %% Notes
    note "Handles client requests" K
    note "Manages business logic" N
    note "Plugin-based storage" S
    note "Cross-cutting concerns" Z

%%% ProjectHub Extended Architecture with Plugins
flowchart TD
    %% User Interaction Layer
    subgraph UI ["User Interaction Layer"]
        A[Desktop User] & B[Web User]
        A -->|uses| C[JavaFX Client] 
        B -->|uses| D[Angular Web App]
    end

    %% Client Applications 
    subgraph Desktop ["Desktop Application"]
        C -->|contains| E[FXML Views]
        C -->|uses| F[ViewModels]
        C -->|managed by| G[Controllers]
        F -->|updates| E
        G -->|coordinates| F
    end

    subgraph Web ["Web Application"]
        D -->|contains| H[Angular Components]
        D -->|uses| I[Services] 
        D -->|manages| J[State]
        I -->|updates| H
        J -->|coordinates| I
    end

    %% API Gateway
    subgraph Gateway ["API Gateway Layer"]
        K[Spring Cloud Gateway]
        C & D -->|requests via| K
        K -->|routes to| L[Backend Services]
    end

    %% Core Application
    subgraph Backend ["Backend Services"]
        L -->|processes| M[REST Controllers]
        M -->|uses| N[Service Layer]
        N -->|implements| O[Business Logic]
        O -->|uses| P[Repository Layer]
        O -->|uses| AG[Plugin Manager]
    end

    %% Plugin Architecture
    subgraph Plugins ["Plugin System"]
        AG -->|manages| AH[Plugin Registry]
        AH -->|registers| AI[Storage Plugins]
        AH -->|registers| AJ[Export Plugins]
        AH -->|registers| AK[Integration Plugins]
        AH -->|registers| AL[Auth Plugins]
        AH -->|registers| AM[Notification Plugins]
    end

    %% Storage Plugins
    subgraph StoragePlugins ["Storage Plugins"]
        AI -->|includes| S[CSV Plugin]
        AI -->|includes| AN[XML Plugin]
        AI -->|includes| AO[JSON Plugin]
        AI -->|includes| AP[NoSQL Plugin]
    end

    %% Export Plugins
    subgraph ExportPlugins ["Export Plugins"]
        AJ -->|includes| AQ[PDF Export]
        AJ -->|includes| AR[Excel Export]
        AJ -->|includes| AS[Report Generator]
    end

    %% Integration Plugins
    subgraph IntegrationPlugins ["Integration Plugins"]
        AK -->|includes| AT[GitHub]
        AK -->|includes| AU[Jira]
        AK -->|includes| AV[Trello]
    end

    %% Auth Plugins
    subgraph AuthPlugins ["Auth Plugins"]
        AL -->|includes| AW[OAuth]
        AL -->|includes| AX[LDAP]
        AL -->|includes| AY[SSO]
    end

    %% Notification Plugins
    subgraph NotificationPlugins ["Notification Plugins"]
        AM -->|includes| AZ[Email]
        AM -->|includes| BA[Slack]
        AM -->|includes| BB[Teams]
    end

    %% Cross-Cutting & Support Services remain same as before
    ...

    %% Additional Styling
    classDef plugin fill:#FFF2CC,stroke:#D6B656
    classDef pluginSystem fill:#FFE6CC,stroke:#D79B00
    
    class AG,AH pluginSystem
    class AI,AJ,AK,AL,AM,S,AN,AO,AP,AQ,AR,AS,AT,AU,AV,AW,AX,AY,AZ,BA,BB plugin

    %% Notes
    note "Plugin System Core" AG
    note "Extensible Architecture" 
    
%% Storage Management flowchart
flowchart TD
    subgraph Primary["Primary Storage: PostgreSQL"]
        PG[PostgreSQL Database]
        style PG fill:#f5d1c4,stroke:#333
    end

    subgraph Local["Local Storage"]
        H2[H2 Database]
        CSV[CSV Files]
        style H2 fill:#d1f5c4,stroke:#333
        style CSV fill:#c4d1f5,stroke:#333
    end

    subgraph Sync["Synchronization"]
        SYNC[Sync Service]
        style SYNC fill:#f5c4d1,stroke:#333
    end

    Desktop[Desktop App] -->|Offline| H2
    Desktop -->|Offline| CSV
    Desktop -->|Online| SYNC
    Web[Web App] -->|Direct| PG
    SYNC -->|Sync when online| PG
    H2 -->|Sync| SYNC
    CSV -->|Sync| SYNC

%% Project Structure Diagram V2
flowchart TB
    subgraph ProjectHub ["ProjectHub Root Project"]
        direction TB
        A[settings.gradle]
        A ---> B[build.gradle]
        A ---> C[build-logic]
        A ---> D[core]
        A ---> E[desktop-ui]
        A ---> F[frontend]
        A ---> G[plugins]
    end

    subgraph BuildLogic ["build-logic Module"]
        C1[build.gradle]
        C2[src/main/groovy/...]
    end

    subgraph Core ["core Module"]
        D1[build.gradle]
        D2[src/main/java/com/projecthub/core/...]
    end

    subgraph DesktopUI ["desktop-ui Module"]
        E1[build.gradle]
        E2[src/main/java/com/projecthub/ui/...]
        E3[Depends on -> Core]
    end

    subgraph Frontend ["frontend Module (Angular App)"]
        F1[package.json]
        F2[src/...]
    end

    subgraph Plugins ["plugins Module"]
        G1[projecthub-csv-plugin]
        G1 ---> G1a[build.gradle]
        G1 ---> G1b[src/main/java/...]
        G1c[Depends on -> Core]
    end

    %% Connections
    A ---> C
    A ---> D
    A ---> E
    A ---> F
    A ---> G

    C ---> C1
    D ---> D1
    E ---> E1
    F ---> F1
    G ---> G1

    D -->|Dependency| E3
    G1 -->|Dependency| G1c

%% Modular architecture diagram
flowchart LR
    subgraph Core ["core Module"]
        direction TB
        C1[Domain Models]
        C2[Services]
        C3[Repositories]
    end

    subgraph DesktopUI ["desktop-ui Module"]
        direction TB
        D1[Controllers]
        D2[Views (FXML)]
        D3[ViewModels]
    end

    subgraph Frontend ["frontend Module"]
        direction TB
        F1[Components]
        F2[Services]
        F3[Models]
    end

    subgraph Plugins ["plugins Module"]
        direction TB
        P1[CSV Plugin]
        P2[Other Plugins]
    end

    subgraph Backend ["Spring Boot Application"]
        direction TB
        B1[REST Controllers]
        B2[Service Layer]
        B3[Repository Layer]
    end

    %% Dependencies
    D1 -->|Uses| D3
    D3 -->|Uses| C2
    F1 -->|Uses| F2
    F2 -->|Calls| B1
    B1 -->|Uses| B2
    B2 -->|Uses| C2
    C2 -->|Implements| C1
    C2 -->|Uses| C3
    P1 -->|Implements| C3

    %% Plugin Integration
    P1 -->|Extends| C3

    %% Notes
    note over DesktopUI: Depends on `core`
    note over Plugins: Depends on `core`
    note over Frontend: Interacts via REST API

    %% External Dependencies
    Backend -->|Depends on| Core
    DesktopUI -->|Depends on| Core
    Plugins -->|Depends on| Core

%% Build Process Diagram
flowchart TD
    subgraph BuildProcess ["Gradle Build Process"]
        direction TB
        S1[settings.gradle]
        S2[Include Modules]
        S3[Define Dependencies]
        S4[Run Build Tasks]

        S1 --> S2 --> S3 --> S4
    end

    subgraph Modules
        M1[build-logic]
        M2[core]
        M3[desktop-ui]
        M4[frontend]
        M5[plugins/projecthub-csv-plugin]
    end

    subgraph BuildTasks
        T1[clean]
        T2[compile]
        T3[test]
        T4[assemble]
    end

    %% Connections
    S4 --> T1
    S4 --> T2
    S4 --> T3
    S4 --> T4

    T2 -->|Compiles| M1 & M2 & M3 & M5
    T2 -->|Builds| M4 (via npm scripts)

    T3 -->|Runs Tests| M1 & M2 & M3 & M5

    T4 -->|Assembles Artifacts| M1 & M2 & M3 & M5

%% Dependency Graph
flowchart LR
    subgraph Modules
        A[build-logic]
        B[core]
        C[desktop-ui]
        D[plugins/projecthub-csv-plugin]
        E[frontend]
    end

    %% Dependencies
    C --> B
    D --> B
    E -->|Interacts via REST API| B

    %% External Dependencies
    B -->|Uses| SpringBoot
    C -->|Uses| JavaFX
    E -->|Uses| Angular

    %% Build Logic Dependency
    AllModules --> A

%% AppStart Orchestration
sequenceDiagram
    participant MainClass as desktop-ui/MainClass
    participant Core as core
    participant Plugin as plugins/CSVPlugin
    participant BuildLogic as build-logic

    MainClass->>Core: Initialize Services
    Core->>Plugin: Load Plugins
    Plugin->>Core: Register Repositories
    Core->>MainClass: Services Ready
    BuildLogic-->>AllModules: Apply Custom Plugins
    MainClass->>MainClass: Start Application

%% Interaction Diagram with Build-Logic
flowchart LR
    subgraph BuildLogic ["build-logic Module"]
        BL1[Custom Gradle Plugins]
        BL2[Shared Configurations]
    end

    subgraph Modules
        M1[core]
        M2[desktop-ui]
        M3[plugins/projecthub-csv-plugin]
        M4[frontend]
    end

    BL1 --> M1
    BL1 --> M2
    BL1 --> M3
    BL1 --> M4

    BL2 --> M1
    BL2 --> M2
    BL2 --> M3
    BL2 --> M4

%% Plugin Architecture Diagram
flowchart TB
    subgraph CoreModule ["core Module"]
        direction TB
        C1[Service Interfaces]
        C2[Repository Interfaces]
    end

    subgraph PluginsModule ["plugins Module"]
        direction TB
        P1[projecthub-csv-plugin]
        P1a[CSVRepository Implementations]
    end

    subgraph Application ["Application Modules"]
        A1[desktop-ui]
        A2[backend services]
    end

    %% Connections
    P1 -->|Implements| C2
    C1 -->|Used by| A1 & A2
    C2 -->|Implemented by| P1 & Other Repos

    %% Profiles
    subgraph Profiles
        Pr1[Profile: csv]
        Pr2[Profile: jpa]
    end

    A1 -->|Depends on| CoreModule
    A2 -->|Depends on| CoreModule

    Pr1 -->|Activates| P1
    Pr2 -->|Activates| JPARepositories

%% Multi-Project Build Lifecycle Diagram
graph LR
    subgraph BuildLifecycle ["Gradle Multi-Project Build Lifecycle"]
        direction TB
        Step1[Initialization Phase]
        Step2[Configuration Phase]
        Step3[Execution Phase]
    end

    subgraph Modules
        Module1[core]
        Module2[desktop-ui]
        Module3[plugins]
        Module4[build-logic]
    end

    Step1 --> Step2 --> Step3

    %% Initialization Phase
    Step1 -->|Includes| Module1 & Module2 & Module3 & Module4

    %% Configuration Phase
    Step2 -->|Configures Build Scripts| Module1 & Module2 & Module3 & Module4
    Step2 -->|Applies Plugins| build-logic

    %% Execution Phase
    Step3 -->|Executes Tasks| Module1 & Module2 & Module3

    %% Build Logic Application
    build-logic -->|Provides| Custom Plugins & Configurations

%% Version Management Evolution Flowchart
graph TD
    A[Initial State] --> B[Security Enhancement]
    B --> C[Version Management]
    
    subgraph "Security Phase"
        B --> B1[JWT Implementation]
        B --> B2[WebAuthn/Passkey]
        B --> B3[Spring Security]
    end
    
    subgraph "Version Management Phase"
        C --> C1[libs.versions.toml]
        C1 --> C2[dependencies.gradle]
        C2 --> C3[gradle.properties]
    end
    
    subgraph "BuildSrc Evolution"
        D[ProjectHubPlugin.java] --> D1[Remove Version Logic]
        D1 --> D2[Pure Configuration]
        D2 --> D3[Test Setup]
    end

%% Dependency Resolution Flow
flowchart LR
    A[gradle.properties] --> B[settings.gradle]
    B --> C[Plugin Versions]
    
    D[dependencies.gradle] --> E[Library Versions]
    E --> F[Project Dependencies]
    
    subgraph "Version Resolution"
        C --> G[BuildSrc Plugin]
        F --> G
    end
    
    G --> H[Subprojects]

%% Build Configuration Architecture
C4Context
    title Build System Architecture
    
    Enterprise_Boundary(b0, "Gradle Build System") {
        System(s1, "gradle.properties", "Plugin Versions")
        System(s2, "settings.gradle", "Plugin Management")
        System(s3, "dependencies.gradle", "Library Versions")
        
        System_Boundary(b1, "BuildSrc") {
            Container(c1, "ProjectHubPlugin", "Configuration")
        }
    }
    
    Rel(s1, s2, "Provides versions")
    Rel(s2, c1, "Configures plugins")
    Rel(s3, c1, "Provides dependencies")

%% System Context Diagram
C4Context
    title System Context Diagram for ProjectHub

    Person(student, "Student", "A student user who submits projects and collaborates in teams")
    Person(instructor, "Instructor", "An instructor who manages projects and evaluates submissions")
    Person(admin, "Administrator", "System administrator who manages users and configurations")

    System(projectHub, "ProjectHub System", "Core project management and evaluation platform")

    System_Ext(db, "PostgreSQL Database", "Stores all project data, user information, and submissions")
    System_Ext(authSystem, "Authentication Service", "Handles user authentication and authorization")
    System_Ext(storageSystem, "File Storage Service", "Manages project files and submissions")
    System_Ext(notificationSystem, "Notification Service", "Handles email and system notifications")
    System_Ext(csvSystem, "CSV Import/Export", "Handles data import/export through CSV files")

    Rel(student, projectHub, "Uses", "HTTPS/REST")
    Rel(instructor, projectHub, "Manages", "HTTPS/REST")
    Rel(admin, projectHub, "Administers", "HTTPS/REST")

    Rel(projectHub, db, "Reads from and writes to", "JPA/JDBC")
    Rel(projectHub, authSystem, "Authenticates users", "JWT/OAuth2")
    Rel(projectHub, storageSystem, "Stores and retrieves files", "API")
    Rel(projectHub, notificationSystem, "Sends notifications", "SMTP/API")
    Rel(projectHub, csvSystem, "Imports/Exports data", "CSV Plugin")

    UpdateLayoutConfig($c4ShapeInRow="3", $c4BoundaryInRow="1")

%% Container Diagram
C4Container
    title Container Diagram for ProjectHub

    Person(student, "Student", "A student using the application")
    Person(instructor, "Instructor", "An instructor managing projects")
    Person(admin, "Administrator", "System administrator")

    System_Boundary(projectHub, "ProjectHub Platform") {
        Container(desktopUi, "Desktop UI", "JavaFX, Spring Boot", "Provides desktop interface for project management")
        
        Container(core, "Core Module", "Java, Spring Boot", "Handles business logic, data processing, and service coordination")
        
        Container(csvPlugin, "CSV Plugin", "Java, PF4J", "Manages data import/export through CSV files")
        
        ContainerDb(db, "Database", "PostgreSQL", "Stores project data, user information, and configurations")
        
        Container(auth, "Authentication", "Spring Security, JWT", "Handles user authentication and authorization")
        
        Container(fileStore, "File Storage", "Local/Network Storage", "Manages project files and attachments")
    }

    Rel(student, desktopUi, "Uses", "JavaFX UI")
    Rel(instructor, desktopUi, "Uses", "JavaFX UI")
    Rel(admin, desktopUi, "Administers", "JavaFX UI")

    Rel(desktopUi, core, "API Calls", "Spring Boot")
    Rel(desktopUi, auth, "Authenticates", "JWT")
    
    Rel(core, db, "Reads/Writes", "JPA")
    Rel(core, fileStore, "Stores files", "File I/O")
    Rel(core, csvPlugin, "Uses", "Plugin API")
    
    Rel(csvPlugin, db, "Reads/Writes", "JPA")
    Rel(auth, db, "Validates", "JDBC")

    UpdateLayoutConfig($c4ShapeInRow="3", $c4BoundaryInRow="1")

%% Component Diagram
C4Component
    title Component Diagram for ProjectHub

    Container_Boundary(core, "Core Module") {
        Component(controllers, "REST Controllers", "Spring MVC", "Handles HTTP requests and responses")
        Component(services, "Service Layer", "Spring Services", "Implements business logic")
        Component(repos, "Repositories", "Spring Data", "Data access layer")
        Component(models, "Domain Models", "JPA Entities", "Business domain objects")
        Component(dtos, "DTOs", "Data Transfer", "Objects for API responses")
        Component(mappers, "Mappers", "MapStruct", "Entity-DTO conversion")
    }

    Container_Boundary(desktop, "Desktop UI Module") {
        Component(fxml, "FXML Views", "JavaFX", "UI layout definitions")
        Component(viewmodels, "ViewModels", "JavaFX Properties", "UI state and logic")
        Component(controllers_ui, "UI Controllers", "JavaFX", "Handle user interactions")
        Component(services_ui, "UI Services", "Spring", "Navigation, notifications")
    }

    Container_Boundary(data, "Data Access") {
        ContainerDb(db, "PostgreSQL", "Stores application data")
        ContainerDb(h2, "H2 Database", "Local development database")
    }

    Rel(controllers, services, "Uses")
    Rel(services, repos, "Uses")
    Rel(services, mappers, "Uses")
    Rel(repos, models, "Manages")
    Rel(mappers, dtos, "Creates")
    Rel(mappers, models, "Maps from/to")
    
    Rel(controllers_ui, viewmodels, "Updates")
    Rel(viewmodels, services, "Calls")
    Rel(controllers_ui, fxml, "Controls")
    Rel(controllers_ui, services_ui, "Uses")
    
    Rel(repos, db, "Reads/Writes")
    Rel(repos, h2, "Reads/Writes (Dev)")

    UpdateLayoutConfig($c4ShapeInRow="3", $c4BoundaryInRow="1")

%% Core Module Structure
flowchart TD
 subgraph subGraph0["Core Module Structure"]
        Entities["entities"]
        Domain["domain"]
        Models["models"]
        DTOs["dtos"]
        Auth["auth"]
        Services["services"]
        User["user"]
        Team["team"]
        Rest["rest-api"]
        Controllers["controllers"]
  end
 subgraph subGraph1["Build Configuration"]
        Dependencies["dependencies.gradle"]
        RootBuild["build.gradle"]
        ProjectHubPlugin["ProjectHubPlugin.groovy"]
        CoreBuild["build.gradle"]
        Modulith["modulith.gradle"]
  end
    ProjectHub["ProjectHub Monorepo"] --> BuildSrc["buildSrc"] & Modules["modules"] & Settings["settings.gradle"] & RootBuild
    BuildSrc --> Dependencies & Modulith & ProjectHubPlugin
    Modules --> Core["core"]
    Core --> Controllers & Domain & Services & CoreBuild
    Domain --> Entities & Models & DTOs
    Services --> Auth & User & Team
    Controllers --> Rest
    RootBuild -.-> Dependencies
    CoreBuild -.-> ProjectHubPlugin
    ProjectHubPlugin -.-> Modulith & Dependencies
     BuildSrc:::buildConfig
     Dependencies:::buildConfig
     Modulith:::buildConfig
     ProjectHubPlugin:::buildConfig
     Core:::module
     Controllers:::module
     Domain:::module
     Services:::module
    classDef default fill:#f9f,stroke:#333,stroke-width:2px
    classDef buildConfig fill:#bbf,stroke:#333,stroke-width:2px
    classDef module fill:#bfb,stroke:#333,stroke-width:2px

sequenceDiagram
    actor User
    participant Navigation
    participant Dashboard
    participant CohortList
    participant CohortDetails
    participant TeamList
    participant Toast
    participant Dialog
    participant CohortViewModel
    participant CohortService

    %% Main Navigation Flow
    User->>Navigation: Select "Cohorts"
    Navigation->>CohortList: loadView("/cohorts")
    Note right of CohortList: Initializes the Cohort List view
    CohortList->>CohortList: initialize()
    CohortList->>CohortList: loadCohorts()
    Note right of CohortList: Fetches and displays all cohorts

    %% Create Flow
    User->>CohortList: clickAddCohort()
    CohortList->>CohortDetails: showCohortDetails(null)
    Note right of CohortDetails: Opens an empty form for new cohort
    CohortDetails->>CohortDetails: initializeForm()
    Note right of CohortDetails: Sets up form fields and bindings

    alt Save New Cohort
        User->>CohortDetails: fillForm()
        Note right of CohortDetails: User enters cohort details
        User->>CohortDetails: clickSave()
        CohortDetails->>CohortDetails: validateInput()
        Note right of CohortDetails: Validates form data
        CohortDetails->>CohortViewModel: saveCohort(cohortDTO)
        CohortViewModel->>CohortService: saveCohort()
        Note right of CohortService: Persists new cohort to the database
        CohortService-->>CohortViewModel: savedCohort
        CohortViewModel-->>CohortDetails: updateUI()
        Note right of CohortDetails: Updates the UI with the new cohort
        CohortDetails->>Toast: showSuccess("Cohort saved")
        Note right of Toast: Displays success message to the user
        CohortDetails->>CohortList: refreshList()
        Note right of CohortList: Reloads the cohort list to include the new cohort
    else Cancel Creation
        User->>CohortDetails: clickCancel()
        CohortDetails->>Dialog: showConfirmation("Discard changes?")
        Note right of Dialog: Asks user to confirm discarding changes
        Dialog-->>CohortDetails: confirmed
        CohortDetails->>CohortList: navigateBack()
        Note right of CohortList: Returns to the Cohort List view without saving
    end

    %% Edit Flow
    User->>CohortList: selectCohort(id)
    Note right of CohortList: User selects a cohort to edit
    CohortList->>CohortDetails: loadCohort(id)
    CohortDetails->>CohortViewModel: loadCohort(id)
    Note right of CohortViewModel: Retrieves cohort details from the service
    CohortViewModel->>CohortService: getCohortById(id)
    CohortService-->>CohortViewModel: cohortDTO
    CohortViewModel-->>CohortDetails: updateUI()
    Note right of CohortDetails: Populates the form with existing cohort data
    CohortDetails->>TeamList: loadTeams(cohortId)
    Note right of TeamList: Loads teams associated with the selected cohort

    %% Delete Flow
    User->>CohortDetails: clickDelete()
    CohortDetails->>Dialog: showDeleteWarning()
    Note right of Dialog: Confirms deletion action with the user
    alt Confirm Delete
        Dialog-->>CohortDetails: confirmed
        CohortDetails->>CohortViewModel: deleteCohort(id)
        CohortViewModel->>CohortService: deleteCohort()
        Note right of CohortService: Deletes the cohort from the database
        CohortService-->>CohortViewModel: success
        CohortViewModel-->>CohortDetails: clear()
        Note right of CohortDetails: Clears the form and related data
        CohortDetails->>Toast: showSuccess("Deleted")
        Note right of Toast: Displays deletion success message
        CohortDetails->>CohortList: navigateBack()
        Note right of CohortList: Returns to the updated Cohort List view
    else Cancel Delete
        Dialog-->>CohortDetails: cancelled
        Note right of CohortDetails: Remains on Cohort Details view without deleting
    end

    %% Navigation Guards
    User->>CohortDetails: clickBack()
    CohortDetails->>CohortViewModel: hasUnsavedChanges()
    Note right of CohortViewModel: Checks if there are unsaved changes in the form
    alt Has Changes
        CohortDetails->>Dialog: showDiscardWarning()
        Note right of Dialog: Prompts user to discard unsaved changes
        Dialog-->>CohortDetails: confirmed
        CohortDetails->>CohortList: navigateBack()
        Note right of CohortList: Returns to Cohort List view after discarding changes
    else No Changes
        CohortDetails->>CohortList: navigateBack()
        Note right of CohortList: Returns to Cohort List view directly
    end

%% Mini sequence diagram CohortDetails
sequenceDiagram
    actor User
    participant UI as User Interface
    participant ViewModel as Cohort ViewModel
    participant Service as Cohort Service
    participant Database as Database
    participant Notification as Notification System

    %% Main Navigation
    User->>UI: Navigate to Cohorts
    UI->>ViewModel: Load Cohort List
    ViewModel->>Service: Fetch Cohorts
    Service->>Database: Retrieve Cohorts
    Database-->>Service: Provide Cohort Data
    Service-->>ViewModel: Return Cohorts
    ViewModel-->>UI: Display Cohort List

    %% Create Cohort
    User->>UI: Add New Cohort
    UI->>ViewModel: Initialize New Cohort Form
    User->>UI: Enter Cohort Details
    UI->>ViewModel: Submit New Cohort
    ViewModel->>Service: Save Cohort
    Service->>Database: Persist Cohort
    Database-->>Service: Confirmation
    Service-->>ViewModel: Cohort Saved
    ViewModel-->>UI: Update Cohort List
    UI->>Notification: Show Success Message

    %% Edit Cohort
    User->>UI: Select Cohort to Edit
    UI->>ViewModel: Load Cohort Details
    ViewModel->>Service: Fetch Cohort by ID
    Service->>Database: Retrieve Cohort Data
    Database-->>Service: Provide Cohort Details
    Service-->>ViewModel: Return Cohort Data
    ViewModel-->>UI: Populate Edit Form
    User->>UI: Modify Cohort Details
    UI->>ViewModel: Submit Changes
    ViewModel->>Service: Update Cohort
    Service->>Database: Update Cohort Record
    Database-->>Service: Confirmation
    Service-->>ViewModel: Cohort Updated
    ViewModel-->>UI: Refresh Cohort List
    UI->>Notification: Show Update Success

    %% Delete Cohort
    User->>UI: Delete Cohort
    UI->>ViewModel: Confirm Deletion
    ViewModel->>Service: Remove Cohort
    Service->>Database: Delete Cohort Record
    Database-->>Service: Confirmation
    Service-->>ViewModel: Cohort Deleted
    ViewModel-->>UI: Update Cohort List
    UI->>Notification: Show Deletion Success

    %% Navigation Guard
    User->>UI: Attempt to Navigate Away
    UI->>ViewModel: Check for Unsaved Changes
    alt Unsaved Changes Present
        ViewModel->>UI: Prompt to Save or Discard
        User->>UI: Choose to Discard
        UI->>ViewModel: Proceed with Navigation
    else No Unsaved Changes
        UI->>ViewModel: Proceed with Navigation
    end

mindmap
  root((🎯 ProjectHub Platform))
    Architecture((🏗️ Architecture))
      Hex[🔷 Hexagonal]
        Ports[🔌 Ports and Adapters]
      DDD[🧩 Domain-Driven Design]
        Agg[📦 Aggregates]
        BC[🎯 Bounded Contexts]
      CQRS[⚡ CQRS]
        Commands[📝 Commands]
        Queries[🔍 Queries]
      Events[📡 Event-Driven]
        Async[🔄 Async Events]
        Integration[🔗 Integration Events]

    Domains((🏢 Core Domains))
      School[🏫 School Management]
        Classes[📚 Classes]
        Teachers[👩‍🏫 Teachers]
      Teams[👥 Team Management]
        Projects[📋 Projects]
        Tasks[✅ Tasks]
      Users[👤 User Management]
        Auth[🔐 Authentication]
        Roles[👑 Authorization]
      Cohorts[👨‍👩‍👧‍👦 Cohort Management]
        Groups[🎓 Study Groups]
        Progress[📈 Progress Tracking]

    Principles((⚖️ Best Practices))
      SOLID[💎 SOLID]
        SRP[1️⃣ Single Responsibility]
        OCP[2️⃣ Open-Closed]
        LSP[3️⃣ Liskov Substitution]
        ISP[4️⃣ Interface Segregation]
        DIP[5️⃣ Dependency Inversion]
      DRY[🎯 Don't Repeat Yourself]
      KISS[💫 Keep It Simple]
      YAGNI[⚡ You Ain't Gonna Need It]

    Infrastructure((⚙️ Infrastructure))
      Database[💾 Persistence]
        SQL[📀 SQL Database]
        Cache[⚡ Caching]
      API[🔌 APIs]
        REST[🌐 REST Endpoints]
        GraphQL[⚡ GraphQL]
      Testing[🧪 Testing]
        Unit[🔬 Unit Tests]
        Integration[🔗 Integration Tests]
      CI_CD[🔄 CI/CD]
        Pipeline[⚙️ Build Pipeline]
        Deploy[🚀 Deployment]

    CrossCutting((🔧 Cross-Cutting))
      Logging[📝 Logging]
      Security[🔒 Security]
      Monitoring[📊 Monitoring]
      Documentation[📚 Documentation]
