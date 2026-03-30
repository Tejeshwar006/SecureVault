# SecureVault

SecureVault is a beginner-friendly Java command-line password vault created as a BYOP project.
It helps a user store, search, and manage website credentials in one place.

## What This Project Does
- Creates a local vault protected by a master password
- Stores credentials for websites (website, username, password)
- Lets you add, update, search, view, delete, and reveal saved passwords
- Saves data in a local file so it is available next time

## Features
- Master password setup and login
- Add or update credential
- View all saved credentials (masked password display)
- Search credentials by website keyword
- Delete credential by website
- Reveal real password for a selected website
- Save vault to local file on exit
- Simple XOR-based obfuscation for stored data (educational purpose)

## Tech Stack
- Language: Java
- Interface: CLI (Command Line Interface)
- Data structures: HashMap, ArrayList
- File handling: BufferedReader, BufferedWriter

## Project Structure
```
SecureVault/
|- src/
|  |- VaultApp.java
|  |- VaultManager.java
|  |- Credential.java
|  |- FileHandler.java
|  |- EncryptionUtil.java
|  |- MasterPasswordException.java
|  |- EntryNotFoundException.java
|- data/
|  |- vault.dat (created at runtime)
|- run.ps1
|- run-demo.ps1
|- README.md
```

## Prerequisites
- Windows PowerShell
- Java JDK 17 or above

## Setup and Run (Recommended)
1. Open PowerShell in the project folder.
2. Run interactive launcher:

```powershell
powershell -ExecutionPolicy Bypass -File .\run.ps1
```

This will:
1. Find installed JDK
2. Compile source files
3. Start SecureVault in interactive mode

## Manual Compile and Run (Alternative)
If java and javac are already in PATH:

```powershell
javac -d out src/*.java
java -cp out VaultApp
```

## How to Use
1. Start the app.
2. Enter a master password.
   - On first run: this creates a new vault.
   - On later runs: same password unlocks existing vault.
3. Use the menu options:
   - 1: Add or update credential
   - 2: View all credentials
   - 3: Search credential
   - 4: Delete credential
   - 5: View password for website
   - 6: Save and exit

## Master Password
Do not share Master password with anyone.

```powershell
Master123
```

## Data File
- Vault data is stored in data/vault.dat
- If you delete this file, all saved credentials are lost and a new vault will be created on next run

## Module Mapping (Course Alignment)
- Module 1: Java basics, loops, strings, menu flow
- Module 2: OOP with classes and encapsulation
- Module 3: Exception handling and file I/O
- Module 4: Collections (HashMap and ArrayList)

## Security Disclaimer
This is an academic project.
The XOR method used here is for learning and demonstration only.
Do not use this application for real sensitive accounts or production security needs.

## Troubleshooting
1. javac is not recognized
   - Reopen terminal after installing JDK
   - Or run through run.ps1
2. java is not recognized
   - Use full executable path or run.ps1
3. Forgot master password
   - Delete data/vault.dat and create a new vault (existing data will be lost)
