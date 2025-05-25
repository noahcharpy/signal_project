# UML Diagram Documentation

## Alert Generation System 
The Alert Generation System is designed to monitor patient data in real time and generate alerts when critical thresholds are exceeded.
The AlertGenerator class evaluates patient data by retrieving it from the DataStorage, assessing whether conditions such as heart rate or blood pressure surpass patient-specific limits.
This separation of concerns ensures that evaluation and data storage remain modular.
The Alert class encapsulates the details of an alert, such as the patientId, condition, and timestamp, ensuring the system keeps track of what triggered the alert and when.
The AlertManager is responsible for dispatching these alerts to medical staff, emphasizing the system’s responsiveness in a hospital environment.
  
The system adheres to clear responsibilities: data evaluation (AlertGenerator), alert representation (Alert), and alert routing (AlertManager).
The modular approach supports extensibility, making it easy to add new types of alerts or dispatch mechanisms without reworking the entire system.
## Data Storage System
The Data Storage System handles the secure collection, retrieval, and management of patient data. 
At the core, the DataStorage class maintains a mapping between patients and their records, providing methods to add new measurements, query past records, and manage old data based on a defined DeletionPolicy.
The Patient class organizes individual patient data, including their measurement records and record types.
User and AccessController classes work together to ensure only authorized personnel can access sensitive patient data.

The DataRetriever class bridges medical staff and stored data, allowing secure queries with appropriate checks in place.
This layered design separates storage management, data access, and security responsibilities, reinforcing modularity and maintainability.
## Patient Identification System
The Patient Identification System ensures that incoming data is accurately matched to the correct hospital patient.
The PatientIdentifier class focuses on matching incoming signal data (via SignalPatient) to hospital records (via HospitalPatient), ensuring data consistency across systems.
The IdentityManager handles mismatches and ensures system integrity.  The MismatchLogger adds robustness by keeping a record of the mismatches, this allows the administrator to track and resolve issues.

By cleanly separating identification logic (PatientIdentifier), data source handling (SignalPatient), and oversight (IdentityManager), the system becomes easier to maintain and extend.
## Data Access Layer
The Data Access Layer serves as the system’s entry point for external data sources, ensuring seamless and consistent integration regardless of input format.
The DataListener interface defines a common connection for input channels, with implementations like TCPDataListener, WebSocketDataListener and FileDataListener.
This structure isolates the rest of the system from the specifics of how data arrives.

This design adheres to the Adapter and Strategy patterns, allowing new data sources or formats to be introduced with minimal impact on downstream components.
The clear separation of listening, parsing, and adapting tasks simplifies maintenance and encourages code reuse.