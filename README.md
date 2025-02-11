# Tetris AI | Kotlin | Genetic Algorithm | Heuristics

A genetic algorithm that finds a matrix multiplication network capable of playing Tetris with super human capabilities. This is done by finding a local maxima in a specified heuristic domain of Tetris. The "AI" is created through simulating games and generational breeding of players. The best player from each generation lives on and breeds with the others, and crossbreeding random players is used for the chance of creating better traits.

https://user-images.githubusercontent.com/48214072/178131757-73f0edd1-7ad9-4ebb-9c0b-347ad0850a5e.mov

<br />This means the first players will fail right away when the program starts.

https://user-images.githubusercontent.com/48214072/178131760-4de5137e-6acb-4949-9608-dc9da5e66d79.mov

<br />This is because the neural nets are initialized with random numbers. Failure causes simulated games and generational breeding to be ran in the background until a player can play the game to the specified score. The simulation and breeding are done using parallel computation, so it is resource heavy and will use nearly 100% of your CPU.

## Generating Players | Crossover
1) The best player from the last generation is kept and added to the next generation.
2) The best player breeds with 5 of the other players, and their children live on.
3) Random players then breed and their children live on in the next generation.
4) This process is continued until a player can reach the specified score!

## Neural Net | Koma
The neural nets are made with [koma](https://github.com/kyonifer/koma) which is a scientific computing environment for Kotlin.

## Visuals | Processing 4
The visuals are created using [Processing](https://processing.org/). A beta version is included in this repo, because the older stable versions do not play nicely with Apple Silicon.

## How to Run Pre-Built Jar File
- run: `java -jar ./pre-built/TetrisAI-1.0.jar` within powershell or terminal

## How To Build/Run On Windows
- run the following commands within Windows PowerShell
- make sure you have the [java development kit](https://dev.java/download/) installed on your machine and the [JAVA_HOME](https://docs.oracle.com/en/cloud/saas/enterprise-performance-management-common/diepm/epm_set_java_home_104x6dd63633_106x6dd6441c.html) environment variable set
- download [Chocolately](https://chocolatey.org/install) (a package manager for Windows)
- use Chocolately to install Gradle (allows you to build jar files)
  - run: `choco install gradle`
- download this repository or run: `git clone https://github.com/kevinpbaker/TetrisAI.git`
- navigate to downloaded folder in the powershell by running: `cd INSERT_DIRECTORY_PATH_WHERE_YOU_SAVED_REPO_HERE`
- run: `gradle build`
- run: `java -jar .\build\libs\TetrisAI-1.0.jar`

## How to Build/Run On MacOS
- run the following commands within Terminal
- make sure you have the [java development kit](https://dev.java/download/) installed on your machine and the [JAVA_HOME](https://docs.oracle.com/en/cloud/saas/enterprise-performance-management-common/diepm/epm_set_java_home_104x6dd63633_106x6dd6441c.html) environment variable set
- download [Homebrew](https://brew.sh/) (a package manager for MacOS)
- use Homebrew to install Gradle (allows you to build jar files)
  - run: `brew install gradle`
- download this repository or run: `git clone https://github.com/kevinpbaker/TetrisAI.git`
- navigate to downloaded folder in the terminal by running: `cd INSERT_DIRECTORY_PATH_WHERE_YOU_SAVED_REPO_HERE`
- run: `gradle build`
- run: `java -jar ./build/libs/TetrisAI-1.0.jar`
