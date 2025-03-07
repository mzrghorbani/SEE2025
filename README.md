# SEE 2025 HLA Simulation Framework 🚀

## High-Level Architecture (HLA) Federates for Lunar Simulation

This repository contains Eclipse-based federates developed for the SEE 2025 event, where multiple simulation entities interact using HLA (High-Level Architecture). The federates simulate key components of a lunar exploration scenario, including landers, spaceports, reference frames, and telemetry systems.

## How to Run the Simulation

1. Start SpaceMaster (Manages synchronization).
2. Launch SpaceReferenceFramePublisher (Publishes coordinate frames).
3. Run HLAtoDON (Links simulation data with DON visualization).
4. Execute Lander and Spaceport (For interaction testing).

To reduce the memory size on GitHub, we have added the content of the Terrain directory to .gitignore. To add them, please use `AitkenBasinDem.tif` and `AitkenBasinDem.tif.meta` from SEE GitHub Repository, [DON-Simulation-Directory](https://github.com/FlaSpaceInst/SEE-Sim-Smackdown/tree/master/Graphics/DON-Simulation-Directory)

## Cloning & Setting Up the Repository in Eclipse

### Clone the SEE2025 GitHub repository to your local machine:

```bash
git clone https://github.com/mzrghorbani/SEE2025.git
```

### Open Eclipse and set the cloned repository as your workspace

Importing Projects into Eclipse and manually import the projects in this order:

- Common
- SpaceReferenceFramePublisher
- MPCNetworkSimServer
- MPCTelemetryWriter
- HLA2DON
- SpaceMaster
- Lander
- Spaceport
- SpaceMaster (optional)

**Note**: Since each project contains a .classpath and .project file, Eclipse should automatically detect dependencies.

### Running pRTI & Initializing the Federation

### Start the pRTI Simulation Environment

1. Navigate to the SpaceMaster project.
2. Run start.bat by double-clicking it.
3. When the terminal opens, type 'A' (for automation) and press Enter.
4. The federation "SEE 2025" should now appear in pRTI.

### Running Federates

#### Step 1: Start SpaceReferenceFramePublisher

- Run it as a Java Application in Eclipse.
- At this stage, two federates should be active in pRTI.
- Tip: Disable console output in Run Configurations to avoid clutter.
  
#### Step 2: Start Lander

- Open a new console tab in Eclipse.
- Run LanderMain.java as a Java Application.

#### Step 3: Start Spaceport

- Open another new console tab in Eclipse.
- Run SpaceportMain.java as a Java Application.

#### Troubleshooting

"LanderMain or SpaceportMain Not Found" Error?

If you see:

```bash
Error: Could not find or load main class LanderMain
Error: Could not find or load main class SpaceportMain
```

#### Try these steps

1. Remove Spaceport from Lander's build path.
2. Run Lander again (it should fail, as expected).
3. Re-add Spaceport to Lander’s build path.
4. Run Lander and Spaceport again.

This often resolves the issue, though the behavior seems inconsistent.

### Testing Message Passing

Once both Lander and Spaceport are running:

1. Click on the Swing window that appears for Lander.
2. Press L to send a landing request to Spaceport.
3. On the Spaceport console, you should see a message prompting you to press 'A' to approve the landing.

### Understanding the Code

- Message handling is implemented using Case-Switch statements in:
  - LanderFederate.java
  - SpaceportFederate.java
- Review these files if you need to debug or modify the logic.

## Contributing

Fork the repo, create a branch, and submit a pull request (PR).
Follow HLA and SEE 2025 guidelines for federation development.

## Contact & Collaboration

For inquiries, open an issue or contact the Brunel Simulation Force team:

- Brunel University London
- Modeling & Simulation Group.
