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

%% ongoing

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

<!-- chefs kiss -->
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
