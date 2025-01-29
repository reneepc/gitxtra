# 🚀 Gitxtra - Extra Git Commands

This repository provides a collection of custom Git commands to enhance your workflow.
These commands are designed to be integrated as Git aliases, so you can use them just like built-in Git commands.

🛠 Installation

1️⃣ Clone the Repository
```bash
git clone > ~/git-command
cd ~/git-command
```
# 🚀 **Gitxtra – Supercharge Your Git Workflow!**  

This repository provides a collection of custom Git commands to enhance your workflow.
These commands are designed to be integrated as Git aliases, so you can use them just like built-in Git commands.

---

## 📜 **Table of Contents**  
1. [Introduction](#introduction)  
2. [Installation](#installation)  
3. [Setting Up Aliases](#setting-up-aliases)  
4. [Available Commands](#available-commands)  
   - [📂 `git bb`](#git-bb)  
   - [🗑️ `git clean-branches`](#git-clean-branches)  
5. [Contributing](#contributing)  
6. [License](#license)  

---

## 🛠 **Installation**  

### Clone the repository
```bash
git clone https://github.com/reneepc/gitxtra.git ~/gitxtra
cd ~/gitxtra
```

### Make Scripts Executable
```bash
chmod +x *
```

### Create Aliases

To register all scripts as Git aliases, run:

```bash
./create-aliases.sh
```

This script will automatically configure Git aliases for all available commands.

## ✅ Check Aliases
Verify that aliases were added successfully:

```bash
git config --global --list | grep alias
```

## 🚀 Available Commands

### 📂 git bb


A better way to view your branches.

```bash
git bb
```

Displays all local branches with ahead/behind status, last commit date, and color-coded output for better readability.

Example Output:
```
Ahead Behind Branch                         Last Commit
----- ------ ------------------------------ -------------------
4     28     feature/add-authentication      7 days ago
21    30     fix/bug-123                     12 days ago
3     49     test/improve-ci                 1 month ago
```

### 🗑️ git clean-branches

Removes old, stale branches from your local repository.

```bash
git clean-branches
```

Identifies and removes stale branches based on inactivity, with an option to delete remote branches after confirmation.

Example run:
```
Enter the time period to consider branches as stale (e.g., 30 minutes, 2 hours, 5 days, 3 weeks, 1 year): 8 weeks
Finding branches older than 8 weeks...

The following branches are stale and will be deleted:
 🔴 feature/deprecated-feature
 🔴 fix/old-bug-456
 🔴 refactor/legacy-code

Are you sure you want to delete these branches? (y/N) y

Do you want to delete remote branches as well? (y/N) N

```
