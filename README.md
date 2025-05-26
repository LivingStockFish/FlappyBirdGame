
# Flappy Bird Game - JavaFX

Welcome to the **Flappy Bird Game** project built using **Java** and **JavaFX 21.0.7**.  
This repository contains the complete source code and resources to compile and run the game locally.

---

## Getting Started

This guide will help you set up the project in your local development environment and run it successfully.

### Prerequisites

- **Java Development Kit (JDK) 21** or later installed  
- **JavaFX 21 SDK for Windows** (download from [Gluon](https://gluonhq.com/products/javafx/))  
- A Java IDE like **Visual Studio Code**, **IntelliJ IDEA**, or **Eclipse**

---

## Folder Structure

The project contains the following key folders:

- `src/` — Java source code files  
- `libs/` — JavaFX SDK (not included, must be downloaded separately)  
- `resources/` — Game assets such as images and sounds  
- `out/` — Output folder for compiled classes (created during build)

> The compiled output files will be generated in the `out` folder by default.  
> You can customize folder settings by modifying `.vscode/settings.json` if using VS Code.

---

## Dependency Management

You can manage dependencies via the `JAVA PROJECTS` view in Visual Studio Code or your IDE’s dependency manager.

More details on managing Java dependencies in VS Code can be found here:  
[https://github.com/microsoft/vscode-java-dependency#manage-dependencies](https://github.com/microsoft/vscode-java-dependency#manage-dependencies)

---

## How to Build and Run Locally

### 1. Clone the Repository

```bash
git clone https://github.com/LivingStockFish/FlappyBirdGame.git
cd FlappyBirdGame
```

### 2. Download JavaFX SDK

Download the JavaFX Windows SDK from [Gluon](https://gluonhq.com/products/javafx/) and extract it to the `libs` folder or any preferred location.

### 3. Compile the Source Code

On Windows PowerShell or terminal, run:

```powershell
mkdir out
$files = Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName }
javac --module-path libs\javafx-sdk-21.0.7\lib --add-modules javafx.controls,javafx.fxml,javafx.media -d out $files
```

> Adjust the path `libs\javafx-sdk-21.0.7\lib` if your JavaFX SDK is in a different location.

### 4. Run the Application

```powershell
java --module-path libs\javafx-sdk-21.0.7\lib --add-modules javafx.controls,javafx.fxml,javafx.media -cp out flappybird.Main
```

---

## About GitHub Actions Workflow

This repository includes a GitHub Actions workflow that:

- Uses a **Windows runner** with JDK 21.0.7 and JavaFX 21.0.7  
- Compiles and runs automated tests for this JavaFX project  

> **Important:** Due to the GUI nature of JavaFX, the UI cannot be displayed or interacted with in GitHub Actions runners. The workflow ensures code compiles and runs without errors but cannot showcase the GUI.

---

## Project Summary

- **Java Version:** 21.0.7  
- **JavaFX Version:** 21.0.7  
- **Build Output:** `out/` folder  
- **Assets:** Stored in `resources/` folder  
- **Source Code:** Located in `src/` folder

---

## Contact & Support

For any questions, issues, or suggestions, please open an issue in this repository or contact the maintainer.

---

Thank you for exploring My the Flappy Bird JavaFX project!  
Happy coding! 🚀
